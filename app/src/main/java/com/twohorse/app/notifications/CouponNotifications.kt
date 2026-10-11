package com.twohorse.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.twohorse.app.MainActivity
import com.twohorse.app.R
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.domain.model.MyCoupon
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

/*
 * Two notifications about the member's own saved coupons (Kuponlarım),
 * built on the phone with WorkManager, no push server:
 *  - "starts soon": REMINDER_LEAD before the coupon's first leg; tapping
 *    opens the app.
 *  - "won": once every leg has hit; tapping opens Kuponlarım.
 * A periodic check (every 15 minutes, WorkManager's minimum) reads
 * /api/my-coupons. When a coupon starts within the next hours it also
 * books a one-off check at exactly start - REMINDER_LEAD.
 */
object CouponNotifications {

    const val EXTRA_OPEN = "open"
    const val OPEN_MY_COUPONS = "my_coupons"

    internal const val CHANNEL_ID = "coupons"
    internal val REMINDER_LEAD: Duration = Duration.ofMinutes(15)
    internal val BOOK_AHEAD: Duration = Duration.ofHours(6)

    private const val PERIODIC_WORK = "coupon-watch"
    private const val NOW_WORK = "coupon-watch-now"

    fun schedule(context: Context) {
        createChannel(context)
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<CouponWatchWorker>(15, TimeUnit.MINUTES)
                .setConstraints(networkConstraint())
                .build()
        )
    }

    /* Right after a coupon is saved: its reminder may be due before the next periodic run. */
    fun checkNow(context: Context) {
        WorkManager.getInstance(context).enqueueUniqueWork(
            NOW_WORK,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<CouponWatchWorker>()
                .setConstraints(networkConstraint())
                .build()
        )
    }

    /* One booked check per start time: coupons of the same altılı share it. */
    internal fun bookReminder(context: Context, start: Instant, delay: Duration) {
        WorkManager.getInstance(context).enqueueUniqueWork(
            "coupon-reminder-${start.epochSecond}",
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<CouponWatchWorker>()
                .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
                .setConstraints(networkConstraint())
                .build()
        )
    }

    fun needsPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED

    internal fun show(
        context: Context,
        notificationId: Int,
        title: String,
        body: String,
        openMyCoupons: Boolean
    ) {
        if (needsPermission(context)) return
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return

        val intent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                if (openMyCoupons) putExtra(EXTRA_OPEN, OPEN_MY_COUPONS)
            }
        val pending =
            PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pending)
                .build()

        try {
            manager.notify(notificationId, notification)
        } catch (_: SecurityException) {
            // Permission revoked between the check and the call.
        }
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_coupons),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_coupons_desc)
            }
        )
    }

    private fun networkConstraint(): Constraints =
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
}

class CouponWatchWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val repository = TwoHorseRepository(context)
        if (!repository.hasSession) return Result.success()

        // A failed fetch waits for the next run instead of retrying in a loop.
        val coupons = repository.myCoupons().getOrNull() ?: return Result.success()

        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val reminded = prefs.getStringSet(KEY_REMINDED, emptySet()).orEmpty().toMutableSet()
        val won = prefs.getStringSet(KEY_WON, emptySet()).orEmpty().toMutableSet()

        val now = Instant.now()
        val today = LocalDate.now(ISTANBUL)

        /*
         * The "already announced" list lives on the phone, so a reinstall
         * forgot it and announced this morning's win again (11 Oct). The
         * first run after install only records the wins already there.
         */
        if (!prefs.getBoolean(KEY_SEEDED, false)) {
            coupons.filter { it.allLegsHit == true }.forEach { won += it.id.toString() }
        }

        /*
         * Several saved coupons of the same altılı start together: one
         * notification per altılı, not one per coupon (11 Oct: six
         * identical "birazdan başlıyor" for six İstanbul 1. Altılı coupons).
         * The id comes from the altılı, so a repeat run replaces it.
         */
        val freshWins =
            coupons.filter { coupon ->
                val key = coupon.id.toString()
                if (coupon.allLegsHit != true || key in won) return@filter false
                won += key
                // Only fresh wins: an app update must not announce last week's coupon.
                val raceDay = runCatching { LocalDate.parse(coupon.raceDate) }.getOrNull()
                raceDay != null && !raceDay.isBefore(today.minusDays(1))
            }

        freshWins.groupBy(::groupKey).forEach { (group, members) ->
            val first = members.first()
            CouponNotifications.show(
                context,
                notificationId = WON_BASE + Math.floorMod(group.hashCode(), ID_SPAN),
                title = context.getString(R.string.notification_coupon_won_title),
                body = withCount(context.getString(R.string.notification_coupon_won_body, label(first), first.legCount), members.size),
                openMyCoupons = true
            )
        }

        val dueReminders = mutableListOf<Pair<MyCoupon, Instant>>()

        for (coupon in coupons) {
            val key = coupon.id.toString()
            val start = coupon.startsAt?.let { runCatching { Instant.parse(it) }.getOrNull() } ?: continue
            if (key in reminded || !now.isBefore(start)) continue

            val due = start.minus(CouponNotifications.REMINDER_LEAD)
            if (!now.isBefore(due)) {
                reminded += key
                dueReminders += coupon to start
            } else if (Duration.between(now, due) <= CouponNotifications.BOOK_AHEAD) {
                CouponNotifications.bookReminder(context, start, Duration.between(now, due))
            }
        }

        dueReminders.groupBy { groupKey(it.first) }.forEach { (group, members) ->
            val (first, start) = members.first()
            val minutes = ((Duration.between(now, start).toMillis() + 59_999) / 60_000).toInt().coerceAtLeast(1)
            CouponNotifications.show(
                context,
                notificationId = REMINDER_BASE + Math.floorMod(group.hashCode(), ID_SPAN),
                title = context.getString(R.string.notification_coupon_soon_title),
                body = withCount(context.getString(R.string.notification_coupon_soon_body, label(first), minutes), members.size),
                openMyCoupons = false
            )
        }

        // Forget coupons that left the list so the sets stay small.
        val present = coupons.map { it.id.toString() }.toSet()
        prefs.edit()
            .putStringSet(KEY_REMINDED, reminded.intersect(present))
            .putStringSet(KEY_WON, won.intersect(present))
            .putBoolean(KEY_SEEDED, true)
            .apply()

        return Result.success()
    }

    private fun groupKey(coupon: MyCoupon): String =
        listOf(coupon.raceDate, coupon.city, coupon.pool, coupon.windowNumber).joinToString("|")

    private fun withCount(body: String, count: Int): String =
        if (count > 1) applicationContext.getString(R.string.notification_coupon_count, body, count) else body

    private fun label(coupon: MyCoupon): String {
        val window =
            applicationContext.getString(
                if (coupon.pool == "fivefold") R.string.coupon_history_window_fivefold
                else R.string.coupon_history_window_sixfold,
                coupon.windowNumber
            )
        return "${coupon.city} · $window"
    }

    private companion object {
        const val PREFS = "coupon_notifications"
        const val KEY_REMINDED = "reminded"
        const val KEY_WON = "won"
        const val KEY_SEEDED = "seeded"
        const val REMINDER_BASE = 10_000
        const val WON_BASE = 20_000
        const val ID_SPAN = 10_000
        val ISTANBUL: ZoneId = ZoneId.of("Europe/Istanbul")
    }
}

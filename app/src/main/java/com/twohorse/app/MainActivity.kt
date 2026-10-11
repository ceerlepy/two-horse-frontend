package com.twohorse.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import com.twohorse.app.i18n.ProvideLanguage
import com.twohorse.app.notifications.CouponNotifications
import com.twohorse.app.ui.theme.TwoHorseTheme

/*
 * AppCompatActivity (not plain ComponentActivity) so the in-app
 * language choice made through AppCompatDelegate.setApplicationLocales
 * is applied on every Android version; on Android 12 and older only
 * AppCompat activities pick it up.
 */
class MainActivity :
    AppCompatActivity() {

    /* Set when a notification asks for a screen (e.g. Kuponlarım). */
    private val openTarget =
        mutableStateOf<String?>(null)

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        openTarget.value = intent?.getStringExtra(CouponNotifications.EXTRA_OPEN)
        CouponNotifications.schedule(this)

        setContent {
            TwoHorseTheme {
                ProvideLanguage {
                    TwoHorseApp(
                        openTarget = openTarget.value,
                        onOpenTargetHandled = {
                            openTarget.value = null
                            // Recreating the activity must not reopen the screen.
                            intent?.removeExtra(CouponNotifications.EXTRA_OPEN)
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(
        intent: Intent
    ) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.getStringExtra(CouponNotifications.EXTRA_OPEN)?.let { openTarget.value = it }
    }
}

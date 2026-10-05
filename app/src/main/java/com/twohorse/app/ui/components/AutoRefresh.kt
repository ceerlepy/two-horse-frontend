package com.twohorse.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay

/*
 * How often an open screen refetches on its own, so a member who
 * never taps refresh still sees fresh AGF and expert data. Follows
 * the backend's own cadence: AGF is refreshed every 5 minutes in the
 * last 90 minutes before a race, so polling faster than every 2
 * minutes would only download the same data again.
 */
internal fun autoRefreshIntervalMillis(
    nextStartMillis: Long?,
    nowMillis: Long
): Long {
    val untilStart =
        nextStartMillis?.minus(nowMillis)

    return when {
        untilStart == null || untilStart < 0 ->
            15 * 60_000L

        untilStart <= 30 * 60_000L ->
            2 * 60_000L

        else ->
            5 * 60_000L
    }
}

/*
 * Calls onRefresh on that cadence while the screen is in the
 * foreground; the loop stops when the app goes to the background
 * (the screens already refetch on resume).
 */
@Composable
fun AutoRefreshEffect(
    nextStartMillis: Long?,
    onRefresh: () -> Unit
) {
    val lifecycleOwner =
        LocalLifecycleOwner.current

    val nextStart by
        rememberUpdatedState(nextStartMillis)

    val refresh by
        rememberUpdatedState(onRefresh)

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(
            Lifecycle.State.RESUMED
        ) {
            while (true) {
                delay(
                    autoRefreshIntervalMillis(
                        nextStart,
                        System.currentTimeMillis()
                    )
                )

                refresh()
            }
        }
    }
}

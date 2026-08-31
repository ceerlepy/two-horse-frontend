package com.twohorse.app.ui.home

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.domain.model.Race
import com.twohorse.app.domain.model.TodayData
import com.twohorse.app.i18n.LocalStrings
import com.twohorse.app.ui.components.CityChip
import com.twohorse.app.ui.components.EmptyRaceState
import com.twohorse.app.ui.components.NextRaceHero
import com.twohorse.app.ui.components.RaceCard
import com.twohorse.app.ui.components.RemainingRacesToggle
import com.twohorse.app.ui.components.SixFoldEntryCard
import com.twohorse.app.ui.components.ShimmerBlock
import com.twohorse.app.ui.components.TwoHorseHeader
import com.twohorse.app.ui.components.UpcomingRaceCard
import com.twohorse.app.ui.theme.Green
import com.twohorse.app.ui.theme.Ink
import com.twohorse.app.ui.theme.Muted
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.OffsetDateTime
import kotlin.math.max

@Composable
fun HomeScreen(
    onRaceClick: (Race) -> Unit,
    onSixFoldClick: (List<String>, String?) -> Unit = { _, _ -> },
    onHistoryClick: () -> Unit = {},
    onAccountClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val strings = LocalStrings.current

    val repository =
        remember {
            TwoHorseRepository(context)
        }

    val lifecycleOwner =
        LocalLifecycleOwner.current

    var loading by
        remember {
            mutableStateOf(true)
        }

    var refreshing by
        remember {
            mutableStateOf(false)
        }

    var requestInFlight by
        remember {
            mutableStateOf(false)
        }

    var showingStaleData by
        remember {
            mutableStateOf(false)
        }

    var data by
        remember {
            mutableStateOf<TodayData?>(
                null
            )
        }

    var error by
        remember {
            mutableStateOf<String?>(
                null
            )
        }

    var selectedCity by
        remember {
            mutableStateOf<String?>(
                null
            )
        }

    var remainingExpanded by
        remember {
            mutableStateOf(false)
        }


    var refreshKey by
        remember {
            mutableLongStateOf(0L)
        }

    var nowMillis by
        remember {
            mutableLongStateOf(
                System.currentTimeMillis()
            )
        }

    LaunchedEffect(Unit) {
        while (true) {
            nowMillis =
                System.currentTimeMillis()

            delay(
                1000
            )
        }
    }

    DisposableEffect(
        lifecycleOwner
    ) {
        var firstResume =
            true

        val observer =
            LifecycleEventObserver {
                _,
                event ->

                if (
                    event ==
                    Lifecycle.Event.ON_RESUME
                ) {
                    if (
                        firstResume
                    ) {
                        firstResume =
                            false
                    } else if (
                        !requestInFlight
                    ) {
                        refreshKey++
                    }
                }
            }

        lifecycleOwner.lifecycle.addObserver(
            observer
        )

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(
                observer
            )
        }
    }

    LaunchedEffect(
        refreshKey
    ) {
        if (
            requestInFlight
        ) {
            return@LaunchedEffect
        }

        requestInFlight =
            true

        if (
            data == null
        ) {
            loading =
                true
        } else {
            refreshing =
                true
        }

        error =
            null

        repository
            .today()
            .onSuccess {
                data =
                    it

                showingStaleData =
                    false

                val availableCities =
                    it.meetings
                        .map {
                            meeting ->
                            meeting.city
                        }

                if (
                    selectedCity !in
                    availableCities
                ) {
                    selectedCity =
                        null
                }
            }
            .onFailure {
                error = it.message

                showingStaleData =
                    data != null
            }

        loading =
            false

        refreshing =
            false

        requestInFlight =
            false
    }

    val allRaces =
        data
            ?.meetings
            .orEmpty()
            .flatMap {
                it.races
            }

    val cities =
        data
            ?.meetings
            .orEmpty()
            .map {
                it.city
            }
            .distinct()

    val filteredRaces =
        allRaces
            .filter {
                selectedCity == null ||
                it.city == selectedCity
            }
            .sortedWith(
                compareBy<Race> {
                    raceTimeMillis(
                        it
                    )
                        ?: Long.MAX_VALUE
                }
                .thenBy {
                    it.number
                }
            )

    val futureRaces =
        filteredRaces.filter {
            val time =
                raceTimeMillis(
                    it
                )

            time == null ||
            time >=
                nowMillis -
                60_000L
        }

    val nextRace =
        futureRaces
            .firstOrNull()

    LazyColumn(
        modifier =
            Modifier.fillMaxSize(),
        verticalArrangement =
            Arrangement.spacedBy(
                12.dp
            )
    ) {
        item {
            TwoHorseHeader(
                refreshing =
                    refreshing,
                onRefresh = {
                    if (
                        !requestInFlight
                    ) {
                        refreshKey++
                    }
                },
                onHistory = onHistoryClick,
                onAccount = onAccountClick
            )
        }

        if (
            showingStaleData
        ) {
            item {
                Text(
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp
                        ),
                    text =
                        strings.homeRefreshFailedStale,
                    color =
                        Muted,
                    fontSize =
                        12.sp
                )
            }
        }

        if (
            cities.isNotEmpty()
        ) {
            item {
                LazyRow(
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp
                        ),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )
                ) {
                    item {
                        CityChip(
                            city =
                                strings.homeAllCities,
                            selected =
                                selectedCity == null,
                            onClick = {
                                selectedCity =
                                    null
                            }
                        )
                    }

                    items(
                        cities
                    ) { city ->
                        CityChip(
                            city =
                                city,
                            selected =
                                selectedCity ==
                                    city,
                            onClick = {
                                selectedCity =
                                    city
                            }
                        )
                    }
                }
            }
        }

        if (
            loading
        ) {
            item {
                HomeLoadingSkeleton()
            }
        } else if (
            error != null &&
            data == null
        ) {
            item {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 18.dp,
                                vertical = 36.dp
                            ),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {
                    EmptyRaceState(
                        message =
                            error
                                ?: strings.homeDataFetchFailed
                    )

                    Button(
                        onClick = {
                            if (
                                !requestInFlight
                            ) {
                                refreshKey++
                            }
                        }
                    ) {
                        Text(
                            text =
                                strings.homeRetryButton
                        )
                    }
                }
            }
        } else if (
            filteredRaces.isEmpty()
        ) {
            item {
                EmptyRaceState(
                    message =
                        strings.homeNoRacesToShow
                )
            }
        } else {
            if (
                nextRace != null
            ) {
                item {
                    Column(
                        modifier =
                            Modifier.padding(
                                horizontal = 18.dp
                            )
                    ) {
                        NextRaceHero(
                            race = nextRace,
                            countdown =
                                countdownText(
                                    nextRace,
                                    nowMillis
                                ),
                            onClick = {
                                onRaceClick(
                                    nextRace
                                )
                            }
                        )
                    }
                }
            }

            item {
                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp
                        )
                ) {
                    SixFoldEntryCard(
                        onClick = {
                            onSixFoldClick(
                                cities,
                                selectedCity
                            )
                        }
                    )
                }
            }

            val remaining =
                futureRaces
                    .filter {
                        it !== nextRace
                    }

            val previews =
                remaining.take(2)

            if (
                previews.isNotEmpty()
            ) {
                item {
                    Column(
                        modifier =
                            Modifier.padding(
                                horizontal = 18.dp,
                                vertical = 3.dp
                            )
                    ) {
                        Text(
                            text =
                                strings.homeUpcomingRacesTitle,
                            color = Ink,
                            fontSize = 19.sp,
                            fontWeight =
                                FontWeight.ExtraBold
                        )

                        Text(
                            text =
                                strings.homeUpcomingRacesSubtitle,
                            color = Muted,
                            fontSize = 11.sp
                        )
                    }
                }

                items(
                    items = previews,
                    key = {
                        "preview-${it.city}-${it.number}"
                    }
                ) { race ->
                    Column(
                        modifier =
                            Modifier.padding(
                                horizontal = 18.dp
                            )
                    ) {
                        UpcomingRaceCard(
                            race = race,
                            time =
                                displayRaceTime(
                                    race
                                ),
                            onClick = {
                                onRaceClick(
                                    race
                                )
                            }
                        )
                    }
                }
            }

            val richRaces =
                remaining.drop(
                    previews.size
                )

            if (
                richRaces.isNotEmpty()
            ) {
                item {
                    Column(
                        modifier =
                            Modifier.padding(
                                horizontal = 18.dp
                            )
                    ) {
                        RemainingRacesToggle(
                            count =
                                richRaces.size,
                            expanded =
                                remainingExpanded,
                            onToggle = {
                                remainingExpanded =
                                    !remainingExpanded
                            }
                        )
                    }
                }

                if (
                    remainingExpanded
                ) {
                    items(
                        items = richRaces,
                        key = {
                            "rich-${it.city}-${it.number}"
                        }
                    ) { race ->
                        Column(
                            modifier =
                                Modifier.padding(
                                    horizontal = 18.dp
                                )
                        ) {
                            RaceCard(
                                race = race,
                                countdown =
                                    countdownText(
                                        race,
                                        nowMillis
                                    ),
                                time =
                                    displayRaceTime(
                                        race
                                    ),
                                onClick = {
                                    onRaceClick(
                                        race
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(
                modifier =
                    Modifier.height(
                        28.dp
                    )
            )
        }
    }
}

private fun raceTimeMillis(
    race: Race
): Long? {
    val value =
        race.startsAt
            ?.trim()
            ?.takeIf {
                it.isNotEmpty()
            }
            ?: return null

    return runCatching {
        Instant
            .parse(value)
            .toEpochMilli()
    }
        .recoverCatching {
            OffsetDateTime
                .parse(value)
                .toInstant()
                .toEpochMilli()
        }
        .getOrNull()
}

private fun displayRaceTime(
    race: Race
): String {
    val value =
        race.startsAt
            ?: return "--:--"

    return runCatching {
        OffsetDateTime
            .parse(value)
            .toLocalTime()
            .toString()
            .take(5)
    }
        .getOrElse {
            Regex(
                """\b\d{2}:\d{2}\b"""
            )
                .find(value)
                ?.value
                ?: "--:--"
        }
}

@Composable
private fun countdownText(
    race: Race,
    nowMillis: Long
): String {
    val start =
        raceTimeMillis(
            race
        )
            ?: return displayRaceTime(
                race
            )

    val seconds =
        max(
            0L,
            (
                start -
                nowMillis
            ) / 1000L
        )

    if (
        seconds <= 0
    ) {
        return LocalStrings.current.startingSoon
    }

    val hours =
        seconds / 3600

    val minutes =
        (
            seconds %
            3600
        ) / 60

    val secs =
        seconds % 60

    return if (
        hours > 0
    ) {
        "%02d:%02d:%02d"
            .format(
                hours,
                minutes,
                secs
            )
    } else {
        "%02d:%02d"
            .format(
                minutes,
                secs
            )
    }
}


@Composable
private fun HomeLoadingSkeleton() {
    val strings = LocalStrings.current

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp
                ),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {
        ShimmerBlock(
            height = 210.dp
        )

        ShimmerBlock(
            height = 80.dp
        )

        ShimmerBlock(
            height = 84.dp
        )

        ShimmerBlock(
            height = 84.dp
        )

        Text(
            text =
                strings.homeRacesPreparing,
            color = Muted,
            fontSize = 11.sp
        )
    }
}

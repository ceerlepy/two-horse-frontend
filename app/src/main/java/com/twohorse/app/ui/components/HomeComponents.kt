package com.twohorse.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.R
import com.twohorse.app.data.api.ApiException
import com.twohorse.app.domain.model.Horse
import com.twohorse.app.domain.model.Race
import com.twohorse.app.i18n.LocalStrings
import com.twohorse.app.ui.race.AskAiCardButton
import com.twohorse.app.ui.theme.*

@Composable
fun TwoHorseHeader(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onHistory: () -> Unit,
    onAccount: () -> Unit = {}
) {
    val strings = LocalStrings.current
    val compact =
        isCompactScreen()

    Surface(
        color = Bg
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        start = 18.dp,
                        end = 8.dp,
                        top = 8.dp,
                        bottom = 10.dp
                    ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Image(
                painter =
                    painterResource(
                        R.drawable.two_horse_logo
                    ),
                contentDescription =
                    strings.homeLogoDescription,
                contentScale =
                    ContentScale.Fit,
                modifier =
                    Modifier
                        .size(
                            if (compact)
                                48.dp
                            else
                                54.dp
                        )
                        .clip(
                            RoundedCornerShape(
                                16.dp
                            )
                        )
            )

            Spacer(
                Modifier.width(12.dp)
            )

            BoxWithConstraints(
                modifier =
                    Modifier.weight(1f)
            ) {
                /*
                 * The name must stay on one line whatever the phone's font
                 * size: shrink it to the space left beside the icons
                 * ("Two Horse" in Black weight is about 5.8 em wide).
                 */
                val fittedTitleSize =
                    with(LocalDensity.current) {
                        (maxWidth / 5.8f).toSp()
                    }

                val titleSize =
                    if (compact)
                        minOf(23f, fittedTitleSize.value).sp
                    else
                        minOf(27f, fittedTitleSize.value).sp

                Column {
                    Text(
                        text = "Two Horse",
                        color = Ink,
                        fontSize = titleSize,
                        fontWeight =
                            FontWeight.Black,
                        maxLines = 1,
                        softWrap = false
                    )

                    Text(
                        text =
                            if (refreshing)
                                strings.homeLiveUpdating
                            else
                                strings.homeTagline,
                        color = Muted,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(
                onClick = onAccount,
                modifier =
                    Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription =
                        strings.homeAccountDescription,
                    tint = Ink
                )
            }

            IconButton(
                onClick = onHistory,
                modifier =
                    Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.ConfirmationNumber,
                    contentDescription =
                        strings.homeHistoryDescription,
                    tint = Ink
                )
            }

            IconButton(
                onClick = onRefresh,
                enabled = !refreshing,
                modifier =
                    Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription =
                        strings.homeRefreshDescription,
                    tint =
                        if (refreshing)
                            Muted
                        else
                            Ink
                )
            }
        }
    }
}

@Composable
fun CityChip(
    city: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier =
            Modifier
                .clip(
                    RoundedCornerShape(50)
                )
                .clickable(
                    onClick = onClick
                ),
        color =
            if (selected)
                Ink
            else
                Surface,
        border =
            BorderStroke(
                1.dp,
                if (selected)
                    Ink
                else
                    Border
            ),
        shape =
            RoundedCornerShape(50)
    ) {
        Text(
            text = city,
            modifier =
                Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 8.dp
                ),
            color =
                if (selected)
                    Color.White
                else
                    Ink,
            fontSize = 11.sp,
            fontWeight =
                FontWeight.Bold
        )
    }
}

@Composable
fun NextRaceHero(
    race: Race,
    countdown: String,
    onClick: () -> Unit
) {
    val strings = LocalStrings.current
    val compact =
        isCompactScreen()

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = Ink
            ),
        shape =
            RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier =
                Modifier.padding(
                    if (compact)
                        18.dp
                    else
                        22.dp
                )
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Surface(
                    color = Gold,
                    shape =
                        RoundedCornerShape(50)
                ) {
                    Text(
                        text =
                            "$countdown ${strings.homeCountdownKaldi}",
                        modifier =
                            Modifier.padding(
                                horizontal = 11.dp,
                                vertical = 6.dp
                            ),
                        color = Ink,
                        fontSize = 10.sp,
                        fontWeight =
                            FontWeight.Black
                    )
                }

                Spacer(
                    Modifier.weight(1f)
                )

                Text(
                    text =
                        raceTime(
                            race
                        ),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight =
                        FontWeight.Black
                )
            }

            Spacer(
                Modifier.height(18.dp)
            )

            Text(
                text = strings.homeNextRaceLabel,
                color =
                    Color.White.copy(
                        alpha = 0.58f
                    ),
                fontSize = 12.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text =
                    strings.raceCityAndNumber(race.city, race.number),
                color = Color.White,
                fontSize =
                    if (compact)
                        23.sp
                    else
                        27.sp,
                fontWeight =
                    FontWeight.Black,
                maxLines = 2,
                overflow =
                    TextOverflow.Ellipsis
            )

            Text(
                text =
                    raceMeta(race),
                color =
                    Color.White.copy(
                        alpha = 0.68f
                    ),
                fontSize = 13.sp
            )

            if (
                race.title.isNotBlank()
            ) {
                Spacer(
                    Modifier.height(5.dp)
                )

                Text(
                    text = race.title,
                    color =
                        Color.White.copy(
                            alpha = 0.72f
                        ),
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow =
                        TextOverflow.Ellipsis
                )
            }

            RaceInsightSummary(
                race = race,
                dark = true
            )

            val leader =
                rankedHorses(race)
                    .firstOrNull()

            leader?.let {
                Spacer(
                    Modifier.height(13.dp)
                )

                HorizontalDivider(
                    color =
                        Color.White.copy(
                            alpha = 0.12f
                        )
                )

                Spacer(
                    Modifier.height(11.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {
                        Text(
                            text =
                                strings.homeModelFavorite,
                            color =
                                Color.White.copy(
                                    alpha = 0.56f
                                ),
                            fontSize = 9.sp,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "#${it.number} ${it.name}",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight =
                                FontWeight.Black,
                            maxLines = 1,
                            overflow =
                                TextOverflow.Ellipsis
                        )
                    }

                    it.score?.let { score ->
                        Surface(
                            color =
                                Color.White.copy(
                                    alpha = 0.10f
                                ),
                            shape = CircleShape
                        ) {
                            Text(
                                text =
                                    score.toInt()
                                        .toString(),
                                modifier =
                                    Modifier.padding(
                                        13.dp
                                    ),
                                color = Gold,
                                fontWeight =
                                    FontWeight.Black
                            )
                        }
                    }
                }
            }

            Spacer(
                Modifier.height(16.dp)
            )

            Button(
                onClick = onClick,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            Color.White,
                        contentColor = Ink
                    ),
                shape =
                    RoundedCornerShape(15.dp)
            ) {
                Text(
                    text = strings.homeOpenAnalysis,
                    fontWeight =
                        FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun UpcomingRaceCard(
    race: Race,
    time: String,
    onClick: () -> Unit
) {
    val strings = LocalStrings.current
    val compact =
        isCompactScreen()

    val favorite =
        rankedHorses(race)
            .firstOrNull()

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),
        colors =
            CardDefaults.cardColors(
                containerColor = CardTone
            ),
        border =
            BorderStroke(
                1.dp,
                CardToneBorder
            ),
        shape =
            RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier =
                Modifier.padding(15.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Column(
                modifier =
                    Modifier.width(
                        if (compact)
                            62.dp
                        else
                            70.dp
                    )
            ) {
                Text(
                    text = time,
                    color = Ink,
                    fontSize =
                        if (compact)
                            16.sp
                        else
                            18.sp,
                    fontWeight =
                        FontWeight.Black
                )

                Text(
                    text =
                        strings.homeCourseNumber(race.number),
                    color = Green,
                    fontSize = 10.sp,
                    fontWeight =
                        FontWeight.Bold
                )
            }

            Box(
                Modifier
                    .width(1.dp)
                    .height(44.dp)
                    .background(Border)
            )

            Spacer(
                Modifier.width(13.dp)
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {
                Text(
                    text =
                        strings.raceCityAndNumber(race.city, race.number),
                    color = Ink,
                    fontSize = 14.sp,
                    fontWeight =
                        FontWeight.Black,
                    maxLines = 1,
                    overflow =
                        TextOverflow.Ellipsis
                )

                Text(
                    text = raceMeta(race),
                    color = Muted,
                    fontSize = 11.sp
                )

                favorite?.let {
                    Text(
                        text =
                            strings.homeFavoritePrefix(it.number, it.name),
                        color = Green,
                        fontSize = 11.sp,
                        fontWeight =
                            FontWeight.Bold,
                        maxLines = 1,
                        overflow =
                            TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription =
                    strings.homeOpenRaceAnalysis,
                tint = Muted
            )
        }
    }
}

@Composable
fun RaceCard(
    race: Race,
    countdown: String,
    time: String,
    onClick: () -> Unit,
    /* "AI'ya sor" about this race, from the card itself. */
    onAskAi: (() -> Unit)? = null
) {
    val strings = LocalStrings.current

    val ranked =
        rankedHorses(race)

    val favorite =
        ranked.firstOrNull()

    val surprise =
        ranked
            .drop(2)
            .firstOrNull()

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),
        colors =
            CardDefaults.cardColors(
                containerColor = CardTone
            ),
        border =
            BorderStroke(
                1.dp,
                CardToneBorder
            ),
        shape =
            RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Surface(
                    color = PaleGreen,
                    shape =
                        RoundedCornerShape(9.dp)
                ) {
                    Text(
                        text =
                            strings.homeCourseNumberCaps(race.number).let { "${race.city} · $it" },
                        modifier =
                            Modifier.padding(
                                horizontal = 9.dp,
                                vertical = 6.dp
                            ),
                        color = Green,
                        fontSize = 10.sp,
                        fontWeight =
                            FontWeight.ExtraBold
                    )
                }

                Spacer(
                    Modifier.width(9.dp)
                )

                Text(
                    text = time,
                    color = Ink,
                    fontSize = 15.sp,
                    fontWeight =
                        FontWeight.Black
                )

                Spacer(
                    Modifier.width(7.dp)
                )

                Text(
                    text = countdown,
                    color = Green,
                    fontSize = 10.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    Modifier.weight(1f)
                )

                onAskAi?.let {
                    AskAiCardButton(onClick = it)
                }

                Icon(
                    imageVector =
                        Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Muted
                )
            }

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                text = raceMeta(race),
                color = Muted,
                fontSize = 11.sp
            )

            if (
                race.title.isNotBlank()
            ) {
                Spacer(
                    Modifier.height(7.dp)
                )

                Text(
                    text = race.title,
                    color = Ink,
                    fontSize = 13.sp,
                    fontWeight =
                        FontWeight.SemiBold,
                    maxLines = 2,
                    overflow =
                        TextOverflow.Ellipsis
                )
            }

            RaceInsightSummary(
                race = race
            )

            favorite?.let {
                Spacer(
                    Modifier.height(13.dp)
                )

                HorizontalDivider(
                    color = Border
                )

                Spacer(
                    Modifier.height(11.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Column(
                        Modifier.weight(1f)
                    ) {
                        Text(
                            text =
                                strings.homeModelFavorite,
                            color = Muted,
                            fontSize = 10.sp,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "#${it.number} ${it.name}",
                            color = Ink,
                            fontSize = 16.sp,
                            fontWeight =
                                FontWeight.Black,
                            maxLines = 1,
                            overflow =
                                TextOverflow.Ellipsis
                        )

                        Text(
                            text =
                                favoriteSummary(it),
                            color = Muted,
                            fontSize = 10.sp,
                            maxLines = 2,
                            overflow =
                                TextOverflow.Ellipsis
                        )
                    }

                    ScoreBadge(it)
                }

                surprise?.let { horse ->
                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Surface(
                        color = LavenderSurface,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text =
                                strings.homeSurprisePrefix(horse.number, horse.name),
                            modifier =
                                Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 5.dp
                                ),
                            color = Lavender,
                            fontSize = 12.sp,
                            fontWeight =
                                FontWeight.Bold,
                            maxLines = 1,
                            overflow =
                                TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RemainingRacesToggle(
    count: Int,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    val strings = LocalStrings.current

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onToggle
                ),
        colors =
            CardDefaults.cardColors(
                containerColor = CardTone
            ),
        border =
            BorderStroke(
                1.dp,
                CardToneBorder
            ),
        shape =
            RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier =
                Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Column(
                Modifier.weight(1f)
            ) {
                Text(
                    text =
                        strings.homeOtherRemainingRaces,
                    color = Ink,
                    fontSize = 14.sp,
                    fontWeight =
                        FontWeight.Black
                )

                Text(
                    text =
                        strings.homeUpcomingCount(count),
                    color = Muted,
                    fontSize = 11.sp
                )
            }

            Icon(
                if (expanded)
                    Icons.Default.KeyboardArrowUp
                else
                    Icons.Default.KeyboardArrowDown,
                contentDescription =
                    if (expanded)
                        strings.homeCloseOtherRaces
                    else
                        strings.homeOpenOtherRaces,
                tint = Ink
            )
        }
    }
}

@Composable
fun SixFoldEntryCard(
    onClick: () -> Unit
) {
    val strings = LocalStrings.current

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    PaleGold
            ),
        shape =
            RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier =
                Modifier.padding(16.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Surface(
                color = Gold,
                shape =
                    RoundedCornerShape(14.dp)
            ) {
                Icon(
                    Icons.Default.Stars,
                    contentDescription = null,
                    tint = Color.White,
                    modifier =
                        Modifier.padding(11.dp)
                )
            }

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(start = 13.dp)
            ) {
                Text(
                    text = strings.homeSixfoldTitle,
                    color = Ink,
                    fontSize = 16.sp,
                    fontWeight =
                        FontWeight.Black
                )

                Text(
                    text =
                        strings.homeSixfoldSubtitle,
                    color = Muted,
                    fontSize = 12.sp
                )
            }

            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription =
                    strings.homeOpenSixfold,
                tint = Gold
            )
        }
    }
}

@Composable
fun ForeignEntryCard(
    onClick: () -> Unit
) {
    val strings = LocalStrings.current

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    PaleGreen
            ),
        shape =
            RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier =
                Modifier.padding(16.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Surface(
                color = Green,
                shape =
                    RoundedCornerShape(14.dp)
            ) {
                Icon(
                    Icons.Default.Public,
                    contentDescription = null,
                    tint = Color.White,
                    modifier =
                        Modifier.padding(11.dp)
                )
            }

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(start = 13.dp)
            ) {
                Text(
                    text = strings.homeForeignTitle,
                    color = Ink,
                    fontSize = 16.sp,
                    fontWeight =
                        FontWeight.Black
                )

                Text(
                    text =
                        strings.homeForeignSubtitle,
                    color = Muted,
                    fontSize = 12.sp
                )
            }

            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription =
                    strings.homeOpenForeign,
                tint = Green
            )
        }
    }
}

@Composable
fun EmptyRaceState(
    message: String
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 48.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Text(
            text = "Two Horse",
            style =
                MaterialTheme.typography
                    .titleMedium,
            color = Ink,
            fontWeight =
                FontWeight.Black
        )

        Spacer(
            Modifier.height(6.dp)
        )

        Text(
            text = message,
            color = Muted,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun ScoreBadge(
    horse: Horse
) {
    Surface(
        color = PaleGreen,
        shape = CircleShape
    ) {
        Text(
            text =
                horse.score
                    ?.toInt()
                    ?.toString()
                    ?: "—",
            modifier =
                Modifier.padding(14.dp),
            color = Green,
            fontSize = 15.sp,
            fontWeight =
                FontWeight.Black
        )
    }
}

private fun rankedHorses(
    race: Race
): List<Horse> =
    race.horses
        .sortedWith(
            compareByDescending<Horse> {
                it.score
                    ?: Double.NEGATIVE_INFINITY
            }
                .thenByDescending {
                    it.agfPercent
                        ?: Double.NEGATIVE_INFINITY
                }
                .thenBy {
                    it.number
                }
        )

@Composable
private fun favoriteSummary(
    horse: Horse
): String {
    val strings = LocalStrings.current

    return buildList {
        horse.agfPercent?.let {
            add(strings.homeAgf("%.1f".format(it)))
        }

        horse.expertConsensus
            ?.sourceCount
            ?.takeIf {
                it > 0
            }
            ?.let {
                add(strings.homeExpertSourceLabel(it))
            }

        horse.hp?.let {
            add(strings.homeHpLabel(it))
        }
    }
        .joinToString(" · ")
        .ifBlank {
            strings.homeFavoriteSummaryFallback
        }
}

@Composable
private fun raceMeta(
    race: Race
): String {
    val strings = LocalStrings.current

    return listOf(
        race.distance
            .takeIf {
                it.isNotBlank()
            },
        race.surface
            .takeIf {
                it.isNotBlank()
            }
    )
        .filterNotNull()
        .joinToString(" · ")
        .ifBlank {
            strings.raceInfoFallback
        }
}

private fun raceTime(
    race: Race
): String {
    val value =
        race.startsAt
            ?: return "--:--"

    // starts_at is UTC ("2026-10-06T09:30:00Z"); show Turkey time.
    return runCatching {
        java.time.OffsetDateTime
            .parse(value)
            .atZoneSameInstant(
                java.time.ZoneId.of("Europe/Istanbul")
            )
            .toLocalTime()
            .toString()
            .take(5)
    }
        .getOrElse {
            Regex("""\d{2}:\d{2}""")
                .find(value)
                ?.value
                ?: "--:--"
        }
}

private enum class AppErrorKind { NoInternet, Timeout, Server, Generic }

private fun appErrorKind(error: Throwable): AppErrorKind {
    val message = error.message.orEmpty()

    return when {
        error is java.net.UnknownHostException ||
            error is java.net.ConnectException ||
            message.contains("Unable to resolve host", ignoreCase = true) ->
            AppErrorKind.NoInternet

        error is java.io.InterruptedIOException ||
            message.contains("timeout", ignoreCase = true) ->
            AppErrorKind.Timeout

        (error as? ApiException)?.let { it.statusCode >= 500 } == true ->
            AppErrorKind.Server

        else ->
            AppErrorKind.Generic
    }
}

/*
 * Friendly full-width error state: an illustration, a plain-language
 * title and message (never the raw exception text) and a retry button.
 */
@Composable
fun AppErrorState(
    error: Throwable?,
    onRetry: () -> Unit
) {
    val strings = LocalStrings.current

    val kind =
        error?.let(::appErrorKind)
            ?: AppErrorKind.Generic

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Box(
            modifier =
                Modifier
                    .size(112.dp)
                    .background(PaleGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier =
                    Modifier
                        .size(76.dp)
                        .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector =
                        if (kind == AppErrorKind.NoInternet)
                            Icons.Filled.WifiOff
                        else
                            Icons.Filled.CloudOff,
                    contentDescription = null,
                    tint = Green,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = strings.errorTitle,
            style = MaterialTheme.typography.titleMedium,
            color = Ink,
            fontWeight = FontWeight.Black,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text =
                when (kind) {
                    AppErrorKind.NoInternet -> strings.errorNoInternet
                    AppErrorKind.Timeout -> strings.errorTimeout
                    AppErrorKind.Server -> strings.errorServer
                    AppErrorKind.Generic -> strings.errorGeneric
                },
            color = Muted,
            fontSize = 13.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(22.dp))

        Button(
            onClick = onRetry,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Green
                )
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = strings.homeRetryButton,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/*
 * "Türkiye | Yurt dışı" switch at the top of the home and foreign
 * screens, like browser tabs: one tap moves between the two cards.
 */
@Composable
fun RegionTabs(
    foreignSelected: Boolean,
    onSelect: (foreign: Boolean) -> Unit
) {
    val strings = LocalStrings.current

    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
        color = CardTone,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, CardToneBorder)
    ) {
        Row(
            modifier = Modifier.padding(4.dp)
        ) {
            listOf(false to strings.regionTabTurkey, true to strings.regionTabForeign)
                .forEach { (foreign, label) ->
                    val selected = foreign == foreignSelected

                    Surface(
                        modifier =
                            Modifier
                                .weight(1f)
                                .clickable(enabled = !selected) { onSelect(foreign) },
                        color = if (selected) Green else Color.Transparent,
                        shape = RoundedCornerShape(11.dp)
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = if (selected) Color.White else Ink,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
        }
    }
}

/*
 * One notice above the cards while TJK has not opened AGF (overnight
 * until race morning): the scores below are built on missing data.
 */
@Composable
fun AgfPendingNotice() {
    val strings = LocalStrings.current

    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
        color = PaleRed,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Red.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = Red,
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = strings.raceAgfPendingNote,
                color = Red,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp
            )
        }
    }
}

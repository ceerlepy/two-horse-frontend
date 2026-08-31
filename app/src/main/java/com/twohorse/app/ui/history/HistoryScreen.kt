package com.twohorse.app.ui.history

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.domain.model.HistoryRace
import com.twohorse.app.ui.theme.Border
import com.twohorse.app.ui.theme.Green
import com.twohorse.app.ui.theme.Ink
import com.twohorse.app.ui.theme.Muted
import com.twohorse.app.ui.theme.PaleGreen
import com.twohorse.app.ui.theme.PaleRed
import com.twohorse.app.ui.theme.Red
import com.twohorse.app.ui.theme.Surface

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onRaceClick: (HistoryRace) -> Unit
) {
    BackHandler(
        onBack = onBack
    )

    val context = LocalContext.current

    val repository =
        remember {
            TwoHorseRepository(context)
        }

    var loading by
        remember {
            mutableStateOf(true)
        }

    var history by
        remember {
            mutableStateOf<List<HistoryRace>>(
                emptyList()
            )
        }

    var error by
        remember {
            mutableStateOf<String?>(
                null
            )
        }

    LaunchedEffect(Unit) {
        repository
            .history()
            .onSuccess {
                history = it
            }
            .onFailure {
                error =
                    it.message
                        ?: "Geçmiş alınamadı"
            }

        loading = false
    }

    LazyColumn(
        modifier =
            Modifier.fillMaxSize(),
        verticalArrangement =
            Arrangement.spacedBy(
                10.dp
            )
    ) {
        item {
            HistoryHeader(
                onBack = onBack
            )
        }

        when {
            loading -> {
                item {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 70.dp
                                ),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Green
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    12.dp
                                )
                        )

                        Text(
                            text =
                                "Geçmiş yarışlar yükleniyor",
                            color =
                                Muted,
                            fontSize =
                                12.sp
                        )
                    }
                }
            }

            error != null -> {
                item {
                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 18.dp
                                ),
                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    PaleRed
                            ),
                        shape =
                            RoundedCornerShape(
                                16.dp
                            )
                    ) {
                        Text(
                            text =
                                error
                                    ?: "Geçmiş alınamadı",
                            modifier =
                                Modifier.padding(
                                    14.dp
                                ),
                            color =
                                Red,
                            fontSize =
                                12.sp,
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }
            }

            history.isEmpty() -> {
                item {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 70.dp
                                ),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.History,
                            contentDescription =
                                null,
                            tint =
                                Muted
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    8.dp
                                )
                        )

                        Text(
                            text =
                                "Henüz tamamlanmış yarış yok",
                            color =
                                Ink,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "Başlayan yarışların snapshotları burada görünür.",
                            color =
                                Muted,
                            fontSize =
                                11.sp
                        )
                    }
                }
            }

            else -> {
                item {
                    Column(
                        modifier =
                            Modifier.padding(
                                horizontal =
                                    18.dp
                            )
                    ) {
                        HistoryStats(
                            history =
                                history
                        )
                    }
                }

                item {
                    Column(
                        modifier =
                            Modifier.padding(
                                horizontal = 18.dp
                            )
                    ) {
                        Text(
                            text =
                                "Geçmiş",
                            color =
                                Ink,
                            fontSize =
                                19.sp,
                            fontWeight =
                                FontWeight.ExtraBold
                        )

                        Text(
                            text =
                                "${history.size} dondurulmuş yarış snapshotı",
                            color =
                                Muted,
                            fontSize =
                                11.sp
                        )
                    }
                }

                items(
                    items =
                        history,
                    key = {
                        "${it.raceDate}-${it.city}-${it.raceNumber}"
                    }
                ) { race ->
                    Column(
                        modifier =
                            Modifier.padding(
                                horizontal = 18.dp
                            )
                    ) {
                        HistoryRaceCard(
                            race =
                                race,
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

@Composable
private fun HistoryHeader(
    onBack: () -> Unit
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp,
                    vertical = 8.dp
                ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack
        ) {
            Icon(
                imageVector =
                    Icons.Default.ArrowBack,
                contentDescription =
                    "Geri",
                tint =
                    Ink
            )
        }

        Text(
            text =
                "Geçmiş Yarışlar",
            color =
                Ink,
            fontSize =
                19.sp,
            fontWeight =
                FontWeight.ExtraBold
        )
    }
}

@Composable
private fun HistoryRaceCard(
    race: HistoryRace,
    onClick: () -> Unit
) {
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
                    Surface
            ),
        shape =
            RoundedCornerShape(
                18.dp
            ),
        border =
            BorderStroke(
                1.dp,
                Border
            )
    ) {
        Row(
            modifier =
                Modifier.padding(
                    15.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Surface(
                color =
                    PaleGreen,
                shape =
                    RoundedCornerShape(
                        13.dp
                    )
            ) {
                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 11.dp,
                            vertical = 9.dp
                        ),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {
                    Text(
                        text =
                            race.startTime
                                .ifBlank {
                                    "--:--"
                                },
                        color =
                            Green,
                        fontSize =
                            13.sp,
                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Text(
                        text =
                            "${race.raceNumber}. K",
                        color =
                            Green,
                        fontSize =
                            9.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(
                            start = 12.dp
                        )
            ) {
                Text(
                    text =
                        "${race.city} · ${race.raceDate}",
                    color =
                        Ink,
                    fontSize =
                        15.sp,
                    fontWeight =
                        FontWeight.ExtraBold,
                    maxLines =
                        1,
                    overflow =
                        TextOverflow.Ellipsis
                )

                Text(
                    text =
                        listOfNotNull(
                            race.distanceMeters
                                ?.let {
                                    "$it m"
                                },
                            race.track
                                .takeIf {
                                    it.isNotBlank()
                                }
                        )
                            .joinToString(
                                " · "
                            )
                            .ifBlank {
                                race.raceDate
                            },
                    color =
                        Muted,
                    fontSize =
                        11.sp
                )

                Text(
                    text =
                        "${race.runners.size} at · " +
                        "${race.expertPredictionCount} uzman satırı",
                    color =
                        Muted,
                    fontSize =
                        10.sp
                )
            }

            Icon(
                imageVector =
                    Icons.Default.KeyboardArrowRight,
                contentDescription =
                    null,
                tint =
                    Muted
            )
        }
    }
}


@Composable
private fun HistoryStats(
    history: List<HistoryRace>
) {
    val completed =
        history.filter {
            race ->
            race.runners.any {
                it.finishPosition != null
            }
        }

    val top1Hits =
        completed.count {
            race ->
            val ranked =
                race.runners
                    .sortedByDescending {
                        it.score
                            ?: Double.NEGATIVE_INFINITY
                    }

            val winner =
                race.runners
                    .firstOrNull {
                        it.finishPosition == 1
                    }

            winner != null &&
            ranked.firstOrNull()
                ?.number ==
                winner.number
        }

    val top3Hits =
        completed.count {
            race ->
            val top3 =
                race.runners
                    .sortedByDescending {
                        it.score
                            ?: Double.NEGATIVE_INFINITY
                    }
                    .take(3)
                    .map {
                        it.number
                    }

            val winner =
                race.runners
                    .firstOrNull {
                        it.finishPosition == 1
                    }

            winner != null &&
            winner.number in
                top3
        }

    val top1Percent =
        if (completed.isEmpty())
            0
        else
            (
                top1Hits * 100.0 /
                completed.size
            ).toInt()

    val top3Percent =
        if (completed.isEmpty())
            0
        else
            (
                top3Hits * 100.0 /
                completed.size
            ).toInt()

    val cityStats =
        completed
            .groupBy {
                it.city
            }
            .map {
                entry ->

                val hits =
                    entry.value.count {
                        race ->

                        val model =
                            race.runners
                                .maxByOrNull {
                                    it.score
                                        ?: Double.NEGATIVE_INFINITY
                                }

                        val winner =
                            race.runners
                                .firstOrNull {
                                    it.finishPosition ==
                                        1
                                }

                        model != null &&
                        winner != null &&
                        model.number ==
                            winner.number
                    }

                Triple(
                    entry.key,
                    hits,
                    entry.value.size
                )
            }
            .sortedByDescending {
                if (it.third == 0)
                    0.0
                else
                    it.second.toDouble() /
                    it.third
            }

    Card(
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Surface
            ),
        border =
            BorderStroke(
                1.dp,
                Border
            ),
        shape =
            RoundedCornerShape(
                20.dp
            )
    ) {
        Column(
            modifier =
                Modifier.padding(
                    15.dp
                )
        ) {
            Text(
                text =
                    "Model performansı",
                color =
                    Ink,
                fontSize =
                    15.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )

            Text(
                text =
                    "${completed.size} sonuçlanmış yarış üzerinden",
                color =
                    Muted,
                fontSize =
                    9.sp
            )

            Spacer(
                Modifier.height(
                    10.dp
                )
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(
                        7.dp
                    )
            ) {
                PerformanceMetric(
                    modifier =
                        Modifier.weight(
                            1f
                        ),
                    label =
                        "Top-1 isabet",
                    value =
                        "%$top1Percent"
                )

                PerformanceMetric(
                    modifier =
                        Modifier.weight(
                            1f
                        ),
                    label =
                        "Top-3 kapsama",
                    value =
                        "%$top3Percent"
                )

                PerformanceMetric(
                    modifier =
                        Modifier.weight(
                            1f
                        ),
                    label =
                        "Yarış",
                    value =
                        completed.size
                            .toString()
                )
            }

            if (
                cityStats.isNotEmpty()
            ) {
                Spacer(
                    Modifier.height(
                        13.dp
                    )
                )

                Text(
                    text =
                        "Şehir bazlı Top-1",
                    color =
                        Ink,
                    fontSize =
                        11.sp,
                    fontWeight =
                        FontWeight.ExtraBold
                )

                Spacer(
                    Modifier.height(
                        6.dp
                    )
                )

                cityStats
                    .take(5)
                    .forEach {
                        item ->

                        val percent =
                            if (
                                item.third == 0
                            )
                                0
                            else
                                (
                                    item.second *
                                    100.0 /
                                    item.third
                                )
                                    .toInt()

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        vertical =
                                            3.dp
                                    )
                        ) {
                            Text(
                                text =
                                    item.first,
                                modifier =
                                    Modifier.weight(
                                        1f
                                    ),
                                color =
                                    Muted,
                                fontSize =
                                    9.sp
                            )

                            Text(
                                text =
                                    "${item.second}/${item.third} · %$percent",
                                color =
                                    Green,
                                fontSize =
                                    9.sp,
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
            }
        }
    }
}

@Composable
private fun PerformanceMetric(
    modifier: Modifier,
    label: String,
    value: String
) {
    Surface(
        modifier =
            modifier,
        color =
            PaleGreen,
        shape =
            RoundedCornerShape(
                12.dp
            )
    ) {
        Column(
            modifier =
                Modifier.padding(
                    9.dp
                )
        ) {
            Text(
                text =
                    label,
                color =
                    Muted,
                fontSize =
                    8.sp
            )

            Text(
                text =
                    value,
                color =
                    Green,
                fontSize =
                    16.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )
        }
    }
}

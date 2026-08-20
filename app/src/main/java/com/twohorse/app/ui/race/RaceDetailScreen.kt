package com.twohorse.app.ui.race

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.domain.model.Horse
import com.twohorse.app.domain.model.Race
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.ui.theme.Border
import com.twohorse.app.ui.theme.Gold
import com.twohorse.app.ui.theme.Green
import com.twohorse.app.ui.theme.Ink
import com.twohorse.app.ui.theme.Muted
import com.twohorse.app.ui.theme.PaleGold
import com.twohorse.app.ui.theme.PaleGreen
import com.twohorse.app.ui.theme.Surface
import kotlin.math.roundToInt

@Composable
fun RaceDetailScreen(
    race: Race,
    onBack: () -> Unit
) {
    BackHandler(
        onBack = onBack
    )

    val repository =
        remember {
            TwoHorseRepository()
        }

    var currentRace by
        remember(
            race.city,
            race.number
        ) {
            mutableStateOf(
                race
            )
        }

    var refreshing by
        remember {
            mutableStateOf(
                false
            )
        }

    var refreshError by
        remember {
            mutableStateOf<String?>(
                null
            )
        }

    var refreshKey by
        remember {
            mutableStateOf(
                0
            )
        }

    LaunchedEffect(
        refreshKey,
        race.city,
        race.number
    ) {
        if (
            refreshing
        ) {
            return@LaunchedEffect
        }

        refreshing =
            true

        refreshError =
            null

        repository
            .today()
            .onSuccess {
                today ->

                val freshRace =
                    today.meetings
                        .asSequence()
                        .flatMap {
                            meeting ->
                            meeting.races
                                .asSequence()
                        }
                        .firstOrNull {
                            candidate ->
                            candidate.city ==
                                race.city &&
                            candidate.number ==
                                race.number
                        }

                if (
                    freshRace != null
                ) {
                    currentRace =
                        freshRace
                } else {
                    refreshError =
                        "Yarış güncel programda bulunamadı. Son bilinen veri gösteriliyor."
                }
            }
            .onFailure {
                refreshError =
                    "Yarış yenilenemedi. Son bilinen veri gösteriliyor."
            }

        refreshing =
            false
    }

    val rankedHorses =
        currentRace.horses
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

    LazyColumn(
        modifier =
            Modifier.fillMaxSize(),
        verticalArrangement =
            Arrangement.spacedBy(
                10.dp
            )
    ) {
        item {
            RaceDetailHeader(
                race =
                    currentRace,
                refreshing =
                    refreshing,
                onRefresh = {
                    if (
                        !refreshing
                    ) {
                        refreshKey++
                    }
                },
                onBack =
                    onBack
            )
        }

        if (
            refreshError != null
        ) {
            item {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 18.dp
                            ),
                    color =
                        PaleGold,
                    shape =
                        RoundedCornerShape(
                            14.dp
                        )
                ) {
                    Text(
                        text =
                            refreshError
                                ?: "",
                        modifier =
                            Modifier.padding(
                                12.dp
                            ),
                        color =
                            Muted,
                        fontSize =
                            11.sp,
                        fontWeight =
                            FontWeight.SemiBold
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
                RaceSummaryCard(
                    race =
                        currentRace,
                    rankedHorses =
                        rankedHorses
                )
            }
        }

        if (
            rankedHorses.isEmpty()
        ) {
            item {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 18.dp
                            ),
                    color =
                        Surface,
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
                    Column(
                        modifier =
                            Modifier.padding(
                                20.dp
                            ),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {
                        Text(
                            text =
                                "At verisi bulunamadı",
                            color =
                                Ink,
                            fontWeight =
                                FontWeight.ExtraBold
                        )

                        Text(
                            text =
                                "Backend bu yarış için runner verisi döndürmedi.",
                            color =
                                Muted,
                            fontSize =
                                11.sp
                        )
                    }
                }
            }
        } else {
            item {
                Column(
                    modifier =
                        Modifier.padding(
                            start = 18.dp,
                            end = 18.dp,
                            top = 8.dp
                        )
                ) {
                    Text(
                        text =
                            "Tüm atlar",
                        color =
                            Ink,
                        fontSize =
                            19.sp,
                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Text(
                        text =
                            "Model puanı, güven ve temel yarış verileri",
                        color =
                            Muted,
                        fontSize =
                            12.sp
                    )
                }
            }

            items(
                items =
                    rankedHorses,
                key = {
                    it.number
                }
            ) { horse ->
                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp
                        )
                ) {
                    HorseAnalysisCard(
                        horse =
                            horse,
                        rank =
                            rankedHorses
                                .indexOf(
                                    horse
                                ) +
                            1
                    )
                }
            }
        }

        item {
            Spacer(
                modifier =
                    Modifier.height(
                        30.dp
                    )
            )
        }
    }
}

@Composable
private fun RaceDetailHeader(
    race: Race,
    refreshing: Boolean,
    onRefresh: () -> Unit,
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
            onClick =
                onBack
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

        Column(
            modifier =
                Modifier.weight(
                    1f
                )
        ) {
            Text(
                text =
                    "${race.city} · ${race.number}. Koşu",
                color =
                    Ink,
                fontSize =
                    18.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )

            Text(
                text =
                    raceMeta(
                        race
                    ),
                color =
                    Muted,
                fontSize =
                    12.sp
            )
        }

        IconButton(
            onClick =
                onRefresh,
            enabled =
                !refreshing
        ) {
            if (
                refreshing
            ) {
                CircularProgressIndicator(
                    modifier =
                        Modifier.padding(
                            9.dp
                        ),
                    color =
                        Green,
                    strokeWidth =
                        2.dp
                )
            } else {
                Icon(
                    imageVector =
                        Icons.Default.Refresh,
                    contentDescription =
                        "Yarışı yenile",
                    tint =
                        Green
                )
            }
        }
    }
}

@Composable
private fun RaceSummaryCard(
    race: Race,
    rankedHorses: List<Horse>
) {
    val leader =
        rankedHorses.firstOrNull()

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                20.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Green
            )
    ) {
        Column(
            modifier =
                Modifier.padding(
                    18.dp
                )
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Surface(
                    color =
                        PaleGold,
                    shape =
                        RoundedCornerShape(
                            50
                        )
                ) {
                    Row(
                        modifier =
                            Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.Stars,
                            contentDescription =
                                null,
                            tint =
                                Gold,
                            modifier =
                                Modifier.padding(
                                    end = 4.dp
                                )
                        )

                        Text(
                            text =
                                "MODEL ANALİZİ",
                            color =
                                Gold,
                            fontSize =
                                10.sp,
                            fontWeight =
                                FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )

                Text(
                    text =
                        "${race.horses.size} at",
                    color =
                        Color.White,
                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )

            if (
                race.title.isNotBlank()
            ) {
                Text(
                    text =
                        race.title,
                    color =
                        Color.White,
                    fontSize =
                        18.sp,
                    fontWeight =
                        FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow =
                        TextOverflow.Ellipsis
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            5.dp
                        )
                )
            }

            Text(
                text =
                    raceMeta(
                        race
                    ),
                color =
                    Color.White.copy(
                        alpha = 0.82f
                    ),
                fontSize =
                    12.sp
            )

            if (
                leader != null
            ) {
                Spacer(
                    modifier =
                        Modifier.height(
                            16.dp
                        )
                )

                Text(
                    text =
                        "Model lideri",
                    color =
                        Color.White.copy(
                            alpha = 0.75f
                        ),
                    fontSize =
                        11.sp
                )

                Text(
                    text =
                        "#${leader.number} ${leader.name}",
                    color =
                        Color.White,
                    fontSize =
                        19.sp,
                    fontWeight =
                        FontWeight.ExtraBold
                )

                leader.score?.let {
                    Text(
                        text =
                            "Puan ${formatScore(it)}",
                        color =
                            Color.White,
                        fontSize =
                            13.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun HorseAnalysisCard(
    horse: Horse,
    rank: Int
) {
    val accent =
        when (rank) {
            1 ->
                Green

            2, 3 ->
                Gold

            else ->
                Muted
        }

    val label =
        when (rank) {
            1 ->
                "Favori"

            2, 3 ->
                "Güçlü"

            else ->
                "Takip"
        }

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                18.dp
            ),
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
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    0.dp
            )
    ) {
        Column(
            modifier =
                Modifier.padding(
                    15.dp
                )
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Surface(
                    color =
                        if (rank == 1)
                            PaleGreen
                        else
                            PaleGold,
                    shape =
                        RoundedCornerShape(
                            13.dp
                        )
                ) {
                    Text(
                        text =
                            horse.number
                                .toString(),
                        modifier =
                            Modifier.padding(
                                horizontal = 13.dp,
                                vertical = 10.dp
                            ),
                        color =
                            accent,
                        fontSize =
                            17.sp,
                        fontWeight =
                            FontWeight.ExtraBold
                    )
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
                            horse.name,
                        color =
                            Ink,
                        fontSize =
                            16.sp,
                        fontWeight =
                            FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow =
                            TextOverflow.Ellipsis
                    )

                    if (
                        horse.jockey.isNotBlank()
                    ) {
                        Text(
                            text =
                                horse.jockey,
                            color =
                                Muted,
                            fontSize =
                                11.sp,
                            maxLines = 1,
                            overflow =
                                TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    color =
                        if (rank == 1)
                            PaleGreen
                        else
                            PaleGold,
                    shape =
                        RoundedCornerShape(
                            50
                        )
                ) {
                    Text(
                        text =
                            label,
                        modifier =
                            Modifier.padding(
                                horizontal = 9.dp,
                                vertical = 5.dp
                            ),
                        color =
                            accent,
                        fontSize =
                            10.sp,
                        fontWeight =
                            FontWeight.ExtraBold
                    )
                }
            }

            horse.score?.let {
                Spacer(
                    modifier =
                        Modifier.height(
                            14.dp
                        )
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text =
                            "Model puanı",
                        color =
                            Muted,
                        fontSize =
                            11.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.weight(1f)
                    )

                    Text(
                        text =
                            formatScore(it),
                        color =
                            Ink,
                        fontSize =
                            15.sp,
                        fontWeight =
                            FontWeight.ExtraBold
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(
                            6.dp
                        )
                )

                LinearProgressIndicator(
                    progress = {
                        (
                            it /
                            100.0
                        )
                            .coerceIn(
                                0.0,
                                1.0
                            )
                            .toFloat()
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    color =
                        accent
                )
            }

            Spacer(
                modifier =
                    Modifier.height(
                        13.dp
                    )
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(
                        7.dp
                    )
            ) {
                StatChip(
                    modifier =
                        Modifier.weight(1f),
                    title =
                        "Güven",
                    value =
                        horse.confidence
                            ?.let {
                                confidenceLabel(
                                    it
                                )
                            }
                            ?: "—"
                )

                StatChip(
                    modifier =
                        Modifier.weight(1f),
                    title =
                        "AGF",
                    value =
                        horse.agfPercent
                            ?.let {
                                "${formatOne(it)}%"
                            }
                            ?: "—"
                )

                StatChip(
                    modifier =
                        Modifier.weight(1f),
                    title =
                        "HP",
                    value =
                        horse.hp
                            ?.toString()
                            ?: "—"
                )
            }

            Spacer(
                modifier =
                    Modifier.height(
                        7.dp
                    )
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(
                        7.dp
                    )
            ) {
                StatChip(
                    modifier =
                        Modifier.weight(1f),
                    title =
                        "Kilo",
                    value =
                        horse.weight
                            ?.let {
                                formatOne(it)
                            }
                            ?: "—"
                )

                StatChip(
                    modifier =
                        Modifier.weight(2f),
                    title =
                        "Form",
                    value =
                        horse.recentForm
                            .ifBlank {
                                "—"
                            }
                )
            }
        }
    }
}

@Composable
private fun StatChip(
    modifier: Modifier,
    title: String,
    value: String
) {
    Surface(
        modifier =
            modifier,
        color =
            Color(
                0xFFF7F9F8
            ),
        shape =
            RoundedCornerShape(
                12.dp
            )
    ) {
        Column(
            modifier =
                Modifier.padding(
                    horizontal = 9.dp,
                    vertical = 8.dp
                )
        ) {
            Text(
                text =
                    title,
                color =
                    Muted,
                fontSize =
                    9.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text =
                    value,
                color =
                    Ink,
                fontSize =
                    12.sp,
                fontWeight =
                    FontWeight.ExtraBold,
                maxLines = 1,
                overflow =
                    TextOverflow.Ellipsis
            )
        }
    }
}

private fun confidenceLabel(
    value: Double
): String {
    val percent =
        if (value <= 1.0)
            value * 100.0
        else
            value

    return "${percent.roundToInt()}%"
}

private fun formatScore(
    value: Double
): String =
    if (
        value ==
        value.roundToInt()
            .toDouble()
    ) {
        value
            .roundToInt()
            .toString()
    } else {
        "%.1f"
            .format(value)
    }

private fun formatOne(
    value: Double
): String =
    "%.1f"
        .format(value)

private fun raceMeta(
    race: Race
): String =
    listOf(
        race.distance,
        race.surface
    )
        .filter {
            it.isNotBlank()
        }
        .joinToString(
            " · "
        )
        .ifBlank {
            "Koşu bilgisi"
        }

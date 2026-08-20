package com.twohorse.app.ui.history

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.domain.model.HistoryRace
import com.twohorse.app.domain.model.Horse
import com.twohorse.app.ui.theme.Border
import com.twohorse.app.ui.theme.Green
import com.twohorse.app.ui.theme.Ink
import com.twohorse.app.ui.theme.Muted
import com.twohorse.app.ui.theme.PaleGreen
import com.twohorse.app.ui.theme.Surface

@Composable
fun HistoryDetailScreen(
    historyRace: HistoryRace,
    onBack: () -> Unit
) {
    BackHandler(
        onBack = onBack
    )

    LazyColumn(
        modifier =
            Modifier.fillMaxSize(),
        verticalArrangement =
            Arrangement.spacedBy(
                9.dp
            )
    ) {
        item {
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

                Column {
                    Text(
                        text =
                            "${historyRace.city} · ${historyRace.raceNumber}. Koşu",
                        color =
                            Ink,
                        fontSize =
                            18.sp,
                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Text(
                        text =
                            "${historyRace.raceDate} · ${historyRace.startTime}",
                        color =
                            Muted,
                        fontSize =
                            11.sp
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
                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                PaleGreen
                        ),
                    shape =
                        RoundedCornerShape(
                            18.dp
                        )
                ) {
                    Column(
                        modifier =
                            Modifier.padding(
                                16.dp
                            )
                    ) {
                        Text(
                            text =
                                "Yarış öncesi dondurulmuş snapshot",
                            color =
                                Green,
                            fontWeight =
                                FontWeight.ExtraBold
                        )

                        Text(
                            text =
                                "${historyRace.runners.size} at · " +
                                "${historyRace.expertPredictionCount} uzman kaydı",
                            color =
                                Muted,
                            fontSize =
                                11.sp
                        )

                        historyRace.finalizedAt
                            ?.let {
                                Spacer(
                                    modifier =
                                        Modifier.height(
                                            6.dp
                                        )
                                )

                                Text(
                                    text =
                                        "Dondurulma: $it",
                                    color =
                                        Muted,
                                    fontSize =
                                        9.sp
                                )
                            }
                    }
                }
            }
        }

        item {
            Text(
                text =
                    "Snapshot atları",
                modifier =
                    Modifier.padding(
                        horizontal = 18.dp,
                        vertical = 5.dp
                    ),
                color =
                    Ink,
                fontSize =
                    17.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )
        }

        items(
            items =
                historyRace.runners,
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
                SnapshotHorseCard(
                    horse = horse
                )
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
private fun SnapshotHorseCard(
    horse: Horse
) {
    Card(
        modifier =
            Modifier.fillMaxWidth(),
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
                16.dp
            )
    ) {
        Row(
            modifier =
                Modifier.padding(
                    14.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Surface(
                color =
                    PaleGreen,
                shape =
                    RoundedCornerShape(
                        11.dp
                    )
            ) {
                Text(
                    text =
                        horse.number
                            .toString(),
                    modifier =
                        Modifier.padding(
                            horizontal = 11.dp,
                            vertical = 8.dp
                        ),
                    color =
                        Green,
                    fontWeight =
                        FontWeight.ExtraBold
                )
            }

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(
                            start = 11.dp
                        )
            ) {
                Text(
                    text =
                        horse.name,
                    color =
                        Ink,
                    fontWeight =
                        FontWeight.ExtraBold,
                    fontSize =
                        14.sp
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
                            10.sp
                    )
                }

                Text(
                    text =
                        listOfNotNull(
                            horse.agfPercent
                                ?.let {
                                    "AGF %.1f%%"
                                        .format(it)
                                },
                            horse.hp
                                ?.let {
                                    "HP $it"
                                },
                            horse.weight
                                ?.let {
                                    "Kilo %.1f"
                                        .format(it)
                                }
                        )
                            .joinToString(
                                " · "
                            )
                            .ifBlank {
                                "Snapshot verisi"
                            },
                    color =
                        Muted,
                    fontSize =
                        10.sp
                )
            }
        }
    }
}

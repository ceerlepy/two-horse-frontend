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
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.domain.model.HistoryRace
import com.twohorse.app.domain.model.Horse
import com.twohorse.app.ui.theme.Border
import com.twohorse.app.ui.theme.Green
import com.twohorse.app.ui.theme.Ink
import com.twohorse.app.ui.theme.Muted
import com.twohorse.app.ui.theme.PaleGold
import com.twohorse.app.ui.theme.PaleGreen
import com.twohorse.app.ui.theme.Surface

@Composable
fun HistoryDetailScreen(
    historyRace: HistoryRace,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val ranked =
        historyRace.runners
            .sortedWith(
                compareByDescending<Horse> {
                    it.score ?: Double.NEGATIVE_INFINITY
                }.thenBy {
                    it.number
                }
            )

    val winner =
        ranked.firstOrNull {
            it.finishPosition == 1
        }

    val modelLeader =
        ranked.firstOrNull()

    val resultAvailable =
        ranked.any {
            it.finishPosition != null
        }

    val top1Hit =
        winner != null &&
        modelLeader != null &&
        winner.number == modelLeader.number

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            HistoryHeader(
                race = historyRace,
                onBack = onBack
            )
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp)
            ) {
                ResultSummaryCard(
                    race = historyRace,
                    modelLeader = modelLeader,
                    winner = winner,
                    resultAvailable = resultAvailable,
                    top1Hit = top1Hit
                )
            }
        }

        item {
            Column(
                modifier =
                    Modifier.padding(
                        horizontal = 18.dp,
                        vertical = 6.dp
                    )
            ) {
                Text(
                    text = "Model sırası ve gerçek sonuç",
                    color = Ink,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text =
                        "Yarış öncesi model sıralaması gerçek bitiriş derecesiyle karşılaştırılır.",
                    color = Muted,
                    fontSize = 10.sp
                )
            }
        }

        itemsIndexed(
            items = ranked,
            key = { _, horse ->
                horse.number
            }
        ) { index, horse ->
            Column(
                modifier = Modifier.padding(horizontal = 18.dp)
            ) {
                HistoricalHorseCard(
                    horse = horse,
                    modelRank = index + 1
                )
            }
        }

        item {
            Spacer(
                modifier = Modifier.height(30.dp)
            )
        }
    }
}

@Composable
private fun HistoryHeader(
    race: HistoryRace,
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Geri",
                tint = Ink
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text =
                    "${race.city} · ${race.raceNumber}. Koşu",
                color = Ink,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text =
                    "${race.raceDate} · ${race.startTime}",
                color = Muted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun ResultSummaryCard(
    race: HistoryRace,
    modelLeader: Horse?,
    winner: Horse?,
    resultAvailable: Boolean,
    top1Hit: Boolean
) {
    val background =
        when {
            !resultAvailable ->
                PaleGold

            top1Hit ->
                PaleGreen

            else ->
                Surface
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = background
            ),
        border =
            BorderStroke(
                width = 1.dp,
                color =
                    if (top1Hit)
                        Green.copy(alpha = 0.35f)
                    else
                        Border
            ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text =
                    when {
                        !resultAvailable ->
                            "Sonuç henüz hazır değil"

                        top1Hit ->
                            "✓ Model lideri yarışı kazandı"

                        else ->
                            "Yarış sonucu tamamlandı"
                    },
                color =
                    if (top1Hit)
                        Green
                    else
                        Ink,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(
                modifier = Modifier.height(11.dp)
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                SummaryValue(
                    modifier = Modifier.weight(1f),
                    label = "Model lideri",
                    value =
                        modelLeader
                            ?.let {
                                "#${it.number} ${it.name}"
                            }
                            ?: "—"
                )

                SummaryValue(
                    modifier = Modifier.weight(1f),
                    label = "Kazanan",
                    value =
                        winner
                            ?.let {
                                "#${it.number} ${it.name}"
                            }
                            ?: if (resultAvailable)
                                "—"
                            else
                                "Bekleniyor"
                )
            }

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            Text(
                text =
                    "${race.runners.size} at · " +
                    "${race.expertPredictionCount} uzman kaydı",
                color = Muted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun SummaryValue(
    modifier: Modifier,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier,
        color =
            Color.White.copy(
                alpha = 0.76f
            ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = label,
                color = Muted,
                fontSize = 8.sp
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = value,
                color = Ink,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun HistoricalHorseCard(
    horse: Horse,
    modelRank: Int
) {
    val finish =
        horse.finishPosition

    val exactHit =
        finish != null &&
        finish == modelRank

    val won =
        finish == 1

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = Surface
            ),
        border =
            BorderStroke(
                width = 1.dp,
                color =
                    when {
                        won ->
                            Green.copy(alpha = 0.60f)

                        exactHit ->
                            Green.copy(alpha = 0.35f)

                        else ->
                            Border
                    }
            ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Surface(
                    color =
                        if (modelRank == 1)
                            PaleGreen
                        else
                            Color(0xFFF3F5F4),
                    shape =
                        RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text =
                            horse.number.toString(),
                        modifier =
                            Modifier.padding(
                                horizontal = 11.dp,
                                vertical = 8.dp
                            ),
                        color =
                            if (modelRank == 1)
                                Green
                            else
                                Ink,
                        fontWeight =
                            FontWeight.ExtraBold
                    )
                }

                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(start = 10.dp)
                ) {
                    Text(
                        text = horse.name,
                        color = Ink,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (
                        horse.jockey.isNotBlank()
                    ) {
                        Text(
                            text = horse.jockey,
                            color = Muted,
                            fontSize = 9.sp,
                            maxLines = 1,
                            overflow =
                                TextOverflow.Ellipsis
                        )
                    }
                }

                when {
                    won -> {
                        ResultBadge(
                            text = "KAZANDI"
                        )
                    }

                    exactHit -> {
                        ResultBadge(
                            text = "✓ İSABET"
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(11.dp)
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(7.dp)
            ) {
                ResultMetric(
                    modifier = Modifier.weight(1f),
                    label = "Model sırası",
                    value = "$modelRank."
                )

                ResultMetric(
                    modifier = Modifier.weight(1f),
                    label = "Gerçek derece",
                    value =
                        finish
                            ?.let {
                                "$it."
                            }
                            ?: "—"
                )

                ResultMetric(
                    modifier = Modifier.weight(1f),
                    label = "Model puanı",
                    value =
                        horse.score
                            ?.let {
                                "%.1f".format(it)
                            }
                            ?: "—"
                )
            }
        }
    }
}

@Composable
private fun ResultBadge(
    text: String
) {
    Surface(
        color = PaleGreen,
        shape =
            RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            modifier =
                Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 5.dp
                ),
            color = Green,
            fontSize = 8.sp,
            fontWeight =
                FontWeight.ExtraBold
        )
    }
}

@Composable
private fun ResultMetric(
    modifier: Modifier,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier,
        color =
            Color(0xFFF5F7F6),
        shape =
            RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = label,
                color = Muted,
                fontSize = 8.sp
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = value,
                color = Ink,
                fontSize = 12.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )
        }
    }
}

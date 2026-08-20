package com.twohorse.app.ui.race

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.domain.model.*
import com.twohorse.app.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun RaceDetailScreen(
    race: Race,
    onBack: () -> Unit,
    onOpenCoupons: (String) -> Unit
) {
    BackHandler(onBack = onBack)

    val repository =
        remember {
            TwoHorseRepository()
        }

    var currentRace by
        remember(
            race.city,
            race.number
        ) {
            mutableStateOf(race)
        }

    var refreshing by
        remember {
            mutableStateOf(false)
        }

    var refreshError by
        remember {
            mutableStateOf<String?>(null)
        }

    var refreshKey by
        remember {
            mutableIntStateOf(0)
        }

    LaunchedEffect(
        refreshKey,
        race.city,
        race.number
    ) {
        if (refreshing) {
            return@LaunchedEffect
        }

        refreshing = true
        refreshError = null

        repository
            .today()
            .onSuccess { today ->
                val freshRace =
                    today.meetings
                        .asSequence()
                        .flatMap {
                            it.races.asSequence()
                        }
                        .firstOrNull {
                            it.city == race.city &&
                                it.number == race.number
                        }

                if (freshRace != null) {
                    currentRace = freshRace
                } else {
                    refreshError =
                        "Yarış güncel programda bulunamadı. Son bilinen veri gösteriliyor."
                }
            }
            .onFailure {
                refreshError =
                    "Yarış yenilenemedi. Son bilinen veri gösteriliyor."
            }

        refreshing = false
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
            Arrangement.spacedBy(10.dp)
    ) {
        item {
            Header(
                race = currentRace,
                refreshing = refreshing,
                onRefresh = {
                    if (!refreshing) {
                        refreshKey++
                    }
                },
                onBack = onBack
            )
        }

        refreshError?.let { message ->
            item {
                NoticeCard(message)
            }
        }

        item {
            Column(
                modifier =
                    Modifier.padding(
                        horizontal = 18.dp
                    )
            ) {
                RaceOverview(
                    race = currentRace,
                    horses = rankedHorses
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
                Button(
                    onClick = {
                        onOpenCoupons(
                            currentRace.city
                        )
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Green
                        ),
                    shape =
                        RoundedCornerShape(
                            14.dp
                        )
                ) {
                    Text(
                        "Bu şehir için Altılı Kupon"
                    )
                }
            }
        }

        currentRace.uncertainty?.let {
            item {
                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp
                        )
                ) {
                    UncertaintyCard(
                        uncertainty = it,
                        strategy =
                            currentRace
                                .couponStrategy
                    )
                }
            }
        }

        if (rankedHorses.isEmpty()) {
            item {
                NoticeCard(
                    "Bu yarış için at verisi bulunamadı."
                )
            }
        } else {
            item {
                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp,
                            vertical = 4.dp
                        )
                ) {
                    Text(
                        "Detaylı model analizi",
                        color = Ink,
                        fontSize = 19.sp,
                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Text(
                        "Skorun hangi sinyallerden oluştuğunu aşağıda görebilirsin.",
                        color = Muted,
                        fontSize = 11.sp
                    )
                }
            }

            items(
                items = rankedHorses,
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
                    HorseCard(
                        horse = horse,
                        rank =
                            rankedHorses
                                .indexOf(horse) + 1
                    )
                }
            }
        }

        item {
            Spacer(
                Modifier.height(28.dp)
            )
        }
    }
}

@Composable
private fun Header(
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
        IconButton(onClick = onBack) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Geri",
                tint = Ink
            )
        }

        Column(
            Modifier.weight(1f)
        ) {
            Text(
                "${race.city} · ${race.number}. Koşu",
                color = Ink,
                fontSize = 18.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )

            Text(
                raceMeta(race),
                color = Muted,
                fontSize = 11.sp
            )
        }

        IconButton(
            onClick = onRefresh,
            enabled = !refreshing
        ) {
            if (refreshing) {
                CircularProgressIndicator(
                    modifier =
                        Modifier.size(22.dp),
                    color = Green,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription =
                        "Yenile",
                    tint = Green
                )
            }
        }
    }
}

@Composable
private fun RaceOverview(
    race: Race,
    horses: List<Horse>
) {
    val leader =
        horses.firstOrNull()

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = Green
            ),
        shape =
            RoundedCornerShape(20.dp)
    ) {
        Column(
            Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Stars,
                    contentDescription = null,
                    tint = Gold
                )

                Spacer(
                    Modifier.width(7.dp)
                )

                Text(
                    "MODEL ANALİZİ",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight =
                        FontWeight.ExtraBold
                )

                Spacer(
                    Modifier.weight(1f)
                )

                Text(
                    "${race.horses.size} at",
                    color =
                        Color.White.copy(
                            alpha = 0.85f
                        ),
                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                Modifier.height(14.dp)
            )

            if (leader != null) {
                Text(
                    "Model lideri",
                    color =
                        Color.White.copy(
                            alpha = 0.7f
                        ),
                    fontSize = 10.sp
                )

                Text(
                    "#${leader.number} ${leader.name}",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight =
                        FontWeight.ExtraBold
                )

                leader.score?.let {
                    Text(
                        "Final puan ${one(it)}",
                        color = Color.White,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun UncertaintyCard(
    uncertainty: RaceUncertainty,
    strategy: RaceCouponStrategy?
) {
    Card(
        modifier =
            Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = Surface
            ),
        border =
            BorderStroke(
                1.dp,
                Border
            ),
        shape =
            RoundedCornerShape(18.dp)
    ) {
        Column(
            Modifier.padding(15.dp)
        ) {
            Text(
                "Yarış belirsizliği",
                color = Ink,
                fontWeight =
                    FontWeight.ExtraBold
            )

            Spacer(
                Modifier.height(7.dp)
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(7.dp)
            ) {
                Metric(
                    Modifier.weight(1f),
                    "Seviye",
                    uncertaintyLabel(
                        uncertainty.level
                    )
                )

                Metric(
                    Modifier.weight(1f),
                    "Belirsizlik",
                    "${one(uncertainty.score)}%"
                )

                Metric(
                    Modifier.weight(1f),
                    "Lider farkı",
                    one(
                        uncertainty.topMargin
                    )
                )
            }

            strategy?.let {
                Spacer(
                    Modifier.height(11.dp)
                )

                Text(
                    "Backend kupon stratejisi",
                    color = Muted,
                    fontSize = 10.sp
                )

                Text(
                    strategyText(it),
                    color = Ink,
                    fontSize = 13.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                if (
                    it.horseNumbers
                        .isNotEmpty()
                ) {
                    Text(
                        "Önerilen atlar: ${
                            it.horseNumbers
                                .joinToString(", ")
                        }",
                        color = Green,
                        fontSize = 11.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun HorseCard(
    horse: Horse,
    rank: Int
) {
    Card(
        modifier =
            Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = Surface
            ),
        border =
            BorderStroke(
                1.dp,
                Border
            ),
        shape =
            RoundedCornerShape(18.dp)
    ) {
        Column(
            Modifier.padding(15.dp)
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
                        RoundedCornerShape(12.dp)
                ) {
                    Text(
                        horse.number.toString(),
                        modifier =
                            Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 9.dp
                            ),
                        color =
                            if (rank == 1)
                                Green
                            else
                                Gold,
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
                        horse.name,
                        color = Ink,
                        fontSize = 16.sp,
                        fontWeight =
                            FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow =
                            TextOverflow.Ellipsis
                    )

                    if (
                        horse.jockey
                            .isNotBlank()
                    ) {
                        Text(
                            horse.jockey,
                            color = Muted,
                            fontSize = 10.sp
                        )
                    }
                }

                horse.score?.let {
                    Text(
                        one(it),
                        color = Green,
                        fontSize = 17.sp,
                        fontWeight =
                            FontWeight.ExtraBold
                    )
                }
            }

            Spacer(
                Modifier.height(12.dp)
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {
                Metric(
                    Modifier.weight(1f),
                    "Güven",
                    horse.confidence
                        ?.let {
                            "${(it * 100).roundToInt()}%"
                        }
                        ?: "—"
                )

                Metric(
                    Modifier.weight(1f),
                    "AGF",
                    horse.agfPercent
                        ?.let {
                            "${one(it)}%"
                        }
                        ?: "—"
                )

                Metric(
                    Modifier.weight(1f),
                    "HP",
                    horse.hp
                        ?.toString()
                        ?: "—"
                )

                Metric(
                    Modifier.weight(1f),
                    "Kilo",
                    horse.weight
                        ?.let(::one)
                        ?: "—"
                )
            }

            LearningSection(horse)

            ExpertSection(
                horse.expertConsensus
            )

            MarketSection(
                horse.marketMovement
            )

            FieldSection(
                horse.fieldSignal
            )

            if (
                horse.scoreComponents
                    .isNotEmpty()
            ) {
                Spacer(
                    Modifier.height(13.dp)
                )

                Text(
                    "Puan bileşenleri",
                    color = Ink,
                    fontSize = 12.sp,
                    fontWeight =
                        FontWeight.ExtraBold
                )

                Spacer(
                    Modifier.height(6.dp)
                )

                horse.scoreComponents
                    .forEach {
                        ComponentRow(it)
                    }
            }
        }
    }
}

@Composable
private fun LearningSection(
    horse: Horse
) {
    val base =
        horse.baseScore

    val adjustment =
        horse.learningAdjustment

    if (
        base == null &&
        adjustment == null
    ) {
        return
    }

    Spacer(
        Modifier.height(12.dp)
    )

    Surface(
        color = PaleGreen,
        shape =
            RoundedCornerShape(12.dp)
    ) {
        Column(
            Modifier.padding(10.dp)
        ) {
            Text(
                "Learning etkisi",
                color = Green,
                fontSize = 10.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )

            val final =
                horse.score

            Text(
                buildString {
                    if (base != null) {
                        append(
                            "Base ${one(base)}"
                        )
                    }

                    if (final != null) {
                        if (isNotEmpty()) {
                            append(" → ")
                        }

                        append(
                            "Final ${one(final)}"
                        )
                    }

                    if (adjustment != null) {
                        append(
                            " (${
                                if (adjustment >= 0)
                                    "+"
                                else
                                    ""
                            }${one(adjustment)})"
                        )
                    }
                },
                color = Ink,
                fontSize = 12.sp,
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ExpertSection(
    value: ExpertConsensusSummary?
) {
    if (
        value == null ||
        value.sourceCount <= 0
    ) {
        return
    }

    Spacer(
        Modifier.height(12.dp)
    )

    Text(
        "Uzman konsensüsü",
        color = Ink,
        fontSize = 12.sp,
        fontWeight =
            FontWeight.ExtraBold
    )

    Text(
        buildString {
            append(
                "${value.sourceCount} kaynak"
            )

            if (value.bankoCount > 0) {
                append(
                    " · ${value.bankoCount} banko"
                )
            }

            if (
                value.favoriteCount > 0
            ) {
                append(
                    " · ${value.favoriteCount} favori"
                )
            }

            if (
                value.strongCount > 0
            ) {
                append(
                    " · ${value.strongCount} güçlü"
                )
            }

            if (
                value.surpriseCount > 0
            ) {
                append(
                    " · ${value.surpriseCount} sürpriz"
                )
            }
        },
        color = Muted,
        fontSize = 11.sp
    )

    if (
        value.labels
            .isNotEmpty()
    ) {
        Text(
            value.labels
                .joinToString(" · "),
            color = Green,
            fontSize = 10.sp,
            fontWeight =
                FontWeight.Bold
        )
    }
}

@Composable
private fun MarketSection(
    value: MarketMovement?
) {
    if (value == null) {
        return
    }

    Spacer(
        Modifier.height(12.dp)
    )

    Text(
        "Piyasa hareketi",
        color = Ink,
        fontSize = 12.sp,
        fontWeight =
            FontWeight.ExtraBold
    )

    Text(
        buildString {
            append(
                marketLabel(
                    value.direction
                )
            )

            value.score?.let {
                append(
                    " · skor ${one(it)}"
                )
            }

            value.absoluteDelta?.let {
                append(
                    " · AGF ${
                        if (it >= 0)
                            "+"
                        else
                            ""
                    }${one(it)}"
                )
            }

            append(
                " · ${value.sampleSize} ölçüm"
            )
        },
        color = Muted,
        fontSize = 11.sp
    )
}

@Composable
private fun FieldSection(
    value: FieldSignal?
) {
    if (
        value == null ||
        value.score == null
    ) {
        return
    }

    Spacer(
        Modifier.height(12.dp)
    )

    Text(
        "Saha sinyali",
        color = Ink,
        fontSize = 12.sp,
        fontWeight =
            FontWeight.ExtraBold
    )

    Text(
        buildString {
            append(
                "Birleşik ${one(value.score)}"
            )

            value.tjkScore?.let {
                append(
                    " · TJK ${one(it)}"
                )
            }

            value.expertScore?.let {
                append(
                    " · Uzman ${one(it)}"
                )
            }

            if (
                value.tjkSampleSize > 0
            ) {
                append(
                    " · ${value.tjkSampleSize} örnek"
                )
            }
        },
        color = Muted,
        fontSize = 11.sp
    )
}

@Composable
private fun ComponentRow(
    item: ScoreComponent
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 3.dp
                ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Text(
            componentName(item.key),
            modifier =
                Modifier.weight(1f),
            color = Muted,
            fontSize = 10.sp
        )

        Text(
            item.score
                ?.let(::one)
                ?: "veri yok",
            color = Ink,
            fontSize = 11.sp,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            Modifier.width(10.dp)
        )

        Text(
            "ağırlık ${one(item.effectiveWeight)}",
            color = Muted,
            fontSize = 9.sp
        )
    }
}

@Composable
private fun Metric(
    modifier: Modifier,
    title: String,
    value: String
) {
    Surface(
        modifier = modifier,
        color =
            Color(
                0xFFF6F8F7
            ),
        shape =
            RoundedCornerShape(10.dp)
    ) {
        Column(
            Modifier.padding(8.dp)
        ) {
            Text(
                title,
                color = Muted,
                fontSize = 8.sp
            )

            Text(
                value,
                color = Ink,
                fontSize = 11.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun NoticeCard(
    message: String
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp
                ),
        color = PaleGold,
        shape =
            RoundedCornerShape(14.dp)
    ) {
        Text(
            message,
            modifier =
                Modifier.padding(12.dp),
            color = Muted,
            fontSize = 11.sp
        )
    }
}

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
        .joinToString(" · ")
        .ifBlank {
            "Koşu bilgisi"
        }

private fun one(
    value: Double
): String =
    "%.1f".format(value)

private fun componentName(
    key: String
): String =
    when (key) {
        "agf" -> "AGF"
        "expert" -> "Uzman"
        "form" -> "Form"
        "hp" -> "HP"
        "market" -> "Piyasa"
        "weight" -> "Kilo"
        "field" -> "Saha"
        else -> key
    }

private fun uncertaintyLabel(
    value: String
): String =
    when (value) {
        "low" -> "Düşük"
        "medium" -> "Orta"
        "high" -> "Yüksek"
        "very-high" -> "Çok yüksek"
        else -> value
    }

private fun marketLabel(
    value: String
): String =
    when (value) {
        "strong-up" -> "Güçlü yükseliş"
        "up" -> "Yükseliş"
        "flat" -> "Yatay"
        "down" -> "Düşüş"
        "strong-down" -> "Güçlü düşüş"
        else -> "Yetersiz piyasa verisi"
    }

private fun strategyText(
    value: RaceCouponStrategy
): String =
    when (value.mode) {
        "single" ->
            "Tek adayı güçlü"

        "compact" ->
            "Dar kupon uygun"

        "spread" ->
            "Bu ayakta genişleme öneriliyor"

        else ->
            value.reason
    }

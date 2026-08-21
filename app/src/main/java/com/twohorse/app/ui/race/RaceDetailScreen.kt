package com.twohorse.app.ui.race

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
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
import com.twohorse.app.ui.components.*
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

    var error by
        remember {
            mutableStateOf<String?>(null)
        }

    var refreshKey by
        remember {
            mutableIntStateOf(0)
        }

    var deepExpanded by
        remember {
            mutableStateOf(false)
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
        error = null

        repository
            .today()
            .onSuccess { today ->
                val fresh =
                    today.meetings
                        .asSequence()
                        .flatMap {
                            it.races.asSequence()
                        }
                        .firstOrNull {
                            it.city ==
                                race.city &&
                            it.number ==
                                race.number
                        }

                if (fresh != null) {
                    currentRace = fresh
                } else {
                    error =
                        "Yarış güncel programda bulunamadı. Son bilinen veri gösteriliyor."
                }
            }
            .onFailure {
                error =
                    "Yarış yenilenemedi. Son bilinen veri gösteriliyor."
            }

        refreshing = false
    }

    val horses =
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

    val favorite =
        horses.firstOrNull()

    val rival =
        horses.getOrNull(1)

    val surprise =
        horses.getOrNull(2)

    LazyColumn(
        modifier =
            Modifier.fillMaxSize(),
        contentPadding =
            PaddingValues(
                bottom = 34.dp
            ),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {
        item {
            RaceHeader(
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

        error?.let {
            item {
                Notice(
                    text = it
                )
            }
        }

        favorite?.let {
            item {
                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp
                        )
                ) {
                    ResultHero(
                        favorite = it,
                        rival = rival,
                        surprise = surprise
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
                RaceRiskCard(
                    race = currentRace
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
                        Modifier
                            .fillMaxWidth()
                            .heightIn(
                                min = 50.dp
                            ),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Green
                        ),
                    shape =
                        RoundedCornerShape(15.dp)
                ) {
                    Text(
                        text =
                            "Bu şehir için Altılı Kupon",
                        fontWeight =
                            FontWeight.Black
                    )
                }
            }
        }

        if (
            horses.isNotEmpty()
        ) {
            item {
                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp
                        )
                ) {
                    SectionHeader(
                        title =
                            "Olası sıralama · tüm atlar",
                        subtitle =
                            "${horses.size} at"
                    )
                }
            }

            itemsIndexed(
                items = horses,
                key = {
                    _,
                    horse ->
                    horse.number
                }
            ) {
                index,
                horse ->

                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp
                        )
                ) {
                    HorseCard(
                        horse = horse,
                        rank = index + 1
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
                ExpandableAnalysisHeader(
                    expanded =
                        deepExpanded,
                    onClick = {
                        deepExpanded =
                            !deepExpanded
                    }
                )
            }
        }

        if (
            deepExpanded
        ) {
            favorite?.let {
                item {
                    Column(
                        modifier =
                            Modifier.padding(
                                horizontal = 18.dp
                            )
                    ) {
                        DeepAnalysisCard(
                            horse = it
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RaceHeader(
    race: Race,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onBack: () -> Unit
) {
    val compact =
        isCompactScreen()

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(
                    horizontal = 8.dp,
                    vertical = 5.dp
                ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier =
                Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Geri",
                tint = Ink
            )
        }

        Column(
            modifier =
                Modifier.weight(1f)
        ) {
            Text(
                text =
                    "${race.city} · ${race.number}. Koşu",
                color = Ink,
                fontSize =
                    if (compact)
                        18.sp
                    else
                        21.sp,
                fontWeight =
                    FontWeight.Black,
                maxLines = 2
            )

            Text(
                text =
                    raceMeta(race),
                color = Muted,
                fontSize = 12.sp
            )
        }

        IconButton(
            onClick = onRefresh,
            enabled = !refreshing,
            modifier =
                Modifier.size(48.dp)
        ) {
            if (refreshing) {
                CircularProgressIndicator(
                    modifier =
                        Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Green
                )
            } else {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription =
                        "Yarışı yenile",
                    tint = Ink
                )
            }
        }
    }
}

@Composable
private fun ResultHero(
    favorite: Horse,
    rival: Horse?,
    surprise: Horse?
) {
    Card(
        colors =
            CardDefaults.cardColors(
                containerColor = Ink
            ),
        shape =
            RoundedCornerShape(26.dp)
    ) {
        Column(
            modifier =
                Modifier.padding(20.dp)
        ) {
            Text(
                text =
                    "OLASI KAZANAN",
                color = Gold,
                fontWeight =
                    FontWeight.ExtraBold,
                fontSize = 10.sp
            )

            Spacer(
                Modifier.height(6.dp)
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
                            "#${favorite.number} ${favorite.name}",
                        color = Color.White,
                        fontWeight =
                            FontWeight.Black,
                        fontSize = 24.sp,
                        maxLines = 2,
                        overflow =
                            TextOverflow.Ellipsis
                    )

                    Text(
                        text =
                            "Güven Puanı ${
                                favorite.score
                                    ?.let {
                                        "%.1f".format(it)
                                    }
                                    ?: "—"
                            }/100",
                        color =
                            Color.White.copy(
                                alpha = 0.72f
                            ),
                        fontSize = 13.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }

                Surface(
                    color =
                        Color.White.copy(
                            alpha = 0.10f
                        ),
                    shape = CircleShape
                ) {
                    Text(
                        text =
                            favorite.score
                                ?.roundToInt()
                                ?.toString()
                                ?: "—",
                        modifier =
                            Modifier.padding(18.dp),
                        color = Gold,
                        fontWeight =
                            FontWeight.Black,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(
                Modifier.height(16.dp)
            )

            DarkMetric(
                "AGF",
                favorite.agfPercent
                    ?.let {
                        "%${"%.1f".format(it)}"
                    }
                    ?: "Veri yok"
            )

            DarkMetric(
                "HP",
                favorite.hp
                    ?.let {
                        "$it puan"
                    }
                    ?: "Veri yok"
            )

            DarkMetric(
                "Uzman desteği",
                expertSummary(
                    favorite
                )
            )

            DarkMetric(
                "Saha",
                fieldSummary(
                    favorite
                )
            )

            DarkMetric(
                "Piyasa",
                marketSummary(
                    favorite
                )
            )

            DarkMetric(
                "Form",
                favorite.recentForm
                    .ifBlank {
                        "Veri yok"
                    }
            )

            favorite.learningAdjustment
                ?.let {
                    DarkMetric(
                        "Learning",
                        "${
                            if (it >= 0)
                                "+"
                            else
                                ""
                        }${"%.1f".format(it)} puan"
                    )
                }

            if (
                rival != null ||
                surprise != null
            ) {
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

                rival?.let {
                    Text(
                        text =
                            "En ciddi rakip: #${it.number} ${it.name} · ${
                                it.score?.roundToInt()
                                    ?: 0
                            }/100",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }

                surprise?.let {
                    Spacer(
                        Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            "💣 Sürpriz: #${it.number} ${it.name} · ${
                                it.score?.roundToInt()
                                    ?: 0
                            }/100",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DarkMetric(
    label: String,
    value: String
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
    ) {
        Text(
            text = label,
            modifier =
                Modifier.weight(1f),
            color =
                Color.White.copy(
                    alpha = 0.58f
                ),
            fontSize = 11.sp
        )

        Spacer(
            Modifier.width(10.dp)
        )

        Text(
            text = value,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight =
                FontWeight.SemiBold
        )
    }
}

@Composable
private fun RaceRiskCard(
    race: Race
) {
    Card(
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
                text =
                    "Yarış risk haritası",
                color = Ink,
                fontSize = 14.sp,
                fontWeight =
                    FontWeight.Black
            )

            RaceInsightSummary(
                race = race
            )

            race.uncertainty?.let {
                Spacer(
                    Modifier.height(9.dp)
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(7.dp)
                ) {
                    MiniMetric(
                        Modifier.weight(1f),
                        "Belirsizlik",
                        uncertaintyText(
                            it.level
                        )
                    )

                    MiniMetric(
                        Modifier.weight(1f),
                        "Lider farkı",
                        "%.1f".format(
                            it.topMargin
                        )
                    )

                    MiniMetric(
                        Modifier.weight(1f),
                        "Genişleme",
                        "%.1f".format(
                            it.expansionPressure
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String
) {
    Row(
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.Bottom
    ) {
        Text(
            text = title,
            modifier =
                Modifier.weight(1f),
            color = Ink,
            fontSize = 19.sp,
            fontWeight =
                FontWeight.Black
        )

        Text(
            text = subtitle,
            color = Muted,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun ExpandableAnalysisHeader(
    expanded: Boolean,
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
                containerColor = Surface
            ),
        border =
            CardDefaults
                .outlinedCardBorder(),
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
                Modifier.weight(1f)
            ) {
                Text(
                    text =
                        "Detaylı model analizi",
                    color = Ink,
                    fontWeight =
                        FontWeight.Black
                )

                Text(
                    text =
                        "Ağırlıklar, learning ve sinyal bileşenleri",
                    color = Muted,
                    fontSize = 10.sp
                )
            }

            Icon(
                if (expanded)
                    Icons.Default.KeyboardArrowUp
                else
                    Icons.Default.KeyboardArrowDown,
                contentDescription =
                    if (expanded)
                        "Detaylı analizi kapat"
                    else
                        "Detaylı analizi aç"
            )
        }
    }
}

@Composable
private fun Notice(
    text: String
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
            text = text,
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

private fun expertSummary(
    horse: Horse
): String {
    val e =
        horse.expertConsensus
            ?: return "Kaynak bekleniyor / bulunamadı"

    return buildString {
        append(
            "${e.sourceCount} kaynak"
        )

        if (e.favoriteCount > 0) {
            append(
                " · ${e.favoriteCount} favori"
            )
        }

        if (e.bankoCount > 0) {
            append(
                " · ⭐ ${e.bankoCount} banko"
            )
        }

        if (e.strongCount > 0) {
            append(
                " · ${e.strongCount} güçlü"
            )
        }
    }
}

private fun marketSummary(
    horse: Horse
): String {
    val m =
        horse.marketMovement
            ?: return "Veri yok"

    return buildString {
        append(
            "${marketArrow(m.direction)} ${
                marketText(m.direction)
            }"
        )

        m.absoluteDelta?.let {
            append(
                " · AGF ${
                    if (it >= 0)
                        "+"
                    else
                        ""
                }${"%.1f".format(it)}"
            )
        }
    }
}

private fun fieldSummary(
    horse: Horse
): String =
    horse.fieldSignal
        ?.score
        ?.let {
            "Birleşik ${"%.1f".format(it)}"
        }
        ?: "Veri yok"

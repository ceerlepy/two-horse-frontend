package com.twohorse.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.R
import com.twohorse.app.domain.model.Horse
import com.twohorse.app.domain.model.Race
import com.twohorse.app.ui.theme.*

@Composable
fun TwoHorseHeader(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onHistory: () -> Unit,
    onAccount: () -> Unit = {}
) {
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
                    "Two Horse logosu",
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

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {
                Text(
                    text = "Two Horse",
                    color = Ink,
                    fontSize =
                        if (compact)
                            23.sp
                        else
                            27.sp,
                    fontWeight =
                        FontWeight.Black,
                    maxLines = 1
                )

                Text(
                    text =
                        if (refreshing)
                            "Canlı veri güncelleniyor…"
                        else
                            "Türkiye yarış analizi",
                    color = Muted,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            IconButton(
                onClick = onAccount,
                modifier =
                    Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription =
                        "Üyelik ve hesap",
                    tint = Ink
                )
            }

            IconButton(
                onClick = onHistory,
                modifier =
                    Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription =
                        "Geçmiş yarışları aç",
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
                        "Yarış verisini yenile",
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
                            "$countdown KALDI",
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
                text = "Sıradaki yarış",
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
                    "${race.city} · ${race.number}. Koşu",
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
                                "MODEL FAVORİSİ",
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
                    text = "Analizi aç",
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
                        "${race.number}. Koşu",
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
                        "${race.city} · ${race.number}. Koşu",
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
                            "Favori: #${it.number} ${it.name}",
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
                    "Yarış analizini aç",
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
    onClick: () -> Unit
) {
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
                containerColor = Surface
            ),
        border =
            CardDefaults
                .outlinedCardBorder(),
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
                            "${race.city} · ${race.number}. KOŞU",
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
                                "MODEL FAVORİSİ",
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

                    Text(
                        text =
                            "💣 Sürpriz  #${horse.number} ${horse.name}",
                        color = Red,
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

@Composable
fun RemainingRacesToggle(
    count: Int,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onToggle
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
                        "Diğer kalan koşular",
                    color = Ink,
                    fontSize = 14.sp,
                    fontWeight =
                        FontWeight.Black
                )

                Text(
                    text =
                        "$count yaklaşan koşu",
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
                        "Diğer yarışları kapat"
                    else
                        "Diğer yarışları aç",
                tint = Ink
            )
        }
    }
}

@Composable
fun SixFoldEntryCard(
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
                    text = "Altılı Kupon",
                    color = Ink,
                    fontSize = 16.sp,
                    fontWeight =
                        FontWeight.Black
                )

                Text(
                    text =
                        "Bütçene göre optimize edilmiş kupon",
                    color = Muted,
                    fontSize = 12.sp
                )
            }

            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription =
                    "Altılı kupon ekranını aç",
                tint = Gold
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

private fun favoriteSummary(
    horse: Horse
): String =
    buildList {
        horse.agfPercent?.let {
            add("AGF %${"%.1f".format(it)}")
        }

        horse.expertConsensus
            ?.sourceCount
            ?.takeIf {
                it > 0
            }
            ?.let {
                add("Uzman $it kaynak")
            }

        horse.hp?.let {
            add("HP $it")
        }
    }
        .joinToString(" · ")
        .ifBlank {
            "Model lideri"
        }

private fun raceMeta(
    race: Race
): String =
    listOf(
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
            "Koşu bilgisi"
        }

private fun raceTime(
    race: Race
): String =
    race.startsAt
        ?.let {
            Regex(
                """\b\d{2}:\d{2}\b"""
            )
                .find(it)
                ?.value
        }
        ?: "--:--"

package com.twohorse.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.R
import com.twohorse.app.domain.model.Race
import com.twohorse.app.ui.theme.Border
import com.twohorse.app.ui.theme.Gold
import com.twohorse.app.ui.theme.Green
import com.twohorse.app.ui.theme.Ink
import com.twohorse.app.ui.theme.Muted
import com.twohorse.app.ui.theme.PaleGold
import com.twohorse.app.ui.theme.PaleGreen
import com.twohorse.app.ui.theme.Surface

@Composable
fun TwoHorseHeader(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onHistory: () -> Unit
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 12.dp
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
                "Two Horse",
            modifier =
                Modifier.size(44.dp)
        )

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(
                        start = 10.dp
                    )
        ) {
            Text(
                text = "Two Horse",
                color = Ink,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Akıllı yarış analizi",
                color = Muted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        IconButton(
            onClick = onHistory
        ) {
            Icon(
                imageVector =
                    Icons.Default.History,
                contentDescription =
                    "Geçmiş",
                tint = Ink
            )
        }

        IconButton(
            onClick = onRefresh,
            enabled = !refreshing
        ) {
            Icon(
                imageVector =
                    Icons.Default.Refresh,
                contentDescription =
                    "Yenile",
                tint =
                    if (refreshing)
                        Muted
                    else
                        Green
            )
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
                    RoundedCornerShape(
                        50
                    )
                )
                .clickable(
                    onClick = onClick
                ),
        shape =
            RoundedCornerShape(
                50
            ),
        color =
            if (selected)
                Green
            else
                Surface,
        tonalElevation =
            if (selected)
                1.dp
            else
                0.dp,
        shadowElevation =
            0.dp,
        border =
            androidx.compose.foundation.BorderStroke(
                1.dp,
                if (selected)
                    Green
                else
                    Border
            )
    ) {
        Text(
            text = city,
            modifier =
                Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 9.dp
                ),
            color =
                if (selected)
                    Color.White
                else
                    Ink,
            fontWeight =
                FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}

@Composable
fun NextRaceHero(
    race: Race,
    countdown: String,
    onClick: () -> Unit
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),
        shape =
            RoundedCornerShape(
                22.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Green
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    2.dp
            )
    ) {
        Column(
            modifier =
                Modifier.padding(
                    18.dp
                )
        ) {
            Row(
                modifier =
                    Modifier.fillMaxWidth(),
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
                    Text(
                        text =
                            "EN YAKIN YARIŞ",
                        modifier =
                            Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),
                        color =
                            Gold,
                        fontSize =
                            10.sp,
                        fontWeight =
                            FontWeight.ExtraBold
                    )
                }

                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )

                Text(
                    text =
                        countdown,
                    color =
                        Color.White,
                    fontSize =
                        18.sp,
                    fontWeight =
                        FontWeight.ExtraBold
                )
            }

            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )

            Text(
                text =
                    "${race.city} · ${race.number}. Koşu",
                color =
                    Color.White,
                fontSize =
                    21.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )

            Spacer(
                modifier =
                    Modifier.height(
                        4.dp
                    )
            )

            if (
                race.title.isNotBlank()
            ) {
                Text(
                    text =
                        race.title,
                    color =
                        Color.White.copy(
                            alpha = 0.88f
                        ),
                    fontSize =
                        13.sp,
                    maxLines = 1,
                    overflow =
                        TextOverflow.Ellipsis
                )
            }

            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )

            RaceInsightBadges(
                race =
                    race,
                dark =
                    true
            )

            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {
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

                    Text(
                        text =
                            "${race.horses.size} at",
                        color =
                            Color.White,
                        fontWeight =
                            FontWeight.Bold,
                        fontSize =
                            13.sp
                    )
                }

                Box(
                    modifier =
                        Modifier
                            .size(
                                38.dp
                            )
                            .background(
                                color =
                                    Color.White.copy(
                                        alpha = 0.14f
                                    ),
                                shape =
                                    RoundedCornerShape(
                                        12.dp
                                    )
                            ),
                    contentAlignment =
                        Alignment.Center
                ) {
                    Icon(
                        imageVector =
                            Icons.Default.KeyboardArrowRight,
                        contentDescription =
                            null,
                        tint =
                            Color.White
                    )
                }
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
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),
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
            androidx.compose.foundation.BorderStroke(
                1.dp,
                Border
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    0.dp
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
                shape =
                    RoundedCornerShape(
                        14.dp
                    ),
                color =
                    PaleGreen
            ) {
                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 13.dp,
                            vertical = 10.dp
                        ),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {
                    Text(
                        text =
                            time,
                        color =
                            Green,
                        fontSize =
                            15.sp,
                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Text(
                        text =
                            "${race.number}. K",
                        color =
                            Green,
                        fontSize =
                            10.sp,
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
                            start = 13.dp
                        )
            ) {
                Text(
                    text =
                        race.city,
                    color =
                        Ink,
                    fontSize =
                        15.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                if (
                    race.title.isNotBlank()
                ) {
                    Text(
                        text =
                            race.title,
                        color =
                            Muted,
                        fontSize =
                            12.sp,
                        maxLines = 1,
                        overflow =
                            TextOverflow.Ellipsis
                    )
                }

                Text(
                    text =
                        raceMeta(
                            race
                        ),
                    color =
                        Muted,
                    fontSize =
                        11.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            6.dp
                        )
                )

                RaceInsightBadges(
                    race =
                        race,
                    dark =
                        false
                )
            }

            Column(
                horizontalAlignment =
                    Alignment.End
            ) {
                Text(
                    text =
                        "${race.horses.size}",
                    color =
                        Ink,
                    fontSize =
                        15.sp,
                    fontWeight =
                        FontWeight.ExtraBold
                )

                Text(
                    text =
                        "at",
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
                    Muted,
                modifier =
                    Modifier.padding(
                        start = 8.dp
                    )
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
        shape =
            RoundedCornerShape(
                18.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    PaleGold
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    0.dp
            )
    ) {
        Row(
            modifier =
                Modifier.padding(
                    16.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Surface(
                color =
                    Gold,
                shape =
                    RoundedCornerShape(
                        14.dp
                    )
            ) {
                Icon(
                    imageVector =
                        Icons.Default.Stars,
                    contentDescription =
                        null,
                    tint =
                        Color.White,
                    modifier =
                        Modifier.padding(
                            11.dp
                        )
                )
            }

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(
                            start = 13.dp
                        )
            ) {
                Text(
                    text =
                        "Altılı Kupon",
                    color =
                        Ink,
                    fontSize =
                        16.sp,
                    fontWeight =
                        FontWeight.ExtraBold
                )

                Text(
                    text =
                        "Bütçene göre optimize edilmiş kupon",
                    color =
                        Muted,
                    fontSize =
                        12.sp
                )
            }

            Icon(
                imageVector =
                    Icons.Default.KeyboardArrowRight,
                contentDescription =
                    null,
                tint =
                    Gold
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
                .padding(
                    vertical = 48.dp
                ),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.Center
    ) {
        Text(
            text = "Two Horse",
            style =
                MaterialTheme.typography.titleMedium,
            color = Ink,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(
                    6.dp
                )
        )

        Text(
            text =
                message,
            color =
                Muted,
            fontSize =
                13.sp
        )
    }
}

private fun raceMeta(
    race: Race
): String {
    return listOf(
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
}


@Composable
private fun RaceInsightBadges(
    race: Race,
    dark: Boolean
) {
    val uncertainty =
        race.uncertainty

    val strategy =
        race.couponStrategy

    if (
        uncertainty == null &&
        strategy == null
    ) {
        return
    }

    Column(
        verticalArrangement =
            Arrangement.spacedBy(
                5.dp
            )
    ) {
        uncertainty?.let {
            CompactRaceBadge(
                text =
                    "Belirsizlik: ${
                        uncertaintyLabel(
                            it.level
                        )
                    }",
                background =
                    if (dark)
                        Color.White.copy(
                            alpha = 0.14f
                        )
                    else
                        uncertaintyBackground(
                            it.level
                        ),
                foreground =
                    if (dark)
                        Color.White
                    else
                        uncertaintyForeground(
                            it.level
                        )
            )
        }

        strategy?.let {
            CompactRaceBadge(
                text =
                    strategyLabel(
                        it.mode
                    ),
                background =
                    if (dark)
                        Color.White.copy(
                            alpha = 0.14f
                        )
                    else
                        PaleGreen,
                foreground =
                    if (dark)
                        Color.White
                    else
                        Green
            )
        }
    }
}

@Composable
private fun CompactRaceBadge(
    text: String,
    background: Color,
    foreground: Color
) {
    Surface(
        color =
            background,
        shape =
            RoundedCornerShape(
                50
            )
    ) {
        Text(
            text =
                text,
            modifier =
                Modifier.padding(
                    horizontal =
                        8.dp,
                    vertical =
                        4.dp
                ),
            color =
                foreground,
            fontSize =
                9.sp,
            fontWeight =
                FontWeight.ExtraBold,
            maxLines =
                1,
            overflow =
                TextOverflow.Ellipsis
        )
    }
}

private fun uncertaintyLabel(
    level: String
): String =
    when(level) {
        "low" ->
            "Düşük"

        "medium" ->
            "Orta"

        "high" ->
            "Yüksek"

        "very-high" ->
            "Çok yüksek"

        else ->
            level
    }

private fun strategyLabel(
    mode: String
): String =
    when(mode) {
        "single" ->
            "Tek adayı güçlü"

        "compact" ->
            "Dar kupon"

        "spread" ->
            "Geniş kupon"

        else ->
            mode
    }

private fun uncertaintyBackground(
    level: String
): Color =
    when(level) {
        "low" ->
            PaleGreen

        "medium" ->
            PaleGold

        "high",
        "very-high" ->
            Color(
                0xFFFFECEC
            )

        else ->
            Color(
                0xFFF2F4F3
            )
    }

private fun uncertaintyForeground(
    level: String
): Color =
    when(level) {
        "low" ->
            Green

        "medium" ->
            Gold

        "high",
        "very-high" ->
            Color(
                0xFFB42318
            )

        else ->
            Muted
    }

package com.twohorse.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.domain.model.*
import com.twohorse.app.ui.theme.*

@Composable
fun isCompactScreen(): Boolean =
    LocalConfiguration.current.screenWidthDp < 360

@Composable
fun isLandscapeScreen(): Boolean =
    LocalConfiguration.current.screenWidthDp >
        LocalConfiguration.current.screenHeightDp

@Composable
fun AnalyticsChip(
    text: String,
    strong: Boolean = false,
    danger: Boolean = false
) {
    Surface(
        color =
            when {
                danger -> PaleRed
                strong -> PaleGreen
                else -> Color(0xFFF2F4F3)
            },
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
            color =
                when {
                    danger -> Red
                    strong -> Green
                    else -> Muted
                },
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ScoreProgress(
    title: String,
    score: Double?,
    subtitle: String? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                color = Ink,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = score?.let {
                    "%.1f".format(it)
                } ?: "Veri yok",
                color =
                    if (score == null)
                        Muted
                    else
                        Green,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(
            Modifier.height(4.dp)
        )

        LinearProgressIndicator(
            progress = {
                (
                    (score ?: 0.0) /
                        100.0
                    )
                    .coerceIn(
                        0.0,
                        1.0
                    )
                    .toFloat()
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(6.dp),
            color =
                if (score == null)
                    Border
                else
                    Green,
            trackColor =
                Color(0xFFE8ECEA)
        )

        subtitle
            ?.takeIf {
                it.isNotBlank()
            }
            ?.let {
                Spacer(
                    Modifier.height(2.dp)
                )

                Text(
                    text = it,
                    color = Muted,
                    fontSize = 8.sp
                )
            }
    }
}

@Composable
fun MiniMetric(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    accent: Boolean = false
) {
    Surface(
        modifier = modifier,
        color =
            if (accent)
                PaleGreen
            else
                Color(0xFFF5F7F6),
        shape =
            RoundedCornerShape(11.dp)
    ) {
        Column(
            modifier =
                Modifier.padding(8.dp)
        ) {
            Text(
                text = label,
                color = Muted,
                fontSize = 8.sp
            )

            Text(
                text = value,
                color =
                    if (accent)
                        Green
                    else
                        Ink,
                fontSize = 11.sp,
                fontWeight =
                    FontWeight.ExtraBold,
                maxLines = 2,
                overflow =
                    TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun RaceInsightSummary(
    race: Race,
    dark: Boolean = false
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

    Spacer(
        Modifier.height(8.dp)
    )

    Column(
        verticalArrangement =
            Arrangement.spacedBy(4.dp)
    ) {
        uncertainty?.let {
            val level =
                when (
                    it.level.lowercase()
                ) {
                    "low" ->
                        "DÜŞÜK"

                    "medium" ->
                        "ORTA"

                    "high" ->
                        "YÜKSEK"

                    "very-high" ->
                        "ÇOK YÜKSEK"

                    else ->
                        it.level.uppercase()
                }

            val explanation =
                when {
                    it.topMargin <= 3.0 ->
                        "İlk adaylar birbirine çok yakın"

                    it.topMargin <= 7.0 ->
                        "İlk 3 at birbirine yakın"

                    else ->
                        "Model lideri belirgin şekilde ayrışıyor"
                }

            Text(
                text =
                    "BELİRSİZLİK $level · $explanation",
                color =
                    if (dark)
                        Color.White.copy(
                            alpha = 0.78f
                        )
                    else
                        Muted,
                fontSize = 10.sp,
                fontWeight =
                    FontWeight.Bold,
                maxLines = 2,
                overflow =
                    TextOverflow.Ellipsis
            )
        }

        strategy?.let {
            val mode =
                when (
                    it.mode.lowercase()
                ) {
                    "single" ->
                        "TEK"

                    "compact",
                    "narrow" ->
                        "DAR KUPON"

                    "spread",
                    "wide",
                    "broad" ->
                        "GENİŞ KUPON"

                    else ->
                        "DENGELİ KUPON"
                }

            val reason =
                when {
                    it.horseNumbers.size == 1 ->
                        "1 güçlü tek adayı"

                    it.horseNumbers.isNotEmpty() ->
                        "${it.horseNumbers.size} güçlü aday"

                    it.reason.isNotBlank() ->
                        it.reason

                    else ->
                        "Backend strateji önerisi"
                }

            Text(
                text =
                    "$mode · $reason",
                color =
                    if (dark)
                        Gold
                    else
                        Green,
                fontSize = 10.sp,
                fontWeight =
                    FontWeight.Bold,
                maxLines = 2,
                overflow =
                    TextOverflow.Ellipsis
            )
        }
    }
}

fun componentTitle(
    key: String
): String =
    when (
        key.lowercase()
    ) {
        "agf" -> "AGF"
        "expert" -> "Uzman"
        "form" -> "Form"
        "hp" -> "HP"
        "market" -> "Piyasa"
        "weight" -> "Kilo"
        "field" -> "Saha"
        else -> key
    }

fun uncertaintyText(
    value: String
): String =
    when (
        value.lowercase()
    ) {
        "low" -> "Düşük"
        "medium" -> "Orta"
        "high" -> "Yüksek"
        "very-high" -> "Çok yüksek"
        else -> value
    }

fun marketArrow(
    direction: String
): String =
    when (
        direction.lowercase()
    ) {
        "strong-up" -> "↑↑"
        "up" -> "↑"
        "flat" -> "→"
        "down" -> "↓"
        "strong-down" -> "↓↓"
        else -> "·"
    }

fun marketText(
    direction: String
): String =
    when (
        direction.lowercase()
    ) {
        "strong-up" ->
            "Güçlü yükseliş"

        "up" ->
            "Yükseliş"

        "flat" ->
            "Yatay"

        "down" ->
            "Düşüş"

        "strong-down" ->
            "Güçlü düşüş"

        else ->
            "Yeterli piyasa hareketi yok"
    }

@Composable
fun ShimmerBlock(
    height: Dp,
    modifier: Modifier = Modifier
) {
    val transition =
        rememberInfiniteTransition(
            label = "shimmer"
        )

    val offset by
        transition.animateFloat(
            initialValue = -400f,
            targetValue = 900f,
            animationSpec =
                infiniteRepeatable(
                    animation =
                        tween(
                            durationMillis = 1400
                        ),
                    repeatMode =
                        RepeatMode.Restart
                ),
            label = "shimmerOffset"
        )

    val brush =
        Brush.linearGradient(
            colors =
                listOf(
                    Color(0xFFE9ECEA),
                    Color(0xFFF7F8F7),
                    Color(0xFFE9ECEA)
                ),
            start =
                androidx.compose.ui.geometry.Offset(
                    offset - 250f,
                    0f
                ),
            end =
                androidx.compose.ui.geometry.Offset(
                    offset,
                    200f
                )
        )

    Spacer(
        modifier =
            modifier
                .fillMaxWidth()
                .height(height)
                .background(
                    brush =
                        brush,
                    shape =
                        RoundedCornerShape(
                            18.dp
                        )
                )
    )
}

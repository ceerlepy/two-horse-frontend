package com.twohorse.app.ui.race

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.ui.theme.*

/*
 * Building blocks for the race screen: a sand-toned card, and inset
 * boxes inside it so each area (experts, market, form...) reads as its
 * own small block instead of one long list of lines.
 */

@Composable
internal fun ToneCard(
    modifier: Modifier = Modifier,
    borderColor: Color = CardToneBorder,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = CardTone
            ),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(content = content)
    }
}

@Composable
internal fun InsetBox(
    modifier: Modifier = Modifier,
    title: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    padding: Dp = 11.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = InsetTone,
        border = BorderStroke(1.dp, InsetToneBorder),
        shape = RoundedCornerShape(13.dp)
    ) {
        Column(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            if (title != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InsetTitle(
                        text = title,
                        modifier = Modifier.weight(1f)
                    )

                    trailing?.invoke()
                }
            }

            content()
        }
    }
}

@Composable
internal fun InsetTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = Ink,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
internal fun ToneDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = InsetToneBorder
    )
}

/* A small label/value cell, e.g. "AGF  %24,5". */
@Composable
internal fun InsetMetric(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    accent: Boolean = false
) {
    Surface(
        modifier = modifier,
        color = if (accent) PaleGreen else InsetTone,
        border =
            BorderStroke(
                1.dp,
                if (accent) Green.copy(alpha = 0.25f) else InsetToneBorder
            ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier =
                Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 8.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = Muted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Long values ("ÇOK YÜKSEK") get a smaller size and a second line.
            val long = value.length > 8

            Text(
                text = value,
                color = if (accent) Green else Ink,
                fontSize = if (long) 12.sp else 15.sp,
                lineHeight = if (long) 14.sp else 18.sp,
                fontWeight = FontWeight.Black,
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/* Horse number in a rounded square. */
@Composable
internal fun NumberBadge(
    number: Int,
    highlighted: Boolean = false,
    size: Dp = 34.dp
) {
    Surface(
        modifier = Modifier.size(size),
        color = if (highlighted) Green else Ink,
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = number.toString(),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

/* Finishing position in a small circle: gold for a win, green for places. */
@Composable
internal fun PositionDot(
    position: Int?,
    label: String? = null
) {
    val (background, foreground) =
        when (position) {
            1 -> Gold to Color.White
            2, 3 -> PaleGreen to Green
            null -> InsetTone to Muted
            else -> Color(0xFFE6E1D8) to Ink
        }

    Surface(
        modifier = Modifier.size(26.dp),
        color = background,
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label ?: position?.toString() ?: "–",
                color = foreground,
                fontSize = if ((label?.length ?: 0) > 2) 9.sp else 11.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

/* A tappable card header with title, subtitle and an arrow. */
@Composable
internal fun ExpandableToneHeader(
    title: String,
    subtitle: String,
    expanded: Boolean,
    openDescription: String,
    closeDescription: String,
    onClick: () -> Unit
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                color = Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                text = subtitle,
                color = Muted,
                fontSize = 11.sp
            )
        }

        Surface(
            color = InsetTone,
            shape = CircleShape
        ) {
            Icon(
                if (expanded)
                    Icons.Default.KeyboardArrowUp
                else
                    Icons.Default.KeyboardArrowDown,
                contentDescription =
                    if (expanded) closeDescription else openDescription,
                modifier = Modifier.padding(4.dp),
                tint = Ink
            )
        }
    }
}

/* Small rounded tag used for split times, dates and similar facts. */
@Composable
internal fun FactTag(
    text: String,
    strong: Boolean = false
) {
    Surface(
        color = if (strong) Ink else Color.White.copy(alpha = 0.7f),
        border =
            if (strong) null
            else BorderStroke(1.dp, InsetToneBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier =
                Modifier.padding(
                    horizontal = 7.dp,
                    vertical = 3.dp
                ),
            color = if (strong) Color.White else Ink,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

/*
 * TJK's "son 6 yarış" string, e.g. "735564": one digit per race,
 * oldest on the left, newest on the right; 0 means tenth or worse.
 * Returns finishing positions with 10 standing for "10+".
 */
internal fun parseRecentForm(raw: String): List<Int> =
    raw.filter { it.isDigit() }
        .map { if (it == '0') 10 else it.digitToInt() }

@Composable
internal fun RecentFormDots(
    raw: String,
    tenPlusLabel: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        parseRecentForm(raw).forEach {
            PositionDot(
                position = it,
                label = if (it >= 10) tenPlusLabel else null
            )
        }
    }
}

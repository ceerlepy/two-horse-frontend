package com.twohorse.app.ui.coupons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.ui.theme.*

/*
 * Shared look of a coupon card: a deep green header band with white
 * text and a gold amount pill, over a white body with the legs. Used
 * by the coupon screen and by "Kuponlarım" so a saved coupon looks
 * like the one the member built.
 */
internal val CouponHeaderBrush =
    Brush.linearGradient(
        listOf(
            Color(0xFF0A3F2B),
            Green,
            Green2
        )
    )

/* Header for a coupon whose legs all hit. */
internal val CouponWinBrush =
    Brush.linearGradient(
        listOf(
            Color(0xFF8A5A0B),
            Gold,
            Color(0xFFF0C25A)
        )
    )

internal val CouponHeaderSubtle = Color(0xFFCFE9DC)

@Composable
internal fun CouponCardHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    brush: Brush = CouponHeaderBrush,
    trailing: @Composable () -> Unit = {}
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(brush)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = subtitle,
                color = CouponHeaderSubtle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        trailing()
    }
}

@Composable
internal fun CouponPill(
    text: String,
    background: Color = Gold,
    content: Color = Color(0xFF2B1D03)
) {
    Surface(
        color = background,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
            color = content,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

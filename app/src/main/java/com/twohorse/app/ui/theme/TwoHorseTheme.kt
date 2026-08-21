package com.twohorse.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

val Bg = Color(0xFFF4F6F5)
val Surface = Color(0xFFFFFFFF)
val Ink = Color(0xFF111815)
val Muted = Color(0xFF69736E)

val Green = Color(0xFF0E6B47)
val Green2 = Color(0xFF17865A)
val PaleGreen = Color(0xFFE5F4EC)

val Gold = Color(0xFFD89B2B)
val PaleGold = Color(0xFFFFF3D9)

val Red = Color(0xFFB64A3A)
val PaleRed = Color(0xFFFFECE8)

val Border = Color(0xFFE1E7E3)
val LoadingBlue = Color(0xFF1976D2)

private val TwoHorseColors =
    lightColorScheme(
        primary = Green,
        onPrimary = Color.White,
        secondary = Gold,
        onSecondary = Ink,
        background = Bg,
        onBackground = Ink,
        surface = Surface,
        onSurface = Ink,
        outline = Border,
        error = Red
    )

private val TwoHorseTypography =
    Typography(
        bodyLarge =
            TextStyle(
                fontSize = 15.sp,
                lineHeight = 21.sp
            ),
        bodyMedium =
            TextStyle(
                fontSize = 13.sp,
                lineHeight = 18.sp
            ),
        bodySmall =
            TextStyle(
                fontSize = 11.sp,
                lineHeight = 16.sp
            ),
        titleLarge =
            TextStyle(
                fontSize = 23.sp,
                lineHeight = 28.sp,
                fontWeight =
                    FontWeight.Black
            ),
        titleMedium =
            TextStyle(
                fontSize = 18.sp,
                lineHeight = 23.sp,
                fontWeight =
                    FontWeight.Bold
            )
    )

@Composable
fun TwoHorseTheme(
    content: @Composable () -> Unit
) {
    val view =
        LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window =
                (
                    view.context
                        as? Activity
                )
                    ?.window
                    ?: return@SideEffect

            window.statusBarColor =
                Bg.toArgb()

            window.navigationBarColor =
                Bg.toArgb()

            WindowCompat
                .getInsetsController(
                    window,
                    view
                )
                .apply {
                    isAppearanceLightStatusBars =
                        true

                    isAppearanceLightNavigationBars =
                        true
                }
        }
    }

    MaterialTheme(
        colorScheme =
            TwoHorseColors,
        typography =
            TwoHorseTypography,
        content =
            content
    )
}

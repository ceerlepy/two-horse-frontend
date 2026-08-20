package com.twohorse.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
        secondary = Gold,
        background = Bg,
        surface = Surface,
        onSurface = Ink
    )

@Composable
fun TwoHorseTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TwoHorseColors,
        content = content
    )
}

package com.twohorse.app

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.twohorse.app.ui.theme.Bg
import com.twohorse.app.ui.home.HomeScreen

@Composable
fun TwoHorseApp() {
    Surface(
        modifier =
            Modifier.fillMaxSize(),

        color =
            Bg
    ) {
        HomeScreen()
    }
}

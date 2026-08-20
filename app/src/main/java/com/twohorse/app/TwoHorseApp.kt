package com.twohorse.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.twohorse.app.domain.model.Race
import com.twohorse.app.ui.home.HomeScreen
import com.twohorse.app.ui.race.RaceDetailScreen
import com.twohorse.app.ui.theme.Bg

private sealed interface AppScreen {
    data object Home :
        AppScreen

    data class RaceDetail(
        val race: Race
    ) :
        AppScreen
}

@Composable
fun TwoHorseApp() {
    var screen by
        remember {
            mutableStateOf<AppScreen>(
                AppScreen.Home
            )
        }

    BackHandler(
        enabled =
            screen !is
            AppScreen.Home
    ) {
        screen =
            AppScreen.Home
    }

    Surface(
        modifier =
            Modifier.fillMaxSize(),
        color =
            Bg
    ) {
        when (
            val current =
                screen
        ) {
            AppScreen.Home -> {
                HomeScreen(
                    onRaceClick = {
                        race ->
                        screen =
                            AppScreen.RaceDetail(
                                race
                            )
                    }
                )
            }

            is AppScreen.RaceDetail -> {
                RaceDetailScreen(
                    race =
                        current.race,
                    onBack = {
                        screen =
                            AppScreen.Home
                    }
                )
            }
        }
    }
}

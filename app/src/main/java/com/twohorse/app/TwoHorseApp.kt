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
import com.twohorse.app.ui.coupons.CouponScreen
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

    data class Coupons(
        val cities: List<String>,
        val selectedCity: String?
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
                    },
                    onSixFoldClick = {
                        cities,
                        selectedCity ->

                        screen =
                            AppScreen.Coupons(
                                cities =
                                    cities,
                                selectedCity =
                                    selectedCity
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

            is AppScreen.Coupons -> {
                CouponScreen(
                    cities =
                        current.cities,
                    initialCity =
                        current.selectedCity,
                    onBack = {
                        screen =
                            AppScreen.Home
                    }
                )
            }
        }
    }
}

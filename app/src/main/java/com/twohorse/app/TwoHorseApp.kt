package com.twohorse.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.twohorse.app.domain.model.HistoryRace
import com.twohorse.app.domain.model.Race
import com.twohorse.app.ui.coupons.CouponScreen
import com.twohorse.app.ui.history.HistoryDetailScreen
import com.twohorse.app.ui.history.HistoryScreen
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
        val selectedCity: String?,
        val returnRace: Race? = null
    ) :
        AppScreen

    data object History :
        AppScreen

    data class HistoryDetail(
        val race: HistoryRace
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

    fun couponBack(
        coupons: AppScreen.Coupons
    ) {
        screen =
            coupons.returnRace
                ?.let {
                    AppScreen.RaceDetail(
                        it
                    )
                }
                ?: AppScreen.Home
    }

    BackHandler(
        enabled =
            screen !is
            AppScreen.Home
    ) {
        screen =
            when (
                val current =
                    screen
            ) {
                is AppScreen.HistoryDetail ->
                    AppScreen.History

                is AppScreen.Coupons ->
                    current.returnRace
                        ?.let {
                            AppScreen.RaceDetail(
                                it
                            )
                        }
                        ?: AppScreen.Home

                else ->
                    AppScreen.Home
            }
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
                                    selectedCity,
                                returnRace =
                                    null
                            )
                    },

                    onHistoryClick = {
                        screen =
                            AppScreen.History
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
                    },

                    onOpenCoupons = {
                        city ->

                        screen =
                            AppScreen.Coupons(
                                cities =
                                    listOf(
                                        city
                                    ),
                                selectedCity =
                                    city,
                                returnRace =
                                    current.race
                            )
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
                        couponBack(
                            current
                        )
                    }
                )
            }

            AppScreen.History -> {
                HistoryScreen(
                    onBack = {
                        screen =
                            AppScreen.Home
                    },

                    onRaceClick = {
                        race ->
                        screen =
                            AppScreen.HistoryDetail(
                                race
                            )
                    }
                )
            }

            is AppScreen.HistoryDetail -> {
                HistoryDetailScreen(
                    historyRace =
                        current.race,

                    onBack = {
                        screen =
                            AppScreen.History
                    }
                )
            }
        }
    }
}

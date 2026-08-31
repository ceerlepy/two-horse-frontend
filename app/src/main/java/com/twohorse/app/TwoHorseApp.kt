package com.twohorse.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.domain.model.HistoryRace
import com.twohorse.app.domain.model.MembershipUser
import com.twohorse.app.domain.model.Race
import com.twohorse.app.ui.account.AccountScreen
import com.twohorse.app.ui.auth.LoginScreen
import com.twohorse.app.ui.coupons.CouponScreen
import com.twohorse.app.ui.history.HistoryDetailScreen
import com.twohorse.app.ui.history.HistoryScreen
import com.twohorse.app.ui.home.HomeScreen
import com.twohorse.app.ui.race.RaceDetailScreen
import com.twohorse.app.ui.theme.Bg
import com.twohorse.app.ui.theme.Green

private sealed interface AppScreen {
    data object Loading :
        AppScreen

    data object Login :
        AppScreen

    data object Home :
        AppScreen

    data object Account :
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
    val context = LocalContext.current

    val repository =
        remember {
            TwoHorseRepository(context)
        }

    var screen by
        remember {
            mutableStateOf<AppScreen>(
                AppScreen.Loading
            )
        }

    var currentUser by
        remember {
            mutableStateOf<MembershipUser?>(
                null
            )
        }

    LaunchedEffect(Unit) {
        if (!repository.hasSession) {
            screen = AppScreen.Login
            return@LaunchedEffect
        }

        repository.me()
            .onSuccess { user ->
                currentUser = user
                screen = AppScreen.Home
            }
            .onFailure {
                repository.logout()
                screen = AppScreen.Login
            }
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
            screen !is AppScreen.Home &&
            screen !is AppScreen.Login &&
            screen !is AppScreen.Loading
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
            AppScreen.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Green
                    )
                }
            }

            AppScreen.Login -> {
                LoginScreen(
                    repository = repository,
                    onLoginSuccess = { user ->
                        currentUser = user
                        screen = AppScreen.Home
                    }
                )
            }

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
                    },

                    onAccountClick = {
                        screen =
                            AppScreen.Account
                    }
                )
            }

            AppScreen.Account -> {
                AccountScreen(
                    repository = repository,
                    initialUser = currentUser,
                    onUserUpdated = { user ->
                        currentUser = user
                    },
                    onBack = {
                        screen = AppScreen.Home
                    },
                    onLoggedOut = {
                        currentUser = null
                        screen = AppScreen.Login
                    }
                )
            }

            is AppScreen.RaceDetail -> {
                RaceDetailScreen(
                    race =
                        current.race,

                    currentUser =
                        currentUser,

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

                    currentUser =
                        currentUser,

                    onBack = {
                        couponBack(
                            current
                        )
                    },

                    onUpgradeClick = {
                        screen = AppScreen.Account
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

package com.twohorse.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
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
import com.twohorse.app.ui.coupons.CouponHistoryScreen
import com.twohorse.app.ui.coupons.MyCouponsScreen
import com.twohorse.app.ui.coupons.CouponScreen
import com.twohorse.app.ui.history.HistoryDetailScreen
import com.twohorse.app.ui.history.HistoryScreen
import com.twohorse.app.ui.foreign.ForeignScreen
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

    data class CouponHistory(
        val returnTo: Coupons
    ) :
        AppScreen

    data class MyCoupons(
        /* null: opened from the home screen. */
        val returnTo: Coupons?
    ) :
        AppScreen

    data object Foreign :
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

                is AppScreen.CouponHistory ->
                    current.returnTo

                is AppScreen.MyCoupons ->
                    current.returnTo ?: AppScreen.Home

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
        /*
         * targetSdk 35 draws the app edge to edge: the status bar
         * (clock, battery) sits over the top of the window and the
         * navigation bar (back/home/menu) over the bottom. Pad every
         * screen inside both so headers stay readable and bottom
         * buttons stay tappable. This consumes the insets, so the
         * screens' own statusBarsPadding() calls become no-ops.
         */
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
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
                            // Members see their own saved coupons; the
                            // model's race-by-race record is for us.
                            screen =
                                if (Config.SHOW_MODEL_PERFORMANCE)
                                    AppScreen.History
                                else
                                    AppScreen.MyCoupons(null)
                        },

                        onAccountClick = {
                            screen =
                                AppScreen.Account
                        },

                        onForeignClick = {
                            screen =
                                AppScreen.Foreign
                        }
                    )
                }

                AppScreen.Foreign -> {
                    ForeignScreen(
                        onBack = {
                            screen =
                                AppScreen.Home
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
                        },

                        onUpgradeClick = {
                            screen = AppScreen.Account
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
                        },

                        onOpenHistory = {
                            screen = AppScreen.CouponHistory(current)
                        },

                        onOpenMyCoupons = {
                            screen = AppScreen.MyCoupons(current)
                        }
                    )
                }

                is AppScreen.MyCoupons -> {
                    MyCouponsScreen(
                        onBack = {
                            screen = current.returnTo ?: AppScreen.Home
                        },
                        onUpgradeClick = {
                            screen = AppScreen.Account
                        }
                    )
                }

                is AppScreen.CouponHistory -> {
                    CouponHistoryScreen(
                        onBack = {
                            screen = current.returnTo
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
}

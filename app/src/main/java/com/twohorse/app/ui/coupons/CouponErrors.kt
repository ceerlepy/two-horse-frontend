package com.twohorse.app.ui.coupons

import androidx.compose.runtime.Composable
import com.twohorse.app.data.api.ApiException
import com.twohorse.app.i18n.Strings

sealed interface CouponError {
    data object CityRequired : CouponError
    data object ValidBudgetRequired : CouponError
    data object SixfoldNotFound : CouponError
    data object NotEnoughRaces : CouponError
    data object NoRunners : CouponError
    data object TierUpgradeRequired : CouponError
    data object BudgetCapExceeded : CouponError
    data object AuthRequired : CouponError
    data object NotFound : CouponError
    data object BadRequest : CouponError
    data object ServerUnavailable : CouponError
    data object NoInternet : CouponError
    data object Timeout : CouponError
    data object Generic : CouponError
    data object CitySelectFailed : CouponError
    data object UnexpectedWindow : CouponError
    data object BudgetExceeded : CouponError
}

@Composable
fun couponErrorText(
    error: CouponError,
    strings: Strings
): String =
    when (error) {
        CouponError.CityRequired ->
            strings.couponErrorCityRequired

        CouponError.ValidBudgetRequired ->
            strings.couponErrorValidBudgetRequired

        CouponError.SixfoldNotFound ->
            strings.couponErrorSixfoldNotFound

        CouponError.NotEnoughRaces ->
            strings.couponErrorNotEnoughRaces

        CouponError.NoRunners ->
            strings.couponErrorNoRunners

        CouponError.TierUpgradeRequired ->
            strings.couponErrorTierUpgradeRequired

        CouponError.BudgetCapExceeded ->
            strings.couponErrorBudgetCapExceeded

        CouponError.AuthRequired ->
            strings.couponErrorAuthRequired

        CouponError.NotFound ->
            strings.couponErrorNotFound

        CouponError.BadRequest ->
            strings.couponErrorBadRequest

        CouponError.ServerUnavailable ->
            strings.couponErrorServerUnavailable

        CouponError.NoInternet ->
            strings.couponErrorNoInternet

        CouponError.Timeout ->
            strings.couponErrorTimeout

        CouponError.Generic ->
            strings.couponErrorGeneric

        CouponError.CitySelectFailed ->
            strings.couponCitySelectFailed

        CouponError.UnexpectedWindow ->
            strings.couponUnexpectedWindow

        CouponError.BudgetExceeded ->
            strings.couponBudgetExceeded
    }

fun couponErrorFromThrowable(throwable: Throwable): CouponError {
    val api = throwable as? ApiException

    return when (api?.apiCode) {
        "CITY_REQUIRED" ->
            CouponError.CityRequired

        "VALID_BUDGET_REQUIRED" ->
            CouponError.ValidBudgetRequired

        "SIXFOLD_WINDOW_NOT_FOUND",
        "SIXFOLD_NOT_FOUND",
        "NO_SIXFOLD_WINDOW" ->
            CouponError.SixfoldNotFound

        "NOT_ENOUGH_RACES",
        "INSUFFICIENT_RACES" ->
            CouponError.NotEnoughRaces

        "NO_RUNNERS",
        "NO_USABLE_RUNNERS" ->
            CouponError.NoRunners

        "TIER_UPGRADE_REQUIRED" ->
            CouponError.TierUpgradeRequired

        "TIER_BUDGET_CAP_EXCEEDED" ->
            CouponError.BudgetCapExceeded

        "AUTH_REQUIRED" ->
            CouponError.AuthRequired

        else -> when {
            api?.statusCode == 404 ->
                CouponError.NotFound

            api?.statusCode == 400 ->
                CouponError.BadRequest

            api?.statusCode != null && api.statusCode >= 500 ->
                CouponError.ServerUnavailable

            throwable.message?.contains(
                "Unable to resolve host",
                ignoreCase = true
            ) == true ->
                CouponError.NoInternet

            throwable.message?.contains(
                "timeout",
                ignoreCase = true
            ) == true ->
                CouponError.Timeout

            else ->
                CouponError.Generic
        }
    }
}

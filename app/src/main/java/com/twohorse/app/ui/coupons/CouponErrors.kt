package com.twohorse.app.ui.coupons

import com.twohorse.app.data.api.ApiException
import com.twohorse.app.i18n.Strings

fun couponErrorMessage(throwable: Throwable, strings: Strings): String {
    val api = throwable as? ApiException

    return when (api?.apiCode) {
        "CITY_REQUIRED" ->
            strings.couponErrorCityRequired

        "VALID_BUDGET_REQUIRED" ->
            strings.couponErrorValidBudgetRequired

        "SIXFOLD_WINDOW_NOT_FOUND",
        "SIXFOLD_NOT_FOUND",
        "NO_SIXFOLD_WINDOW" ->
            strings.couponErrorSixfoldNotFound

        "NOT_ENOUGH_RACES",
        "INSUFFICIENT_RACES" ->
            strings.couponErrorNotEnoughRaces

        "NO_RUNNERS",
        "NO_USABLE_RUNNERS" ->
            strings.couponErrorNoRunners

        "TIER_UPGRADE_REQUIRED" ->
            strings.couponErrorTierUpgradeRequired

        "TIER_BUDGET_CAP_EXCEEDED" ->
            strings.couponErrorBudgetCapExceeded

        "AUTH_REQUIRED" ->
            strings.couponErrorAuthRequired

        else -> when {
            api?.statusCode == 404 ->
                strings.couponErrorNotFound

            api?.statusCode == 400 ->
                strings.couponErrorBadRequest

            api?.statusCode != null && api.statusCode >= 500 ->
                strings.couponErrorServerUnavailable

            throwable.message?.contains(
                "Unable to resolve host",
                ignoreCase = true
            ) == true ->
                strings.couponErrorNoInternet

            throwable.message?.contains(
                "timeout",
                ignoreCase = true
            ) == true ->
                strings.couponErrorTimeout

            else ->
                strings.couponErrorGeneric
        }
    }
}

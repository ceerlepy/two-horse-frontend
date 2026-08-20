package com.twohorse.app.data.repository

import com.twohorse.app.data.api.TwoHorseApi
import com.twohorse.app.domain.model.CouponResult
import com.twohorse.app.domain.model.TodayData

class TwoHorseRepository(
    private val api:
        TwoHorseApi =
        TwoHorseApi()
) {
    suspend fun today():
        Result<TodayData> =
        runCatching {
            api.getToday()
        }

    suspend fun coupons(
        city: String,
        budgetTl: Double,
        sixfold: Int,
        multiplier: Int = 1
    ): Result<CouponResult> =
        runCatching {
            api.getCoupons(
                city =
                    city,

                budgetTl =
                    budgetTl,

                sixfold =
                    sixfold,

                multiplier =
                    multiplier
            )
        }
}

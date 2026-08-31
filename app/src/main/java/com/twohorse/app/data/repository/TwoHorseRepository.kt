package com.twohorse.app.data.repository

import com.twohorse.app.data.api.TwoHorseApi
import com.twohorse.app.domain.model.CouponResult
import com.twohorse.app.domain.model.HistoryRace
import com.twohorse.app.domain.model.HorseVideo
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

    suspend fun history():
        Result<List<HistoryRace>> =
        runCatching {
            api.getHistory()
        }

    suspend fun horseVideos(
        raceDate: String,
        city: String,
        raceNumber: Int,
        horseNumber: Int
    ): Result<List<HorseVideo>> =
        runCatching {
            api.getHorseVideos(
                raceDate =
                    raceDate,

                city =
                    city,

                raceNumber =
                    raceNumber,

                horseNumber =
                    horseNumber
            )
        }

    suspend fun coupons(
        city: String,
        budgetTl: Double,
        sixfold: Int,
        multiplier: Int = 1,
        pool: String = "sixfold"
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
                    multiplier,

                pool =
                    pool
            )
        }
}

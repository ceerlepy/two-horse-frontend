package com.twohorse.app.data.repository

import android.content.Context
import com.twohorse.app.data.api.TwoHorseApi
import com.twohorse.app.data.auth.SessionStore
import com.twohorse.app.domain.model.CouponResult
import com.twohorse.app.domain.model.ForeignMeeting
import com.twohorse.app.domain.model.HistoryRace
import com.twohorse.app.domain.model.HorseVideo
import com.twohorse.app.domain.model.MembershipUser
import com.twohorse.app.domain.model.RaceForm
import com.twohorse.app.domain.model.RaceTraining
import com.twohorse.app.domain.model.TodayData

class TwoHorseRepository(
    context: Context
) {
    private val sessionStore =
        SessionStore(
            context.applicationContext
        )

    private val api =
        TwoHorseApi(
            tokenProvider = {
                sessionStore.getToken()
            }
        )

    val hasSession: Boolean
        get() =
            sessionStore.getToken() != null

    suspend fun loginWithGoogle(
        idToken: String
    ): Result<MembershipUser> =
        runCatching {
            val result =
                api.authGoogle(
                    idToken
                )

            sessionStore.saveToken(
                result.token
            )

            result.user
        }

    suspend fun loginWithPassword(
        email: String,
        password: String
    ): Result<MembershipUser> =
        runCatching {
            val result =
                api.authPassword(
                    email,
                    password
                )

            sessionStore.saveToken(
                result.token
            )

            result.user
        }

    suspend fun me():
        Result<MembershipUser> =
        runCatching {
            api.authMe()
        }

    fun logout() {
        sessionStore.clear()
    }

    suspend fun verifyPurchase(
        productId: String,
        purchaseToken: String
    ): Result<MembershipUser> =
        runCatching {
            api.verifyPurchase(
                productId,
                purchaseToken
            )
        }

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

    suspend fun foreignMeetings():
        Result<List<ForeignMeeting>> =
        runCatching {
            api.getForeignMeetings()
        }

    suspend fun raceForm(
        raceDate: String,
        city: String,
        raceNumber: Int
    ): Result<RaceForm> =
        runCatching {
            api.getRaceForm(
                raceDate = raceDate,
                city = city,
                raceNumber = raceNumber
            )
        }

    suspend fun raceTraining(
        raceDate: String,
        city: String,
        raceNumber: Int
    ): Result<RaceTraining> =
        runCatching {
            api.getRaceTraining(
                raceDate =
                    raceDate,

                city =
                    city,

                raceNumber =
                    raceNumber
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

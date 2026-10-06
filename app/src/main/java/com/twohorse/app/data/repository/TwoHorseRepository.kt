package com.twohorse.app.data.repository

import android.content.Context
import com.twohorse.app.data.api.TwoHorseApi
import com.twohorse.app.data.auth.SessionStore
import com.twohorse.app.domain.model.AskAnswer
import com.twohorse.app.domain.model.Coupon
import com.twohorse.app.domain.model.CouponHistoryEntry
import com.twohorse.app.domain.model.CouponResult
import com.twohorse.app.domain.model.ForeignMeeting
import com.twohorse.app.domain.model.HistoryRace
import com.twohorse.app.domain.model.HorseVideo
import com.twohorse.app.domain.model.MembershipUser
import com.twohorse.app.domain.model.MyCoupon
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

    suspend fun register(
        email: String,
        password: String,
        displayName: String?
    ): Result<MembershipUser> =
        runCatching {
            val result =
                api.authRegister(
                    email,
                    password,
                    displayName
                )

            sessionStore.saveToken(
                result.token
            )

            result.user
        }

    suspend fun deleteAccount(): Result<Unit> =
        runCatching {
            api.deleteAccount()
            sessionStore.clear()
        }

    /* refreshSubscription re-asks Google (cancel / plan switch made in Play). */
    suspend fun me(
        refreshSubscription: Boolean = false
    ):
        Result<MembershipUser> =
        runCatching {
            api.authMe(refreshSubscription)
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

    suspend fun couponHistory(
        days: Int = 30
    ): Result<List<CouponHistoryEntry>> =
        runCatching {
            api.getCouponHistory(days)
        }

    suspend fun myCoupons(): Result<List<MyCoupon>> =
        runCatching {
            api.getMyCoupons()
        }

    suspend fun saveMyCoupon(
        result: CouponResult,
        coupon: Coupon
    ): Result<Long> =
        runCatching {
            api.saveMyCoupon(result, coupon)
        }

    suspend fun deleteMyCoupon(
        id: Long
    ): Result<Unit> =
        runCatching {
            api.deleteMyCoupon(id)
        }

    suspend fun ask(
        city: String,
        raceNumber: Int,
        question: String,
        language: String
    ): Result<AskAnswer> =
        runCatching {
            api.ask(city, raceNumber, question, language)
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

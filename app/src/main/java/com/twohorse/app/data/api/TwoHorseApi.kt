package com.twohorse.app.data.api

import com.twohorse.app.domain.model.Coupon
import com.twohorse.app.domain.model.CouponHorse
import com.twohorse.app.domain.model.CouponLeg
import com.twohorse.app.domain.model.CouponResult
import com.twohorse.app.domain.model.Horse
import com.twohorse.app.domain.model.HistoryRace
import com.twohorse.app.domain.model.Meeting
import com.twohorse.app.domain.model.Race
import com.twohorse.app.domain.model.TodayData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class TwoHorseApi(
    private val baseUrl: String =
        "https://two-horse-backend.veyseltosun-vt.workers.dev"
) {
    private val client =
        OkHttpClient.Builder()
            .connectTimeout(
                8,
                TimeUnit.SECONDS
            )
            .readTimeout(
                15,
                TimeUnit.SECONDS
            )
            .callTimeout(
                20,
                TimeUnit.SECONDS
            )
            .retryOnConnectionFailure(true)
            .build()

    suspend fun getToday(): TodayData =
        withContext(
            Dispatchers.IO
        ) {
            val json =
                getJson(
                    "/api/today"
                )

            val date =
                json.optString(
                    "date"
                )

            val meetingsArray =
                json.optJSONArray(
                    "meetings"
                )

            val meetings =
                buildList {
                    if (
                        meetingsArray != null
                    ) {
                        for (
                            i in 0 until
                            meetingsArray.length()
                        ) {
                            val meeting =
                                meetingsArray
                                    .getJSONObject(i)

                            add(
                                parseMeeting(
                                    meeting,
                                    date
                                )
                            )
                        }
                    }
                }

            TodayData(
                date = date,
                meetings = meetings
            )
        }

    suspend fun getHistory():
        List<HistoryRace> =
        withContext(
            Dispatchers.IO
        ) {
            val json =
                getJson(
                    "/api/history"
                )

            val history =
                json.optJSONArray(
                    "history"
                )

            buildList {
                if (
                    history != null
                ) {
                    for (
                        i in 0 until
                        history.length()
                    ) {
                        val item =
                            history
                                .getJSONObject(i)

                        val runnersJson =
                            item.optJSONArray(
                                "runners"
                            )

                        val runners =
                            buildList {
                                if (
                                    runnersJson != null
                                ) {
                                    for (
                                        j in 0 until
                                        runnersJson.length()
                                    ) {
                                        add(
                                            parseHorse(
                                                runnersJson
                                                    .getJSONObject(j)
                                            )
                                        )
                                    }
                                }
                            }

                        val experts =
                            item.optJSONArray(
                                "expertPredictions"
                            )

                        add(
                            HistoryRace(
                                raceDate =
                                    item.optString(
                                        "raceDate"
                                    ),

                                city =
                                    item.optString(
                                        "city"
                                    ),

                                raceNumber =
                                    item.optInt(
                                        "raceNumber"
                                    ),

                                startTime =
                                    item.optString(
                                        "startTime"
                                    ),

                                startsAt =
                                    item.firstString(
                                        "startsAt"
                                    ),

                                distanceMeters =
                                    item.firstInt(
                                        "distanceMeters"
                                    ),

                                track =
                                    item.optString(
                                        "track"
                                    ),

                                runners =
                                    runners,

                                expertPredictionCount =
                                    experts?.length()
                                        ?: 0,

                                finalizedAt =
                                    item.firstString(
                                        "finalizedAt"
                                    )
                            )
                        )
                    }
                }
            }
        }

    suspend fun getCoupons(
        city: String,
        budgetTl: Double,
        sixfold: Int = 1,
        multiplier: Int = 1
    ): CouponResult =
        withContext(
            Dispatchers.IO
        ) {
            val url =
                (
                    baseUrl +
                    "/api/coupons/generate"
                )
                    .toHttpUrl()
                    .newBuilder()
                    .addQueryParameter(
                        "city",
                        city
                    )
                    .addQueryParameter(
                        "budgetTl",
                        budgetTl.toString()
                    )
                    .addQueryParameter(
                        "sixfold",
                        sixfold.toString()
                    )
                    .addQueryParameter(
                        "multiplier",
                        multiplier.toString()
                    )
                    .build()

            val json =
                execute(
                    Request.Builder()
                        .url(url)
                        .get()
                        .build()
                )

            val couponsArray =
                json.optJSONArray(
                    "coupons"
                )

            val coupons =
                buildList {
                    if (
                        couponsArray != null
                    ) {
                        for (
                            i in 0 until
                            couponsArray.length()
                        ) {
                            add(
                                parseCoupon(
                                    couponsArray
                                        .getJSONObject(i)
                                )
                            )
                        }
                    }
                }

            CouponResult(
                city =
                    json.optString(
                        "city",
                        city
                    ),

                sixfold =
                    json.optInt(
                        "sixfold",
                        sixfold
                    ),

                startRace =
                    json.optNullableInt(
                        "startRace"
                    ),

                endRace =
                    json.optNullableInt(
                        "endRace"
                    ),

                budgetTl =
                    json.optDouble(
                        "budgetTl",
                        budgetTl
                    ),

                coupons =
                    coupons
            )
        }

    private fun getJson(
        path: String
    ): JSONObject {
        val request =
            Request.Builder()
                .url(
                    baseUrl +
                    path
                )
                .get()
                .build()

        return execute(
            request
        )
    }

    private fun execute(
        request: Request
    ): JSONObject {
        client
            .newCall(request)
            .execute()
            .use { response ->
                val body =
                    response.body
                        ?.string()
                        .orEmpty()

                if (
                    !response.isSuccessful
                ) {
                    throw IllegalStateException(
                        "HTTP ${response.code}: $body"
                    )
                }

                return JSONObject(
                    body
                )
            }
    }

    private fun parseMeeting(
        json: JSONObject,
        fallbackDate: String
    ): Meeting {
        val city =
            json.optString(
                "city"
            )

        val racesArray =
            json.optJSONArray(
                "races"
            )

        val races =
            buildList {
                if (
                    racesArray != null
                ) {
                    for (
                        i in 0 until
                        racesArray.length()
                    ) {
                        add(
                            parseRace(
                                racesArray
                                    .getJSONObject(i),
                                city
                            )
                        )
                    }
                }
            }

        return Meeting(
            city = city,
            date =
                json.optString(
                    "date",
                    fallbackDate
                ),
            races = races
        )
    }

    private fun parseRace(
        json: JSONObject,
        fallbackCity: String
    ): Race {
        val horsesJson =
            json.optJSONArray(
                "runners"
            )
                ?: json.optJSONArray(
                    "horses"
                )

        val horses =
            buildList {
                if (
                    horsesJson != null
                ) {
                    for (
                        i in 0 until
                        horsesJson.length()
                    ) {
                        add(
                            parseHorse(
                                horsesJson
                                    .getJSONObject(i)
                            )
                        )
                    }
                }
            }

        return Race(
            number =
                json.firstInt(
                    "race_number",
                    "raceNumber",
                    "number"
                )
                    ?: 0,

            city =
                json.optString(
                    "city",
                    fallbackCity
                ),

            startsAt =
                json.firstString(
                    "starts_at",
                    "startsAt"
                ),

            title =
                json.firstString(
                    "title",
                    "race_name",
                    "raceName"
                )
                    .orEmpty(),

            distance =
                json.firstInt(
                    "distance_meters",
                    "distanceMeters"
                )
                    ?.let {
                        "$it m"
                    }
                    ?: json.firstString(
                        "distance"
                    )
                        .orEmpty(),

            surface =
                json.firstString(
                    "track",
                    "surface"
                )
                    .orEmpty(),

            horses =
                horses
        )
    }

    private fun parseHorse(
        json: JSONObject
    ): Horse =
        Horse(
            number =
                json.firstInt(
                    "horse_number",
                    "horseNumber",
                    "number",
                    "no"
                )
                    ?: 0,

            name =
                json.firstString(
                    "horse_name",
                    "horseName",
                    "name"
                )
                    .orEmpty(),

            jockey =
                json.firstString(
                    "jockey",
                    "jockey_name",
                    "jockeyName"
                )
                    .orEmpty(),

            weight =
                json.firstDouble(
                    "weight"
                ),

            hp =
                json.firstInt(
                    "hp"
                ),

            agfPercent =
                json.firstDouble(
                    "agf_percent",
                    "agfPercent",
                    "agf"
                ),

            recentForm =
                json.firstString(
                    "recent_form_raw",
                    "recentForm",
                    "last6"
                )
                    .orEmpty(),

            score =
                json.optJSONObject(
                    "modelScore"
                )
                    ?.firstDouble(
                        "score"
                    )
                    ?: json.firstDouble(
                        "score"
                    ),

            confidence =
                json.optJSONObject(
                    "modelScore"
                )
                    ?.firstDouble(
                        "confidence"
                    )
                    ?: json.firstDouble(
                        "confidence"
                    )
        )

    private fun parseCoupon(
        json: JSONObject
    ): Coupon {
        val legsJson =
            json.optJSONArray(
                "legs"
            )

        val legs =
            buildList {
                if (
                    legsJson != null
                ) {
                    for (
                        i in 0 until
                        legsJson.length()
                    ) {
                        val legJson =
                            legsJson
                                .getJSONObject(i)

                        val horsesJson =
                            legJson.optJSONArray(
                                "horses"
                            )

                        val horses =
                            buildList {
                                if (
                                    horsesJson != null
                                ) {
                                    for (
                                        j in 0 until
                                        horsesJson.length()
                                    ) {
                                        val horse =
                                            horsesJson
                                                .getJSONObject(j)

                                        add(
                                            CouponHorse(
                                                horseNumber =
                                                    horse.optInt(
                                                        "horseNumber"
                                                    ),

                                                horseName =
                                                    horse.optString(
                                                        "horseName"
                                                    ),

                                                score =
                                                    horse
                                                        .optNullableDouble(
                                                            "score"
                                                        ),

                                                probability =
                                                    horse
                                                        .optNullableDouble(
                                                            "probability"
                                                        )
                                            )
                                        )
                                    }
                                }
                            }

                        add(
                            CouponLeg(
                                raceNumber =
                                    legJson.optInt(
                                        "raceNumber"
                                    ),

                                horses =
                                    horses,

                                coverageProbability =
                                    legJson
                                        .optNullableDouble(
                                            "coverageProbability"
                                        )
                            )
                        )
                    }
                }
            }

        return Coupon(
            profile =
                json.optString(
                    "profile"
                ),

            totalTl =
                json.optDouble(
                    "totalTl"
                ),

            combinations =
                json.optLong(
                    "combinations"
                ),

            estimatedSurvivalProbability =
                json.optNullableDouble(
                    "estimatedSurvivalProbability"
                ),

            legs =
                legs
        )
    }
}

private fun JSONObject.firstString(
    vararg keys: String
): String? {
    for (key in keys) {
        if (
            has(key) &&
            !isNull(key)
        ) {
            return optString(
                key
            )
        }
    }

    return null
}

private fun JSONObject.firstInt(
    vararg keys: String
): Int? {
    for (key in keys) {
        if (
            has(key) &&
            !isNull(key)
        ) {
            return optInt(
                key
            )
        }
    }

    return null
}

private fun JSONObject.firstDouble(
    vararg keys: String
): Double? {
    for (key in keys) {
        if (
            has(key) &&
            !isNull(key)
        ) {
            return optDouble(
                key
            )
        }
    }

    return null
}

private fun JSONObject.optNullableInt(
    key: String
): Int? =
    if (
        has(key) &&
        !isNull(key)
    ) {
        optInt(key)
    } else {
        null
    }

private fun JSONObject.optNullableDouble(
    key: String
): Double? =
    if (
        has(key) &&
        !isNull(key)
    ) {
        optDouble(key)
    } else {
        null
    }

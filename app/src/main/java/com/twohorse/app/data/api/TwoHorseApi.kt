package com.twohorse.app.data.api

import com.twohorse.app.domain.model.*
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
                                    item.optInt(
                                        "expertPredictionCount",
                                        experts?.length()
                                            ?: 0
                                    ),

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
        multiplier: Int = 1,
        pool: String = "sixfold"
    ): CouponResult =
        withContext(
            Dispatchers.IO
        ) {
            val legCount =
                if (pool == "fivefold") 5 else 6

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
                    .addQueryParameter(
                        "pool",
                        pool
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

            val responseCity =
                json.optString(
                    "city",
                    city
                )

            val responsePool =
                json.optString(
                    "pool",
                    pool
                )

            val responseSixfold =
                json.optInt(
                    "windowNumber",
                    sixfold
                )

            val startRace =
                json.optNullableInt(
                    "startRace"
                )

            val endRace =
                json.optNullableInt(
                    "endRace"
                )

            val responseBudget =
                json.optDouble(
                    "budgetTl",
                    budgetTl
                )

            require(
                responseCity.isNotBlank()
            ) {
                "INVALID_COUPON_RESPONSE_CITY"
            }

            require(
                responseSixfold == 1 ||
                responseSixfold == 2
            ) {
                "INVALID_COUPON_RESPONSE_SIXFOLD"
            }

            require(
                startRace != null &&
                endRace != null &&
                endRace >= startRace &&
                endRace - startRace == legCount - 1
            ) {
                "INVALID_COUPON_RESPONSE_WINDOW"
            }

            require(
                responseBudget > 0.0
            ) {
                "INVALID_COUPON_RESPONSE_BUDGET"
            }

            coupons.forEach {
                coupon ->

                require(
                    coupon.totalTl >= 0.0
                ) {
                    "INVALID_COUPON_TOTAL"
                }

                require(
                    coupon.legs.size == legCount
                ) {
                    "INVALID_COUPON_LEG_COUNT"
                }
            }

            CouponResult(
                city =
                    responseCity,

                pool =
                    responsePool,

                sixfold =
                    responseSixfold,

                startRace =
                    startRace,

                endRace =
                    endRace,

                budgetTl =
                    responseBudget,

                coupons =
                    coupons,

                date =
                    json.firstString(
                        "date"
                    ),

                unitPriceTl =
                    json.firstDouble(
                        "unitPriceTl"
                    ),

                multiplier =
                    json.firstInt(
                        "multiplier"
                    ) ?: 1,

                generatedAt =
                    json.firstString(
                        "generatedAt"
                    )
            )
        }

    suspend fun getHorseVideos(
        raceDate: String,
        city: String,
        raceNumber: Int,
        horseNumber: Int
    ): List<HorseVideo> =
        withContext(
            Dispatchers.IO
        ) {
            val url =
                (
                    baseUrl +
                    "/api/horses/videos"
                )
                    .toHttpUrl()
                    .newBuilder()
                    .addQueryParameter(
                        "raceDate",
                        raceDate
                    )
                    .addQueryParameter(
                        "city",
                        city
                    )
                    .addQueryParameter(
                        "raceNumber",
                        raceNumber.toString()
                    )
                    .addQueryParameter(
                        "horseNumber",
                        horseNumber.toString()
                    )
                    .build()

            val json =
                execute(
                    Request.Builder()
                        .url(url)
                        .get()
                        .build()
                )

            val videosArray =
                json.optJSONArray(
                    "videos"
                )

            buildList {
                if (videosArray != null) {
                    for (
                        i in 0 until
                        videosArray.length()
                    ) {
                        val item =
                            videosArray.getJSONObject(i)

                        add(
                            HorseVideo(
                                label =
                                    item.optString(
                                        "label"
                                    ),
                                url =
                                    item.optString(
                                        "url"
                                    )
                            )
                        )
                    }
                }
            }
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
                    val errorJson =
                        runCatching {
                            JSONObject(body)
                        }.getOrNull()

                    val apiCode =
                        errorJson
                            ?.optString("error")
                            ?.takeIf {
                                it.isNotBlank()
                            }

                    throw ApiException(
                        statusCode = response.code,
                        apiCode = apiCode,
                        message = apiCode ?: "HTTP ${response.code}"
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

        val meetingDate =
            json.optString(
                "date",
                fallbackDate
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
                                city,
                                meetingDate
                            )
                        )
                    }
                }
            }

        return Meeting(
            city = city,
            date = meetingDate,
            races = races
        )
    }

    private fun parseRace(
        json: JSONObject,
        fallbackCity: String,
        raceDate: String
    ): Race {
        val horsesJson =
            json.optJSONArray("runners")
                ?: json.optJSONArray("horses")

        val horses =
            buildList {
                if (horsesJson != null) {
                    for (i in 0 until horsesJson.length()) {
                        add(
                            parseHorse(
                                horsesJson.getJSONObject(i)
                            )
                        )
                    }
                }
            }

        val uncertaintyJson =
            json.optJSONObject("uncertainty")

        val uncertainty =
            uncertaintyJson?.let {
                RaceUncertainty(
                    level =
                        it.optString(
                            "level",
                            "unknown"
                        ),
                    score =
                        it.optDouble(
                            "score",
                            0.0
                        ),
                    topMargin =
                        it.optDouble(
                            "topMargin",
                            0.0
                        ),
                    leaderScore =
                        it.optDouble(
                            "leaderScore",
                            0.0
                        ),
                    secondScore =
                        it.optNullableDouble(
                            "secondScore"
                        ),
                    expansionPressure =
                        it.optDouble(
                            "expansionPressure",
                            0.0
                        )
                )
            }

        val strategyJson =
            json.optJSONObject(
                "couponStrategy"
            )

        val strategy =
            strategyJson?.let {
                val horseNumbers =
                    buildList {
                        val array =
                            it.optJSONArray(
                                "horseNumbers"
                            )

                        if (array != null) {
                            for (
                                i in 0 until
                                array.length()
                            ) {
                                add(
                                    array.optInt(i)
                                )
                            }
                        }
                    }

                RaceCouponStrategy(
                    mode =
                        it.optString(
                            "mode"
                        ),
                    horseNumbers =
                        horseNumbers,
                    confidence =
                        it.optDouble(
                            "confidence",
                            0.0
                        ),
                    expansionPressure =
                        it.optDouble(
                            "expansionPressure",
                            0.0
                        ),
                    reason =
                        it.optString(
                            "reason"
                        )
                )
            }

        return Race(
            number =
                json.firstInt(
                    "race_number",
                    "raceNumber",
                    "number"
                ) ?: 0,

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
                ).orEmpty(),

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
                    ).orEmpty(),

            surface =
                json.firstString(
                    "track",
                    "surface"
                ).orEmpty(),

            horses =
                horses,

            uncertainty =
                uncertainty,

            couponStrategy =
                strategy,

            raceDate =
                raceDate
        )
    }

    private fun parseHorse(
        json: JSONObject
    ): Horse {
        val modelScore =
            json.optJSONObject(
                "modelScore"
            )

        val components =
            buildList {
                val array =
                    modelScore
                        ?.optJSONArray(
                            "components"
                        )

                if (array != null) {
                    for (
                        i in 0 until
                        array.length()
                    ) {
                        val item =
                            array.getJSONObject(i)

                        add(
                            ScoreComponent(
                                key =
                                    item.optString(
                                        "key"
                                    ),
                                score =
                                    item.optNullableDouble(
                                        "score"
                                    ),
                                configuredWeight =
                                    item.optDouble(
                                        "configuredWeight",
                                        0.0
                                    ),
                                effectiveWeight =
                                    item.optDouble(
                                        "effectiveWeight",
                                        0.0
                                    )
                            )
                        )
                    }
                }
            }

        val consensusJson =
            json.optJSONObject(
                "expertConsensus"
            )

        val consensus =
            consensusJson?.let {
                val labels =
                    buildList {
                        val array =
                            it.optJSONArray(
                                "labels"
                            )

                        if (array != null) {
                            for (
                                i in 0 until
                                array.length()
                            ) {
                                val value =
                                    array.optString(i)

                                if (
                                    value.isNotBlank()
                                ) {
                                    add(value)
                                }
                            }
                        }
                    }

                ExpertConsensusSummary(
                    sourceCount =
                        it.optInt(
                            "sourceCount",
                            0
                        ),
                    bankoCount =
                        it.optInt(
                            "bankoCount",
                            0
                        ),
                    favoriteCount =
                        it.optInt(
                            "favoriteCount",
                            0
                        ),
                    strongCount =
                        it.optInt(
                            "strongCount",
                            0
                        ),
                    starCount =
                        it.optInt(
                            "starCount",
                            0
                        ),
                    rivalCount =
                        it.optInt(
                            "rivalCount",
                            0
                        ),
                    surpriseCount =
                        it.optInt(
                            "surpriseCount",
                            0
                        ),
                    avoidCount =
                        it.optInt(
                            "avoidCount",
                            0
                        ),
                    bankoScore =
                        it.optDouble(
                            "bankoScore",
                            0.0
                        ),
                    favoriteScore =
                        it.optDouble(
                            "favoriteScore",
                            0.0
                        ),
                    strongScore =
                        it.optDouble(
                            "strongScore",
                            0.0
                        ),
                    starScore =
                        it.optDouble(
                            "starScore",
                            0.0
                        ),
                    rivalScore =
                        it.optDouble(
                            "rivalScore",
                            0.0
                        ),
                    surpriseScore =
                        it.optDouble(
                            "surpriseScore",
                            0.0
                        ),
                    avoidScore =
                        it.optDouble(
                            "avoidScore",
                            0.0
                        ),
                    expertScore =
                        it.optNullableDouble(
                            "expertScore"
                        ),
                    supportConfidence =
                        it.optNullableDouble(
                            "supportConfidence"
                        ),
                    labels =
                        labels,
                    summary =
                        it.optString(
                            "summary",
                            ""
                        )
                )
            }

        val marketJson =
            json.optJSONObject(
                "marketMovement"
            )

        val market =
            marketJson?.let {
                MarketMovement(
                    score =
                        it.optNullableDouble(
                            "score"
                        ),
                    sampleSize =
                        it.optInt(
                            "sampleSize",
                            0
                        ),
                    firstAgf =
                        it.optNullableDouble(
                            "firstAgf"
                        ),
                    latestAgf =
                        it.optNullableDouble(
                            "latestAgf"
                        ),
                    absoluteDelta =
                        it.optNullableDouble(
                            "absoluteDelta"
                        ),
                    relativeDelta =
                        it.optNullableDouble(
                            "relativeDelta"
                        ),
                    spanMinutes =
                        it.optNullableDouble(
                            "spanMinutes"
                        ),
                    direction =
                        it.optString(
                            "direction",
                            "unknown"
                        )
                )
            }

        val fieldJson =
            json.optJSONObject(
                "fieldSignal"
            )

        val field =
            fieldJson?.let {
                FieldSignal(
                    score =
                        it.optNullableDouble(
                            "score"
                        ),
                    tjkScore =
                        it.optNullableDouble(
                            "tjkScore"
                        ),
                    expertScore =
                        it.optNullableDouble(
                            "expertScore"
                        ),
                    tjkSampleSize =
                        it.optInt(
                            "tjkSampleSize",
                            0
                        )
                )
            }

        return Horse(
            number =
                json.firstInt(
                    "horse_number",
                    "horseNumber",
                    "number",
                    "no"
                ) ?: 0,

            name =
                json.firstString(
                    "horse_name",
                    "horseName",
                    "name"
                ).orEmpty(),

            jockey =
                json.firstString(
                    "jockey",
                    "jockey_name",
                    "jockeyName"
                ).orEmpty(),

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
                ).orEmpty(),

            score =
                modelScore
                    ?.firstDouble(
                        "score"
                    )
                    ?: json.firstDouble(
                        "score"
                    ),

            confidence =
                modelScore
                    ?.firstDouble(
                        "confidence"
                    )
                    ?: json.firstDouble(
                        "confidence"
                    ),

            baseScore =
                modelScore
                    ?.firstDouble(
                        "baseScore"
                    ),

            learningAdjustment =
                modelScore
                    ?.firstDouble(
                        "learningAdjustment"
                    ),

            scoreComponents =
                components,

            expertConsensus =
                consensus,

            marketMovement =
                market,

            fieldSignal =
                field,

            finishPosition =
                json.firstInt(
                    "finishPosition",
                    "finish_position"
                )
        )
    }

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

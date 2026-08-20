package com.twohorse.app.domain.model

data class Horse(
    val number: Int,
    val name: String,
    val jockey: String = "",
    val weight: Double? = null,
    val hp: Int? = null,
    val agfPercent: Double? = null,
    val recentForm: String = "",
    val score: Double? = null,
    val confidence: Double? = null
)

data class Race(
    val number: Int,
    val city: String,
    val startsAt: String?,
    val title: String = "",
    val distance: String = "",
    val surface: String = "",
    val horses: List<Horse> = emptyList()
)

data class Meeting(
    val city: String,
    val date: String,
    val races: List<Race> = emptyList()
)

data class TodayData(
    val date: String,
    val meetings: List<Meeting>
)

data class CouponHorse(
    val horseNumber: Int,
    val horseName: String,
    val score: Double?,
    val probability: Double?
)

data class CouponLeg(
    val raceNumber: Int,
    val horses: List<CouponHorse>,
    val coverageProbability: Double?
)

data class Coupon(
    val profile: String,
    val totalTl: Double,
    val combinations: Long,
    val estimatedSurvivalProbability: Double?,
    val legs: List<CouponLeg>
)

data class CouponResult(
    val city: String,
    val sixfold: Int,
    val startRace: Int?,
    val endRace: Int?,
    val budgetTl: Double,
    val coupons: List<Coupon>
)

data class HistoryRace(
    val raceDate: String,
    val city: String,
    val raceNumber: Int,
    val startTime: String,
    val startsAt: String?,
    val distanceMeters: Int?,
    val track: String,
    val runners: List<Horse>,
    val expertPredictionCount: Int,
    val finalizedAt: String?
)

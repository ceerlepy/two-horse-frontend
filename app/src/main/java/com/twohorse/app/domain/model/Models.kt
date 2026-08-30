package com.twohorse.app.domain.model

data class ScoreComponent(
    val key: String,
    val score: Double?,
    val configuredWeight: Double,
    val effectiveWeight: Double
)

data class ExpertConsensusSummary(
    val sourceCount: Int = 0,
    val bankoCount: Int = 0,
    val favoriteCount: Int = 0,
    val strongCount: Int = 0,
    val starCount: Int = 0,
    val rivalCount: Int = 0,
    val surpriseCount: Int = 0,
    val avoidCount: Int = 0,
    val expertScore: Double? = null,
    val supportConfidence: Double? = null,
    val labels: List<String> = emptyList(),
    val summary: String = ""
)

data class MarketMovement(
    val score: Double? = null,
    val sampleSize: Int = 0,
    val firstAgf: Double? = null,
    val latestAgf: Double? = null,
    val absoluteDelta: Double? = null,
    val relativeDelta: Double? = null,
    val spanMinutes: Double? = null,
    val direction: String = "unknown"
)

data class FieldSignal(
    val score: Double? = null,
    val tjkScore: Double? = null,
    val expertScore: Double? = null,
    val tjkSampleSize: Int = 0
)

data class RaceUncertainty(
    val level: String,
    val score: Double,
    val topMargin: Double,
    val leaderScore: Double,
    val secondScore: Double?,
    val expansionPressure: Double
)

data class RaceCouponStrategy(
    val mode: String,
    val horseNumbers: List<Int>,
    val confidence: Double,
    val expansionPressure: Double,
    val reason: String
)

data class Horse(
    val number: Int,
    val name: String,
    val jockey: String = "",
    val weight: Double? = null,
    val hp: Int? = null,
    val agfPercent: Double? = null,
    val recentForm: String = "",
    val score: Double? = null,
    val confidence: Double? = null,

    val baseScore: Double? = null,
    val learningAdjustment: Double? = null,
    val scoreComponents: List<ScoreComponent> = emptyList(),

    val expertConsensus: ExpertConsensusSummary? = null,
    val marketMovement: MarketMovement? = null,
    val fieldSignal: FieldSignal? = null,

    val finishPosition: Int? = null
)

data class Race(
    val number: Int,
    val city: String,
    val startsAt: String?,
    val title: String = "",
    val distance: String = "",
    val surface: String = "",
    val horses: List<Horse> = emptyList(),
    val uncertainty: RaceUncertainty? = null,
    val couponStrategy: RaceCouponStrategy? = null
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
    val coupons: List<Coupon>,
    val date: String? = null,
    val unitPriceTl: Double? = null,
    val multiplier: Int = 1,
    val generatedAt: String? = null
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

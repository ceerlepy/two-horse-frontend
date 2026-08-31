package com.twohorse.app.i18n

import androidx.compose.runtime.Composable

/*
 * Every user-facing string in the app, grouped by screen. The real
 * per-locale text lives in res/values/strings.xml (Turkish/default)
 * and res/values-en/strings.xml; ResourceStrings implements this
 * interface by reading those via stringResource(), so call sites get
 * compile-time-checked typed arguments instead of a raw vararg.
 */
interface Strings {

    // ---- Common ----
    @get:Composable
    val noData: String
    @get:Composable
    val startingSoon: String
    @get:Composable
    val back: String

    // ---- Login screen ----
    @get:Composable
    val loginSubtitle: String
    @get:Composable
    val loginGoogleButton: String
    @get:Composable
    val loginOr: String
    @get:Composable
    val loginEmailLabel: String
    @get:Composable
    val loginPasswordLabel: String
    @get:Composable
    val loginSubmitButton: String
    @get:Composable
    val loginErrorInvalidCredentials: String
    @get:Composable
    val loginErrorEmailPasswordRequired: String
    @get:Composable
    val loginErrorEmailNotVerified: String
    @get:Composable
    val loginErrorNotConfigured: String
    @get:Composable
    val loginErrorGeneric: String
    @get:Composable
    val loginErrorGoogleIncomplete: String
    @Composable
    fun loginErrorGoogleFailed(code: Int): String

    // ---- Account screen ----
    @get:Composable
    val accountTitle: String
    @Composable
    fun accountTierTitle(tier: String): String
    @Composable
    fun accountTrialEndsAt(date: String): String
    @Composable
    fun accountSubscriptionRenewsAt(date: String): String
    @get:Composable
    val accountUnlimited: String
    @get:Composable
    val accountGoldDescription: String
    @get:Composable
    val accountPremiumDescription: String
    @Composable
    fun accountUpgradeTo(tierTitle: String): String
    @get:Composable
    val accountLoadingEllipsis: String
    @get:Composable
    val accountAlreadyPremium: String
    @get:Composable
    val accountLogout: String
    @Composable
    fun accountPurchaseActivated(tierTitle: String): String
    @get:Composable
    val accountPurchaseVerifyFailed: String

    // ---- Coupon errors ----
    @get:Composable
    val couponErrorCityRequired: String
    @get:Composable
    val couponErrorValidBudgetRequired: String
    @get:Composable
    val couponErrorSixfoldNotFound: String
    @get:Composable
    val couponErrorNotEnoughRaces: String
    @get:Composable
    val couponErrorNoRunners: String
    @get:Composable
    val couponErrorTierUpgradeRequired: String
    @get:Composable
    val couponErrorBudgetCapExceeded: String
    @get:Composable
    val couponErrorAuthRequired: String
    @get:Composable
    val couponErrorNotFound: String
    @get:Composable
    val couponErrorBadRequest: String
    @get:Composable
    val couponErrorServerUnavailable: String
    @get:Composable
    val couponErrorNoInternet: String
    @get:Composable
    val couponErrorTimeout: String
    @get:Composable
    val couponErrorGeneric: String

    // ---- Coupon screen ----
    @get:Composable
    val couponHeaderTitle: String
    @get:Composable
    val poolLabelSixfold: String
    @get:Composable
    val poolLabelFivefold: String
    @get:Composable
    val couponCitySelectFailed: String
    @get:Composable
    val couponUnexpectedWindow: String
    @get:Composable
    val couponBudgetExceeded: String
    @get:Composable
    val couponGenerateButton: String
    @get:Composable
    val couponUpgradeButton: String
    @get:Composable
    val couponCityTitle: String
    @get:Composable
    val couponCitySubtitle: String
    @get:Composable
    val couponTypeTitle: String
    @get:Composable
    val couponTypeSubtitle: String
    @Composable
    fun couponWindowSubtitle(poolLabelLower: String): String
    @get:Composable
    val couponBudgetTitle: String
    @get:Composable
    val couponBudgetSubtitle: String
    @Composable
    fun couponGeneratedSummary(
        city: String,
        window: Int,
        poolLabel: String,
        budgetTl: Int
    ): String
    @Composable
    fun couponWindowStarted(poolLabel: String): String
    @get:Composable
    val couponNoneGenerated: String
    @get:Composable
    val couponIntroTitle: String
    @get:Composable
    val couponIntroSubtitle: String
    @Composable
    fun couponRaceRange(startRace: String, endRace: String): String
    @Composable
    fun couponMaxBudgetAndCount(budgetTl: Int, couponCount: Int): String
    @Composable
    fun couponUnitPriceAndMultiplier(unitPriceTl: String, multiplier: Int): String
    @Composable
    fun couponGeneratedAt(time: String): String
    @get:Composable
    val couponLadderTitle: String
    @get:Composable
    val couponLadderFixed1Title: String
    @get:Composable
    val couponLadderFixed1Desc: String
    @get:Composable
    val couponLadderFixed2Title: String
    @get:Composable
    val couponLadderFixed2Desc: String
    @get:Composable
    val couponLadderVariableTitle: String
    @get:Composable
    val couponLadderVariableDesc: String
    @get:Composable
    val couponLadderFooter: String
    @Composable
    fun couponTierLabel(index: Int, total: Int): String
    @Composable
    fun couponAmountLabel(amount: Int): String
    @get:Composable
    val couponMetricCombinations: String
    @get:Composable
    val couponMetricCoverage: String
    @Composable
    fun couponLegCoverage(pct: String): String
    @Composable
    fun couponWindowOrdinal(number: Int, poolLabel: String): String

    // ---- Race detail screen ----
    @get:Composable
    val raceNotFoundInProgram: String
    @get:Composable
    val raceRefreshFailed: String
    @get:Composable
    val raceCouponButton: String
    @get:Composable
    val raceAllHorsesTitle: String
    @Composable
    fun raceHorseCount(n: Int): String
    @get:Composable
    val raceRefresh: String
    @Composable
    fun raceCityAndNumber(city: String, number: Int): String
    @get:Composable
    val raceLikelyWinner: String
    @Composable
    fun raceConfidenceScore(score: String): String
    @get:Composable
    val raceAgf: String
    @get:Composable
    val raceHp: String
    @Composable
    fun raceHpPoints(n: Int): String
    @get:Composable
    val raceExpertSupport: String
    @get:Composable
    val raceField: String
    @get:Composable
    val raceMarket: String
    @get:Composable
    val raceForm: String
    @get:Composable
    val raceLearning: String
    @Composable
    fun raceLearningDelta(signedValue: String): String
    @Composable
    fun raceTopRival(number: Int, name: String, score: Int): String
    @Composable
    fun raceSurprise(number: Int, name: String, score: Int): String
    @get:Composable
    val raceRiskMapTitle: String
    @get:Composable
    val raceUncertaintyMetric: String
    @get:Composable
    val raceLeaderMarginMetric: String
    @get:Composable
    val raceExpansionMetric: String
    @get:Composable
    val raceDeepAnalysisTitle: String
    @get:Composable
    val raceDeepAnalysisSubtitle: String
    @get:Composable
    val raceCloseDeepAnalysis: String
    @get:Composable
    val raceOpenDeepAnalysis: String
    @get:Composable
    val raceInfoFallback: String
    @get:Composable
    val raceExpertSourceMissing: String
    @Composable
    fun raceExpertSourcesCount(n: Int): String
    @Composable
    fun raceExpertFavoriteCount(n: Int): String
    @Composable
    fun raceExpertBankoCount(n: Int): String
    @Composable
    fun raceExpertStrongCount(n: Int): String
    @Composable
    fun raceFieldCombined(value: String): String
    @get:Composable
    val raceGuven: String
    @get:Composable
    val raceVideoLabel: String
    @get:Composable
    val raceVideoLabelLocked: String
    @get:Composable
    val raceVideoLockedBody: String
    @get:Composable
    val raceVideoNotFound: String
    @get:Composable
    val raceVideoFallbackLabel: String
    @get:Composable
    val raceCloseModelDetail: String
    @get:Composable
    val raceOpenModelDetail: String
    @get:Composable
    val raceScoreComponents: String
    @Composable
    fun raceWeightBoth(effective: String, configured: String): String
    @Composable
    fun raceWeightEffectiveOnly(effective: String): String
    @get:Composable
    val raceExpertConsensusTitle: String
    @Composable
    fun raceStarTag(n: Int, pct: Int): String
    @Composable
    fun raceRivalTag(n: Int, pct: Int): String
    @Composable
    fun raceSurpriseTag(n: Int, pct: Int): String
    @Composable
    fun raceAvoidTag(n: Int, pct: Int): String
    @get:Composable
    val raceExpertScoreTitle: String
    @Composable
    fun raceSupportConfidence(pct: String): String
    @get:Composable
    val raceMarketMoveTitle: String
    @Composable
    fun raceMarketFirst(pct: String): String
    @Composable
    fun raceMarketTo(pct: String): String
    @Composable
    fun raceMarketSamples(n: Int): String
    @get:Composable
    val raceMarketScoreTitle: String
    @get:Composable
    val raceFieldSignalTitle: String
    @get:Composable
    val raceFieldCombinedTitle: String
    @Composable
    fun raceFieldTjk(value: String): String
    @Composable
    fun raceFieldExpert(value: String): String
    @Composable
    fun raceFieldSamples(n: Int): String
    @get:Composable
    val raceLearningEffectTitle: String
    @Composable
    fun raceLearningBase(value: String): String
    @Composable
    fun raceLearningFinal(value: String): String
    @get:Composable
    val raceDeepViewTitle: String

    // ---- Home screen ----
    @get:Composable
    val homeDataFetchFailed: String
    @get:Composable
    val homeRefreshFailedStale: String
    @get:Composable
    val homeAllCities: String
    @get:Composable
    val homeRetryButton: String
    @get:Composable
    val homeNoRacesToShow: String
    @get:Composable
    val homeUpcomingRacesTitle: String
    @get:Composable
    val homeUpcomingRacesSubtitle: String
    @get:Composable
    val homeRacesPreparing: String
    @get:Composable
    val homeLiveUpdating: String
    @get:Composable
    val homeTagline: String
    @get:Composable
    val homeLogoDescription: String
    @get:Composable
    val homeAccountDescription: String
    @get:Composable
    val homeHistoryDescription: String
    @get:Composable
    val homeRefreshDescription: String
    @get:Composable
    val homeNextRaceLabel: String
    @get:Composable
    val homeOpenAnalysis: String
    @get:Composable
    val homeModelFavorite: String
    @Composable
    fun homeCourseNumber(n: Int): String
    @get:Composable
    val homeOpenRaceAnalysis: String
    @Composable
    fun homeCourseNumberCaps(n: Int): String
    @Composable
    fun homeSurprisePrefix(number: Int, name: String): String
    @get:Composable
    val homeOtherRemainingRaces: String
    @Composable
    fun homeUpcomingCount(n: Int): String
    @get:Composable
    val homeCloseOtherRaces: String
    @get:Composable
    val homeOpenOtherRaces: String
    @get:Composable
    val homeSixfoldTitle: String
    @get:Composable
    val homeSixfoldSubtitle: String
    @get:Composable
    val homeOpenSixfold: String
    @get:Composable
    val homeFavoriteSummaryFallback: String
    @Composable
    fun homeAgf(pct: String): String
    @Composable
    fun homeExpertSourceLabel(n: Int): String
    @Composable
    fun homeHpLabel(n: Int): String
    @Composable
    fun homeCityRaceLabel(city: String, number: Int): String
    @Composable
    fun homeFavoritePrefix(number: Int, name: String): String
    @get:Composable
    val homeCountdownKaldi: String

    // ---- Analytics components (shared) ----
    @get:Composable
    val uncertaintyLow: String
    @get:Composable
    val uncertaintyMedium: String
    @get:Composable
    val uncertaintyHigh: String
    @get:Composable
    val uncertaintyVeryHigh: String
    @get:Composable
    val uncertaintyLowCaps: String
    @get:Composable
    val uncertaintyMediumCaps: String
    @get:Composable
    val uncertaintyHighCaps: String
    @get:Composable
    val uncertaintyVeryHighCaps: String
    @get:Composable
    val explanationClose: String
    @get:Composable
    val explanationTop3Close: String
    @get:Composable
    val explanationClearLeader: String
    @Composable
    fun uncertaintyLine(level: String, explanation: String): String
    @get:Composable
    val strategySingle: String
    @get:Composable
    val strategyCompact: String
    @get:Composable
    val strategySpread: String
    @get:Composable
    val strategyBalanced: String
    @get:Composable
    val strategyOneCandidate: String
    @Composable
    fun strategyCandidates(n: Int): String
    @get:Composable
    val strategyBackendDefault: String
    @Composable
    fun strategyLine(mode: String, reason: String): String
    @get:Composable
    val componentAgf: String
    @get:Composable
    val componentExpert: String
    @get:Composable
    val componentForm: String
    @get:Composable
    val componentHp: String
    @get:Composable
    val componentMarket: String
    @get:Composable
    val componentWeight: String
    @get:Composable
    val componentField: String
    @get:Composable
    val marketStrongUp: String
    @get:Composable
    val marketUp: String
    @get:Composable
    val marketFlat: String
    @get:Composable
    val marketDown: String
    @get:Composable
    val marketStrongDown: String
    @get:Composable
    val marketNone: String

    // ---- History screens ----
    @get:Composable
    val historyFetchFailed: String
    @get:Composable
    val historyLoading: String
    @get:Composable
    val historyEmptyTitle: String
    @get:Composable
    val historyEmptyBody: String
    @get:Composable
    val historyTitle: String
    @Composable
    fun historySnapshotCount(n: Int): String
    @get:Composable
    val historyHeaderTitle: String
    @Composable
    fun historyExpertRowCount(n: Int): String
    @get:Composable
    val historyModelPerformanceTitle: String
    @Composable
    fun historyBasedOnRaces(n: Int): String
    @get:Composable
    val historyTop1Hit: String
    @get:Composable
    val historyTop3Coverage: String
    @get:Composable
    val historyRaceLabel: String
    @get:Composable
    val historyCityTop1: String
    @Composable
    fun historyResultRatio(hits: Int, total: Int, pct: Int): String
    @get:Composable
    val historyDetailRankTitle: String
    @get:Composable
    val historyDetailRankSubtitle: String
    @get:Composable
    val historyResultNotReady: String
    @get:Composable
    val historyModelLeaderWon: String
    @get:Composable
    val historyRaceCompleted: String
    @get:Composable
    val historyModelLeaderLabel: String
    @get:Composable
    val historyWinnerLabel: String
    @get:Composable
    val historyPending: String
    @Composable
    fun historyExpertRecordCount(n: Int): String
    @get:Composable
    val historyWon: String
    @get:Composable
    val historyExactHit: String
    @get:Composable
    val historyModelRank: String
    @get:Composable
    val historyActualRank: String
    @get:Composable
    val historyModelScore: String
    @Composable
    fun historyCityAndDate(city: String, date: String): String
    @Composable
    fun historyCityAndRace(city: String, raceNumber: Int): String
    @Composable
    fun historyDateAndTime(date: String, time: String): String
    @Composable
    fun historyRaceNumberAbbrev(n: Int): String
}

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
    @Composable
    val noData: String
    @Composable
    val startingSoon: String
    @Composable
    val back: String

    // ---- Login screen ----
    @Composable
    val loginSubtitle: String
    @Composable
    val loginGoogleButton: String
    @Composable
    val loginOr: String
    @Composable
    val loginEmailLabel: String
    @Composable
    val loginPasswordLabel: String
    @Composable
    val loginSubmitButton: String
    @Composable
    val loginErrorInvalidCredentials: String
    @Composable
    val loginErrorEmailPasswordRequired: String
    @Composable
    val loginErrorEmailNotVerified: String
    @Composable
    val loginErrorNotConfigured: String
    @Composable
    val loginErrorGeneric: String
    @Composable
    val loginErrorGoogleIncomplete: String
    @Composable
    fun loginErrorGoogleFailed(code: Int): String

    // ---- Account screen ----
    @Composable
    val accountTitle: String
    @Composable
    fun accountTierTitle(tier: String): String
    @Composable
    fun accountTrialEndsAt(date: String): String
    @Composable
    fun accountSubscriptionRenewsAt(date: String): String
    @Composable
    val accountUnlimited: String
    @Composable
    val accountGoldDescription: String
    @Composable
    val accountPremiumDescription: String
    @Composable
    fun accountUpgradeTo(tierTitle: String): String
    @Composable
    val accountLoadingEllipsis: String
    @Composable
    val accountAlreadyPremium: String
    @Composable
    val accountLogout: String
    @Composable
    fun accountPurchaseActivated(tierTitle: String): String
    @Composable
    val accountPurchaseVerifyFailed: String

    // ---- Coupon errors ----
    @Composable
    val couponErrorCityRequired: String
    @Composable
    val couponErrorValidBudgetRequired: String
    @Composable
    val couponErrorSixfoldNotFound: String
    @Composable
    val couponErrorNotEnoughRaces: String
    @Composable
    val couponErrorNoRunners: String
    @Composable
    val couponErrorTierUpgradeRequired: String
    @Composable
    val couponErrorBudgetCapExceeded: String
    @Composable
    val couponErrorAuthRequired: String
    @Composable
    val couponErrorNotFound: String
    @Composable
    val couponErrorBadRequest: String
    @Composable
    val couponErrorServerUnavailable: String
    @Composable
    val couponErrorNoInternet: String
    @Composable
    val couponErrorTimeout: String
    @Composable
    val couponErrorGeneric: String

    // ---- Coupon screen ----
    @Composable
    val couponHeaderTitle: String
    @Composable
    val poolLabelSixfold: String
    @Composable
    val poolLabelFivefold: String
    @Composable
    val couponCitySelectFailed: String
    @Composable
    val couponUnexpectedWindow: String
    @Composable
    val couponBudgetExceeded: String
    @Composable
    val couponGenerateButton: String
    @Composable
    val couponUpgradeButton: String
    @Composable
    val couponCityTitle: String
    @Composable
    val couponCitySubtitle: String
    @Composable
    val couponTypeTitle: String
    @Composable
    val couponTypeSubtitle: String
    @Composable
    fun couponWindowSubtitle(poolLabelLower: String): String
    @Composable
    val couponBudgetTitle: String
    @Composable
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
    @Composable
    val couponNoneGenerated: String
    @Composable
    val couponIntroTitle: String
    @Composable
    val couponIntroSubtitle: String
    @Composable
    fun couponRaceRange(startRace: String, endRace: String): String
    @Composable
    fun couponMaxBudgetAndCount(budgetTl: Int, couponCount: Int): String
    @Composable
    fun couponUnitPriceAndMultiplier(unitPriceTl: String, multiplier: Int): String
    @Composable
    fun couponGeneratedAt(time: String): String
    @Composable
    val couponLadderTitle: String
    @Composable
    val couponLadderFixed1Title: String
    @Composable
    val couponLadderFixed1Desc: String
    @Composable
    val couponLadderFixed2Title: String
    @Composable
    val couponLadderFixed2Desc: String
    @Composable
    val couponLadderVariableTitle: String
    @Composable
    val couponLadderVariableDesc: String
    @Composable
    val couponLadderFooter: String
    @Composable
    fun couponTierLabel(index: Int, total: Int): String
    @Composable
    fun couponAmountLabel(amount: Int): String
    @Composable
    val couponMetricCombinations: String
    @Composable
    val couponMetricCoverage: String
    @Composable
    fun couponLegCoverage(pct: String): String
    @Composable
    fun couponWindowOrdinal(number: Int, poolLabel: String): String

    // ---- Race detail screen ----
    @Composable
    val raceNotFoundInProgram: String
    @Composable
    val raceRefreshFailed: String
    @Composable
    val raceCouponButton: String
    @Composable
    val raceAllHorsesTitle: String
    @Composable
    fun raceHorseCount(n: Int): String
    @Composable
    val raceRefresh: String
    @Composable
    fun raceCityAndNumber(city: String, number: Int): String
    @Composable
    val raceLikelyWinner: String
    @Composable
    fun raceConfidenceScore(score: String): String
    @Composable
    val raceAgf: String
    @Composable
    val raceHp: String
    @Composable
    fun raceHpPoints(n: Int): String
    @Composable
    val raceExpertSupport: String
    @Composable
    val raceField: String
    @Composable
    val raceMarket: String
    @Composable
    val raceForm: String
    @Composable
    val raceLearning: String
    @Composable
    fun raceLearningDelta(signedValue: String): String
    @Composable
    fun raceTopRival(number: Int, name: String, score: Int): String
    @Composable
    fun raceSurprise(number: Int, name: String, score: Int): String
    @Composable
    val raceRiskMapTitle: String
    @Composable
    val raceUncertaintyMetric: String
    @Composable
    val raceLeaderMarginMetric: String
    @Composable
    val raceExpansionMetric: String
    @Composable
    val raceDeepAnalysisTitle: String
    @Composable
    val raceDeepAnalysisSubtitle: String
    @Composable
    val raceCloseDeepAnalysis: String
    @Composable
    val raceOpenDeepAnalysis: String
    @Composable
    val raceInfoFallback: String
    @Composable
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
    @Composable
    val raceGuven: String
    @Composable
    val raceVideoLabel: String
    @Composable
    val raceVideoLabelLocked: String
    @Composable
    val raceVideoLockedBody: String
    @Composable
    val raceVideoNotFound: String
    @Composable
    val raceVideoFallbackLabel: String
    @Composable
    val raceCloseModelDetail: String
    @Composable
    val raceOpenModelDetail: String
    @Composable
    val raceScoreComponents: String
    @Composable
    fun raceWeightBoth(effective: String, configured: String): String
    @Composable
    fun raceWeightEffectiveOnly(effective: String): String
    @Composable
    val raceExpertConsensusTitle: String
    @Composable
    fun raceStarTag(n: Int, pct: Int): String
    @Composable
    fun raceRivalTag(n: Int, pct: Int): String
    @Composable
    fun raceSurpriseTag(n: Int, pct: Int): String
    @Composable
    fun raceAvoidTag(n: Int, pct: Int): String
    @Composable
    val raceExpertScoreTitle: String
    @Composable
    fun raceSupportConfidence(pct: String): String
    @Composable
    val raceMarketMoveTitle: String
    @Composable
    fun raceMarketFirst(pct: String): String
    @Composable
    fun raceMarketTo(pct: String): String
    @Composable
    fun raceMarketSamples(n: Int): String
    @Composable
    val raceMarketScoreTitle: String
    @Composable
    val raceFieldSignalTitle: String
    @Composable
    val raceFieldCombinedTitle: String
    @Composable
    fun raceFieldTjk(value: String): String
    @Composable
    fun raceFieldExpert(value: String): String
    @Composable
    fun raceFieldSamples(n: Int): String
    @Composable
    val raceLearningEffectTitle: String
    @Composable
    fun raceLearningBase(value: String): String
    @Composable
    fun raceLearningFinal(value: String): String
    @Composable
    val raceDeepViewTitle: String

    // ---- Home screen ----
    @Composable
    val homeDataFetchFailed: String
    @Composable
    val homeRefreshFailedStale: String
    @Composable
    val homeAllCities: String
    @Composable
    val homeRetryButton: String
    @Composable
    val homeNoRacesToShow: String
    @Composable
    val homeUpcomingRacesTitle: String
    @Composable
    val homeUpcomingRacesSubtitle: String
    @Composable
    val homeRacesPreparing: String
    @Composable
    val homeLiveUpdating: String
    @Composable
    val homeTagline: String
    @Composable
    val homeLogoDescription: String
    @Composable
    val homeAccountDescription: String
    @Composable
    val homeHistoryDescription: String
    @Composable
    val homeRefreshDescription: String
    @Composable
    val homeNextRaceLabel: String
    @Composable
    val homeOpenAnalysis: String
    @Composable
    val homeModelFavorite: String
    @Composable
    fun homeCourseNumber(n: Int): String
    @Composable
    val homeOpenRaceAnalysis: String
    @Composable
    fun homeCourseNumberCaps(n: Int): String
    @Composable
    fun homeSurprisePrefix(number: Int, name: String): String
    @Composable
    val homeOtherRemainingRaces: String
    @Composable
    fun homeUpcomingCount(n: Int): String
    @Composable
    val homeCloseOtherRaces: String
    @Composable
    val homeOpenOtherRaces: String
    @Composable
    val homeSixfoldTitle: String
    @Composable
    val homeSixfoldSubtitle: String
    @Composable
    val homeOpenSixfold: String
    @Composable
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
    @Composable
    val homeCountdownKaldi: String

    // ---- Analytics components (shared) ----
    @Composable
    val uncertaintyLow: String
    @Composable
    val uncertaintyMedium: String
    @Composable
    val uncertaintyHigh: String
    @Composable
    val uncertaintyVeryHigh: String
    @Composable
    val uncertaintyLowCaps: String
    @Composable
    val uncertaintyMediumCaps: String
    @Composable
    val uncertaintyHighCaps: String
    @Composable
    val uncertaintyVeryHighCaps: String
    @Composable
    val explanationClose: String
    @Composable
    val explanationTop3Close: String
    @Composable
    val explanationClearLeader: String
    @Composable
    fun uncertaintyLine(level: String, explanation: String): String
    @Composable
    val strategySingle: String
    @Composable
    val strategyCompact: String
    @Composable
    val strategySpread: String
    @Composable
    val strategyBalanced: String
    @Composable
    val strategyOneCandidate: String
    @Composable
    fun strategyCandidates(n: Int): String
    @Composable
    val strategyBackendDefault: String
    @Composable
    fun strategyLine(mode: String, reason: String): String
    @Composable
    val componentAgf: String
    @Composable
    val componentExpert: String
    @Composable
    val componentForm: String
    @Composable
    val componentHp: String
    @Composable
    val componentMarket: String
    @Composable
    val componentWeight: String
    @Composable
    val componentField: String
    @Composable
    val marketStrongUp: String
    @Composable
    val marketUp: String
    @Composable
    val marketFlat: String
    @Composable
    val marketDown: String
    @Composable
    val marketStrongDown: String
    @Composable
    val marketNone: String

    // ---- History screens ----
    @Composable
    val historyFetchFailed: String
    @Composable
    val historyLoading: String
    @Composable
    val historyEmptyTitle: String
    @Composable
    val historyEmptyBody: String
    @Composable
    val historyTitle: String
    @Composable
    fun historySnapshotCount(n: Int): String
    @Composable
    val historyHeaderTitle: String
    @Composable
    fun historyExpertRowCount(n: Int): String
    @Composable
    val historyModelPerformanceTitle: String
    @Composable
    fun historyBasedOnRaces(n: Int): String
    @Composable
    val historyTop1Hit: String
    @Composable
    val historyTop3Coverage: String
    @Composable
    val historyRaceLabel: String
    @Composable
    val historyCityTop1: String
    @Composable
    fun historyResultRatio(hits: Int, total: Int, pct: Int): String
    @Composable
    val historyDetailRankTitle: String
    @Composable
    val historyDetailRankSubtitle: String
    @Composable
    val historyResultNotReady: String
    @Composable
    val historyModelLeaderWon: String
    @Composable
    val historyRaceCompleted: String
    @Composable
    val historyModelLeaderLabel: String
    @Composable
    val historyWinnerLabel: String
    @Composable
    val historyPending: String
    @Composable
    fun historyExpertRecordCount(n: Int): String
    @Composable
    val historyWon: String
    @Composable
    val historyExactHit: String
    @Composable
    val historyModelRank: String
    @Composable
    val historyActualRank: String
    @Composable
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

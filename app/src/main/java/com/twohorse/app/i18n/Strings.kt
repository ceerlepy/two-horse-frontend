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
    fun loginErrorGoogleFailed(code: String): String
    @get:Composable
    val loginNameLabel: String
    @get:Composable
    val loginRegisterButton: String
    @get:Composable
    val loginPasswordHint: String
    @get:Composable
    val loginLegalNotice: String
    @get:Composable
    val loginErrorEmailTaken: String
    @get:Composable
    val loginErrorInvalidEmail: String
    @get:Composable
    val loginErrorWeakPassword: String

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
    @get:Composable
    val accountManageSubscription: String
    @get:Composable
    val accountDeleteButton: String
    @get:Composable
    val accountDeleteTitle: String
    @get:Composable
    val accountDeleteMessage: String
    @get:Composable
    val accountDeleteConfirm: String
    @get:Composable
    val accountDeleteCancel: String
    @get:Composable
    val accountDeleteFailed: String
    @get:Composable
    val accountPurchaseOtherAccount: String
    @get:Composable
    val loginShowPassword: String
    @get:Composable
    val loginHidePassword: String
    @get:Composable
    val loginBenefitTrial: String
    @get:Composable
    val loginBenefitSignals: String
    @get:Composable
    val loginBenefitCancel: String
    @get:Composable
    val accountChoosePlan: String
    @get:Composable
    val accountBadgePopular: String
    @get:Composable
    val accountPerMonth: String
    @get:Composable
    val accountRenewalTerms: String
    @get:Composable
    val accountRestorePurchases: String
    @get:Composable
    val accountRestoreNone: String
    @get:Composable
    val accountTrialBadge: String
    @get:Composable
    val accountCurrentPlan: String
    @get:Composable
    val accountFeatureProgram: String
    @get:Composable
    val accountFeatureTraining: String
    @get:Composable
    val accountFeatureSignals: String
    @get:Composable
    val accountFeatureCoupons: String
    @get:Composable
    val accountFeatureVideos: String
    @get:Composable
    val accountCouponLimitGold: String
    @get:Composable
    val accountCouponUnlimited: String
    @Composable
    fun accountDaysLeft(days: Int): String

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
    @get:Composable
    val couponCoverageInfoTitle: String
    @get:Composable
    val couponCoverageInfo: String
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
    val homeForeignTitle: String
    @get:Composable
    val homeForeignSubtitle: String
    @get:Composable
    val homeOpenForeign: String
    @get:Composable
    val foreignTitle: String
    @get:Composable
    val foreignSubtitle: String
    @get:Composable
    val foreignEmpty: String
    @get:Composable
    val foreignLoadFailed: String
    @get:Composable
    val foreignAgfNote: String
    @get:Composable
    val foreignWinProbNote: String
    @get:Composable
    val foreignWinProbInfoTitle: String
    @get:Composable
    val foreignWinProbInfo: String
    @Composable
    fun foreignWinProb(percent: String): String
    @Composable
    fun foreignRaceTitle(number: String): String
    @Composable
    fun foreignAiTop(horse: String): String
    @Composable
    fun foreignAiSelection(numbers: String): String
    @Composable
    fun foreignAiCouponTitle(number: Int): String
    @get:Composable
    val foreignAltCouponNote: String
    @Composable
    fun foreignModelCouponTitle(number: Int): String
    @get:Composable
    val foreignCouponButton: String
    @get:Composable
    val foreignResultTitle: String
    @get:Composable
    val askAiPickRace: String
    @get:Composable
    val foreignAltCouponExpand: String
    @get:Composable
    val foreignAltCouponCollapse: String
    @get:Composable
    val foreignModelCouponNote: String
    @Composable
    fun foreignCouponCoverage(percent: String): String
    @Composable
    fun foreignAiCouponStart(time: String): String
    @Composable
    fun foreignAiCouponTotal(combinations: Int, amount: String): String
    @Composable
    fun foreignAiLeg(raceNumber: Int, numbers: String): String
    @get:Composable
    val foreignBack: String
    @get:Composable
    val raceTrainingTitle: String
    @get:Composable
    val raceTrainingSubtitle: String
    @get:Composable
    val raceTrainingOpen: String
    @get:Composable
    val raceTrainingClose: String
    @get:Composable
    val raceTrainingEmpty: String
    @get:Composable
    val raceTrainingUnavailable: String
    @get:Composable
    val raceTrainingRetry: String
    @get:Composable
    val raceTrainingVideo: String
    @Composable
    fun raceTrainingJockey(name: String): String
    @get:Composable
    val raceFormTitle: String
    @get:Composable
    val raceFormSubtitle: String
    @get:Composable
    val raceFormOpen: String
    @get:Composable
    val raceFormClose: String
    @get:Composable
    val raceFormEmpty: String
    @get:Composable
    val raceFormNoRuns: String
    @Composable
    fun raceFormOdds(odds: String): String
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
    @Composable
    fun raceConsensusAvoid(sources: Int, avoid: Int, pct: Int): String
    @Composable
    fun raceConsensusPositive(sources: Int, n: Int, category: String, pct: Int): String
    @Composable
    fun raceConsensusPositiveWithAvoid(sources: Int, n: Int, category: String, pct: Int, avoid: Int): String
    @Composable
    fun raceConsensusNoDirection(sources: Int): String
    @get:Composable
    val raceCategoryBanko: String
    @get:Composable
    val raceCategoryFavorite: String
    @get:Composable
    val raceCategoryStrong: String
    @get:Composable
    val raceCategoryStar: String
    @get:Composable
    val raceCategorySurprise: String
    @get:Composable
    val raceCategoryRival: String
    @get:Composable
    val raceValueModelTitle: String
    @get:Composable
    val raceValueUnderrated: String
    @get:Composable
    val raceValueOverrated: String
    @get:Composable
    val raceValueStillFavourite: String
    @Composable
    fun raceValueVsAgf(market: String, model: String): String
    @Composable
    fun raceValueVsGanyan(market: String, model: String): String
    @get:Composable
    val raceValueNote: String
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
    @get:Composable
    val explanationThinData: String
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

    // ---- Premium package / coupon history ----
    @get:Composable
    val couponHistoryButton: String
    @get:Composable
    val couponHistoryTitle: String
    @get:Composable
    val couponHistorySubtitle: String
    @get:Composable
    val couponHistoryEmpty: String
    @get:Composable
    val couponHistoryPending: String
    @get:Composable
    val couponHistoryAllHit: String
    @get:Composable
    val couponHistoryBudget: String
    @get:Composable
    val homeDayOverTitle: String
    @get:Composable
    val homeDayOverBody: String
    @get:Composable
    val homeDayOverButton: String
    @Composable
    fun homeNextDayTitle(date: String): String
    @get:Composable
    val homeNextDayNote: String
    @Composable
    fun homeNextDayRunnerWeight(weight: String): String
    @Composable
    fun homeNextDayRunnerForm(form: String): String
    @Composable
    fun homeNextDayRunnerCount(n: Int): String
    @Composable
    fun homeNextDayExpertCount(n: Int): String
    @get:Composable
    val myCouponsButton: String
    @get:Composable
    val myCouponsTitle: String
    @get:Composable
    val myCouponsSubtitle: String
    @get:Composable
    val myCouponsEmpty: String
    @get:Composable
    val myCouponsSave: String
    @get:Composable
    val myCouponsSaved: String
    @get:Composable
    val myCouponsSaveFailed: String
    @get:Composable
    val myCouponsDelete: String
    @get:Composable
    val myCouponsLoadFailed: String
    @Composable
    fun myCouponsWinner(horseNumber: Int): String
    @Composable
    fun couponHistoryLegsHit(hit: Int, total: Int): String
    @Composable
    fun couponHistoryCost(totalTl: String, combinations: Int): String
    @Composable
    fun couponHistoryLeg(leg: Int, raceNumber: Int, horses: String): String
    @get:Composable
    val couponErrorDailyLimit: String
    @get:Composable
    val accountFeatureValueModel: String
    @get:Composable
    val accountFeatureCouponHistory: String
    @get:Composable
    val accountFeatureDailyCoupons: String
    @get:Composable
    val accountDailyCouponsGold: String
    @get:Composable
    val accountPeriodMonthly: String
    @get:Composable
    val accountPeriodYearly: String
    @get:Composable
    val accountYearlySaving: String
    @get:Composable
    val accountPerYear: String
    @Composable
    fun couponHistoryWindow(pool: String, windowNumber: Int): String
    @get:Composable
    val accountFeatureAskAi: String
    @get:Composable
    val accountAskAiPremium: String
    @get:Composable
    val accountFeatureMyCoupons: String

    // ---- AI'ya sor ----
    @get:Composable
    val askAiTitle: String
    @get:Composable
    val askAiSubtitle: String
    @get:Composable
    val askAiPlaceholder: String
    @get:Composable
    val askAiSend: String
    @get:Composable
    val askAiSuggestionFavorite: String
    @get:Composable
    val askAiSuggestionUpset: String
    @get:Composable
    val askAiSuggestionAgf: String
    @get:Composable
    val askAiDisclaimer: String
    @get:Composable
    val askAiYou: String
    @get:Composable
    val askAiLockedTitle: String
    @get:Composable
    val askAiLockedBody: String
    @get:Composable
    val askAiUpgrade: String
    @get:Composable
    val askAiErrorBusy: String
    @get:Composable
    val askAiErrorFailed: String
    @get:Composable
    val askAiErrorInvalid: String
    @get:Composable
    val askAiErrorRaceNotFound: String
    @get:Composable
    val askAiErrorUpgrade: String
    @Composable
    fun askAiRemaining(remaining: Int, limit: Int): String
    @Composable
    fun askAiErrorDailyLimit(limit: Int): String
    @get:Composable
    val askAiOpen: String
    @get:Composable
    val askAiClose: String
    @get:Composable
    val askAiClear: String
    @get:Composable
    val askAiThinking: String
    @Composable
    fun raceRankLabel(rank: Int): String
    @get:Composable
    val raceExpertScoreHint: String
    @get:Composable
    val raceMarketScoreHint: String
    @get:Composable
    val raceFieldHint: String
    @get:Composable
    val raceFormHint: String
    @get:Composable
    val raceFormTenPlus: String
    @get:Composable
    val raceRiskHint: String
    @get:Composable
    val raceOpenVideos: String
    @get:Composable
    val raceCloseVideos: String
    @get:Composable
    val couponHeaderSubtitle: String
    @get:Composable
    val myCouponsLocked: String
    @get:Composable
    val myCouponsUpgrade: String
    @get:Composable
    val homeDayOverCouponsBody: String
    @get:Composable
    val homeDayOverCouponsButton: String
    @Composable
    fun raceLeaderMarginValue(value: String): String
    @get:Composable
    val infoOk: String
    @get:Composable
    val infoOpen: String
    @get:Composable
    val infoGuven: String
    @get:Composable
    val infoAgf: String
    @get:Composable
    val infoHp: String
    @get:Composable
    val infoUncertainty: String
    @get:Composable
    val infoLeaderMargin: String
    @get:Composable
    val infoCouponAdvice: String
    @get:Composable
    val infoValueModel: String
    @get:Composable
    val infoExpert: String
    @get:Composable
    val infoMarket: String
    @get:Composable
    val infoField: String
    @get:Composable
    val infoForm: String
    @get:Composable
    val raceCategoryAvoid: String
    @get:Composable
    val raceMarketNoDataHint: String
    @get:Composable
    val raceExpertNone: String
    @Composable
    fun raceCategoryCount(n: Int): String
    @get:Composable
    val strategyShortSingle: String
    @get:Composable
    val strategyShortCompact: String
    @get:Composable
    val strategyShortSpread: String
    @get:Composable
    val strategyShortBalanced: String
    @Composable
    fun couponLegRace(n: Int): String
    @get:Composable
    val infoModelScoreTitle: String
    @get:Composable
    val infoModelScore: String
    @get:Composable
    val errorTitle: String
    @get:Composable
    val errorTimeout: String
    @get:Composable
    val errorNoInternet: String
    @get:Composable
    val errorServer: String
    @get:Composable
    val errorGeneric: String
    @Composable
    fun raceCategoryCountOf(n: Int, total: Int): String
    @get:Composable
    val expertRowStrong: String
    @get:Composable
    val expertRowSurprise: String
    @get:Composable
    val expertRowAvoid: String
    @get:Composable
    val expertChipFavorite: String
    @get:Composable
    val expertChipBanko: String
    @get:Composable
    val expertChipStar: String
    @get:Composable
    val expertFirstChoiceInfo: String
    @Composable
    fun raceExpertHeroStrong(a1: Int): String
    @get:Composable
    val accountBillingUnavailable: String
    @get:Composable
    val accountBillingUnavailableButton: String
    @Composable
    fun homeNextDayExpertHorses(a1: Int): String
    @get:Composable
    val raceExpertFirstChoice: String
    @get:Composable
    val raceExpertSecondChoice: String
    @Composable
    fun raceExpertHeroFirst(a1: Int, a2: Int): String
    @Composable
    fun raceExpertHeroSecond(a1: Int): String
    @get:Composable
    val welcomeTitle: String
    @Composable
    fun welcomeBody(a1: String): String
    @get:Composable
    val welcomeStart: String
    @get:Composable
    val welcomePlans: String
    @get:Composable
    val trialEndedTitle: String
    @get:Composable
    val trialEndedBody: String
    @get:Composable
    val trialEndedCompare: String
    @get:Composable
    val trialEndedContinueFree: String
    @get:Composable
    val homeTrialStrip: String
    @get:Composable
    val accountChangePlan: String
    @get:Composable
    val accountSwitchToGold: String
    @get:Composable
    val accountGoldDowngradeNote: String
    @get:Composable
    val accountSwitchTitle: String
    @Composable
    fun accountSwitchBody(a1: String): String
    @Composable
    fun accountSwitchScheduled(a1: String): String
    @Composable
    fun accountSwitchScheduledCard(a1: String): String
    @get:Composable
    val accountCancelButton: String
    @Composable
    fun accountCancelBody(a1: String, a2: String): String
    @get:Composable
    val accountGoToPlay: String
    @Composable
    fun accountCanceledCard(a1: String): String
    @Composable
    fun accountCanceledNote(a1: String, a2: String): String
    @get:Composable
    val accountResume: String
}

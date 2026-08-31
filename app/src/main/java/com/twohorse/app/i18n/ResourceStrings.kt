package com.twohorse.app.i18n

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.twohorse.app.R

/*
 * Implements Strings by reading real Android string resources
 * (res/values/strings.xml = Turkish/default, res/values-en/strings.xml
 * = English) -- the actual per-locale text lives there, this is just
 * a typed, compile-time-checked facade over stringResource() so call
 * sites keep passing typed arguments instead of a raw vararg.
 */
object ResourceStrings : Strings {

    override val noData: String
        @Composable get() = stringResource(R.string.no_data)

    override val startingSoon: String
        @Composable get() = stringResource(R.string.starting_soon)

    override val back: String
        @Composable get() = stringResource(R.string.back)

    override val loginSubtitle: String
        @Composable get() = stringResource(R.string.login_subtitle)

    override val loginGoogleButton: String
        @Composable get() = stringResource(R.string.login_google_button)

    override val loginOr: String
        @Composable get() = stringResource(R.string.login_or)

    override val loginEmailLabel: String
        @Composable get() = stringResource(R.string.login_email_label)

    override val loginPasswordLabel: String
        @Composable get() = stringResource(R.string.login_password_label)

    override val loginSubmitButton: String
        @Composable get() = stringResource(R.string.login_submit_button)

    override val loginErrorInvalidCredentials: String
        @Composable get() = stringResource(R.string.login_error_invalid_credentials)

    override val loginErrorEmailPasswordRequired: String
        @Composable get() = stringResource(R.string.login_error_email_password_required)

    override val loginErrorEmailNotVerified: String
        @Composable get() = stringResource(R.string.login_error_email_not_verified)

    override val loginErrorNotConfigured: String
        @Composable get() = stringResource(R.string.login_error_not_configured)

    override val loginErrorGeneric: String
        @Composable get() = stringResource(R.string.login_error_generic)

    override val loginErrorGoogleIncomplete: String
        @Composable get() = stringResource(R.string.login_error_google_incomplete)

    @Composable
    override fun loginErrorGoogleFailed(code: Int): String =
        stringResource(R.string.login_error_google_failed, code)

    override val accountTitle: String
        @Composable get() = stringResource(R.string.account_title)

    @Composable
    override fun accountTrialEndsAt(date: String): String =
        stringResource(R.string.account_trial_ends_at, date)

    @Composable
    override fun accountSubscriptionRenewsAt(date: String): String =
        stringResource(R.string.account_subscription_renews_at, date)

    override val accountUnlimited: String
        @Composable get() = stringResource(R.string.account_unlimited)

    override val accountGoldDescription: String
        @Composable get() = stringResource(R.string.account_gold_description)

    override val accountPremiumDescription: String
        @Composable get() = stringResource(R.string.account_premium_description)

    @Composable
    override fun accountUpgradeTo(tierTitle: String): String =
        stringResource(R.string.account_upgrade_to, tierTitle)

    override val accountLoadingEllipsis: String
        @Composable get() = stringResource(R.string.account_loading_ellipsis)

    override val accountAlreadyPremium: String
        @Composable get() = stringResource(R.string.account_already_premium)

    override val accountLogout: String
        @Composable get() = stringResource(R.string.account_logout)

    @Composable
    override fun accountPurchaseActivated(tierTitle: String): String =
        stringResource(R.string.account_purchase_activated, tierTitle)

    override val accountPurchaseVerifyFailed: String
        @Composable get() = stringResource(R.string.account_purchase_verify_failed)

    override val couponErrorCityRequired: String
        @Composable get() = stringResource(R.string.coupon_error_city_required)

    override val couponErrorValidBudgetRequired: String
        @Composable get() = stringResource(R.string.coupon_error_valid_budget_required)

    override val couponErrorSixfoldNotFound: String
        @Composable get() = stringResource(R.string.coupon_error_sixfold_not_found)

    override val couponErrorNotEnoughRaces: String
        @Composable get() = stringResource(R.string.coupon_error_not_enough_races)

    override val couponErrorNoRunners: String
        @Composable get() = stringResource(R.string.coupon_error_no_runners)

    override val couponErrorTierUpgradeRequired: String
        @Composable get() = stringResource(R.string.coupon_error_tier_upgrade_required)

    override val couponErrorBudgetCapExceeded: String
        @Composable get() = stringResource(R.string.coupon_error_budget_cap_exceeded)

    override val couponErrorAuthRequired: String
        @Composable get() = stringResource(R.string.coupon_error_auth_required)

    override val couponErrorNotFound: String
        @Composable get() = stringResource(R.string.coupon_error_not_found)

    override val couponErrorBadRequest: String
        @Composable get() = stringResource(R.string.coupon_error_bad_request)

    override val couponErrorServerUnavailable: String
        @Composable get() = stringResource(R.string.coupon_error_server_unavailable)

    override val couponErrorNoInternet: String
        @Composable get() = stringResource(R.string.coupon_error_no_internet)

    override val couponErrorTimeout: String
        @Composable get() = stringResource(R.string.coupon_error_timeout)

    override val couponErrorGeneric: String
        @Composable get() = stringResource(R.string.coupon_error_generic)

    override val couponHeaderTitle: String
        @Composable get() = stringResource(R.string.coupon_header_title)

    override val poolLabelSixfold: String
        @Composable get() = stringResource(R.string.pool_label_sixfold)

    override val poolLabelFivefold: String
        @Composable get() = stringResource(R.string.pool_label_fivefold)

    override val couponCitySelectFailed: String
        @Composable get() = stringResource(R.string.coupon_city_select_failed)

    override val couponUnexpectedWindow: String
        @Composable get() = stringResource(R.string.coupon_unexpected_window)

    override val couponBudgetExceeded: String
        @Composable get() = stringResource(R.string.coupon_budget_exceeded)

    override val couponGenerateButton: String
        @Composable get() = stringResource(R.string.coupon_generate_button)

    override val couponUpgradeButton: String
        @Composable get() = stringResource(R.string.coupon_upgrade_button)

    override val couponCityTitle: String
        @Composable get() = stringResource(R.string.coupon_city_title)

    override val couponCitySubtitle: String
        @Composable get() = stringResource(R.string.coupon_city_subtitle)

    override val couponTypeTitle: String
        @Composable get() = stringResource(R.string.coupon_type_title)

    override val couponTypeSubtitle: String
        @Composable get() = stringResource(R.string.coupon_type_subtitle)

    @Composable
    override fun couponWindowSubtitle(poolLabelLower: String): String =
        stringResource(R.string.coupon_window_subtitle, poolLabelLower)

    override val couponBudgetTitle: String
        @Composable get() = stringResource(R.string.coupon_budget_title)

    override val couponBudgetSubtitle: String
        @Composable get() = stringResource(R.string.coupon_budget_subtitle)

    @Composable
    override fun couponWindowStarted(poolLabel: String): String =
        stringResource(R.string.coupon_window_started, poolLabel)

    @Composable
    override fun couponGeneratedSummary(
        city: String,
        window: Int,
        poolLabel: String,
        budgetTl: Int
    ): String =
        stringResource(
            R.string.coupon_generated_summary,
            city,
            window,
            poolLabel,
            budgetTl
        )

    override val couponNoneGenerated: String
        @Composable get() = stringResource(R.string.coupon_none_generated)

    override val couponIntroTitle: String
        @Composable get() = stringResource(R.string.coupon_intro_title)

    override val couponIntroSubtitle: String
        @Composable get() = stringResource(R.string.coupon_intro_subtitle)

    @Composable
    override fun couponRaceRange(startRace: String, endRace: String): String =
        stringResource(R.string.coupon_race_range, startRace, endRace)

    @Composable
    override fun couponMaxBudgetAndCount(budgetTl: Int, couponCount: Int): String =
        stringResource(R.string.coupon_max_budget_and_count, budgetTl, couponCount)

    @Composable
    override fun couponUnitPriceAndMultiplier(unitPriceTl: String, multiplier: Int): String =
        stringResource(R.string.coupon_unit_price_and_multiplier, unitPriceTl, multiplier)

    @Composable
    override fun couponGeneratedAt(time: String): String =
        stringResource(R.string.coupon_generated_at, time)

    override val couponLadderTitle: String
        @Composable get() = stringResource(R.string.coupon_ladder_title)

    override val couponLadderFixed1Title: String
        @Composable get() = stringResource(R.string.coupon_ladder_fixed1_title)

    override val couponLadderFixed1Desc: String
        @Composable get() = stringResource(R.string.coupon_ladder_fixed1_desc)

    override val couponLadderFixed2Title: String
        @Composable get() = stringResource(R.string.coupon_ladder_fixed2_title)

    override val couponLadderFixed2Desc: String
        @Composable get() = stringResource(R.string.coupon_ladder_fixed2_desc)

    override val couponLadderVariableTitle: String
        @Composable get() = stringResource(R.string.coupon_ladder_variable_title)

    override val couponLadderVariableDesc: String
        @Composable get() = stringResource(R.string.coupon_ladder_variable_desc)

    override val couponLadderFooter: String
        @Composable get() = stringResource(R.string.coupon_ladder_footer)

    @Composable
    override fun couponTierLabel(index: Int, total: Int): String =
        stringResource(R.string.coupon_tier_label, index, total)

    @Composable
    override fun couponAmountLabel(amount: Int): String =
        stringResource(R.string.coupon_amount_label, amount)

    override val couponMetricCombinations: String
        @Composable get() = stringResource(R.string.coupon_metric_combinations)

    override val couponMetricCoverage: String
        @Composable get() = stringResource(R.string.coupon_metric_coverage)

    @Composable
    override fun couponLegCoverage(pct: String): String =
        stringResource(R.string.coupon_leg_coverage, pct)

    @Composable
    override fun couponWindowOrdinal(number: Int, poolLabel: String): String =
        stringResource(R.string.coupon_window_ordinal, number, poolLabel)

    override val raceNotFoundInProgram: String
        @Composable get() = stringResource(R.string.race_not_found_in_program)

    override val raceRefreshFailed: String
        @Composable get() = stringResource(R.string.race_refresh_failed)

    override val raceCouponButton: String
        @Composable get() = stringResource(R.string.race_coupon_button)

    override val raceAllHorsesTitle: String
        @Composable get() = stringResource(R.string.race_all_horses_title)

    @Composable
    override fun raceHorseCount(n: Int): String =
        stringResource(R.string.race_horse_count, n)

    override val raceRefresh: String
        @Composable get() = stringResource(R.string.race_refresh)

    @Composable
    override fun raceCityAndNumber(city: String, number: Int): String =
        stringResource(R.string.race_city_and_number, city, number)

    override val raceLikelyWinner: String
        @Composable get() = stringResource(R.string.race_likely_winner)

    @Composable
    override fun raceConfidenceScore(score: String): String =
        stringResource(R.string.race_confidence_score, score)

    override val raceAgf: String
        @Composable get() = stringResource(R.string.race_agf)

    override val raceHp: String
        @Composable get() = stringResource(R.string.race_hp)

    @Composable
    override fun raceHpPoints(n: Int): String =
        stringResource(R.string.race_hp_points, n)

    override val raceExpertSupport: String
        @Composable get() = stringResource(R.string.race_expert_support)

    override val raceField: String
        @Composable get() = stringResource(R.string.race_field)

    override val raceMarket: String
        @Composable get() = stringResource(R.string.race_market)

    override val raceForm: String
        @Composable get() = stringResource(R.string.race_form)

    override val raceLearning: String
        @Composable get() = stringResource(R.string.race_learning)

    @Composable
    override fun raceLearningDelta(signedValue: String): String =
        stringResource(R.string.race_learning_delta, signedValue)

    @Composable
    override fun raceTopRival(number: Int, name: String, score: Int): String =
        stringResource(R.string.race_top_rival, number, name, score)

    @Composable
    override fun raceSurprise(number: Int, name: String, score: Int): String =
        stringResource(R.string.race_surprise, number, name, score)

    override val raceRiskMapTitle: String
        @Composable get() = stringResource(R.string.race_risk_map_title)

    override val raceUncertaintyMetric: String
        @Composable get() = stringResource(R.string.race_uncertainty_metric)

    override val raceLeaderMarginMetric: String
        @Composable get() = stringResource(R.string.race_leader_margin_metric)

    override val raceExpansionMetric: String
        @Composable get() = stringResource(R.string.race_expansion_metric)

    override val raceDeepAnalysisTitle: String
        @Composable get() = stringResource(R.string.race_deep_analysis_title)

    override val raceDeepAnalysisSubtitle: String
        @Composable get() = stringResource(R.string.race_deep_analysis_subtitle)

    override val raceCloseDeepAnalysis: String
        @Composable get() = stringResource(R.string.race_close_deep_analysis)

    override val raceOpenDeepAnalysis: String
        @Composable get() = stringResource(R.string.race_open_deep_analysis)

    override val raceInfoFallback: String
        @Composable get() = stringResource(R.string.race_info_fallback)

    override val raceExpertSourceMissing: String
        @Composable get() = stringResource(R.string.race_expert_source_missing)

    @Composable
    override fun raceExpertSourcesCount(n: Int): String =
        stringResource(R.string.race_expert_sources_count, n)

    @Composable
    override fun raceExpertFavoriteCount(n: Int): String =
        stringResource(R.string.race_expert_favorite_count, n)

    @Composable
    override fun raceExpertBankoCount(n: Int): String =
        stringResource(R.string.race_expert_banko_count, n)

    @Composable
    override fun raceExpertStrongCount(n: Int): String =
        stringResource(R.string.race_expert_strong_count, n)

    @Composable
    override fun raceFieldCombined(value: String): String =
        stringResource(R.string.race_field_combined, value)

    override val raceGuven: String
        @Composable get() = stringResource(R.string.race_guven)

    override val raceVideoLabel: String
        @Composable get() = stringResource(R.string.race_video_label)

    override val raceVideoLabelLocked: String
        @Composable get() = stringResource(R.string.race_video_label_locked)

    override val raceVideoLockedBody: String
        @Composable get() = stringResource(R.string.race_video_locked_body)

    override val raceVideoNotFound: String
        @Composable get() = stringResource(R.string.race_video_not_found)

    override val raceVideoFallbackLabel: String
        @Composable get() = stringResource(R.string.race_video_fallback_label)

    override val raceCloseModelDetail: String
        @Composable get() = stringResource(R.string.race_close_model_detail)

    override val raceOpenModelDetail: String
        @Composable get() = stringResource(R.string.race_open_model_detail)

    override val raceScoreComponents: String
        @Composable get() = stringResource(R.string.race_score_components)

    @Composable
    override fun raceWeightBoth(effective: String, configured: String): String =
        stringResource(R.string.race_weight_both, effective, configured)

    @Composable
    override fun raceWeightEffectiveOnly(effective: String): String =
        stringResource(R.string.race_weight_effective_only, effective)

    override val raceExpertConsensusTitle: String
        @Composable get() = stringResource(R.string.race_expert_consensus_title)

    @Composable
    override fun raceStarTag(n: Int, pct: Int): String =
        stringResource(R.string.race_star_tag, n, pct)

    @Composable
    override fun raceRivalTag(n: Int, pct: Int): String =
        stringResource(R.string.race_rival_tag, n, pct)

    @Composable
    override fun raceSurpriseTag(n: Int, pct: Int): String =
        stringResource(R.string.race_surprise_tag, n, pct)

    @Composable
    override fun raceAvoidTag(n: Int, pct: Int): String =
        stringResource(R.string.race_avoid_tag, n, pct)

    override val raceExpertScoreTitle: String
        @Composable get() = stringResource(R.string.race_expert_score_title)

    @Composable
    override fun raceSupportConfidence(pct: String): String =
        stringResource(R.string.race_support_confidence, pct)

    override val raceMarketMoveTitle: String
        @Composable get() = stringResource(R.string.race_market_move_title)

    @Composable
    override fun raceMarketFirst(pct: String): String =
        stringResource(R.string.race_market_first, pct)

    @Composable
    override fun raceMarketTo(pct: String): String =
        stringResource(R.string.race_market_to, pct)

    @Composable
    override fun raceMarketSamples(n: Int): String =
        stringResource(R.string.race_market_samples, n)

    override val raceMarketScoreTitle: String
        @Composable get() = stringResource(R.string.race_market_score_title)

    override val raceFieldSignalTitle: String
        @Composable get() = stringResource(R.string.race_field_signal_title)

    override val raceFieldCombinedTitle: String
        @Composable get() = stringResource(R.string.race_field_combined_title)

    @Composable
    override fun raceFieldTjk(value: String): String =
        stringResource(R.string.race_field_tjk, value)

    @Composable
    override fun raceFieldExpert(value: String): String =
        stringResource(R.string.race_field_expert, value)

    @Composable
    override fun raceFieldSamples(n: Int): String =
        stringResource(R.string.race_field_samples, n)

    override val raceLearningEffectTitle: String
        @Composable get() = stringResource(R.string.race_learning_effect_title)

    @Composable
    override fun raceLearningBase(value: String): String =
        stringResource(R.string.race_learning_base, value)

    @Composable
    override fun raceLearningFinal(value: String): String =
        stringResource(R.string.race_learning_final, value)

    override val raceDeepViewTitle: String
        @Composable get() = stringResource(R.string.race_deep_view_title)

    override val homeDataFetchFailed: String
        @Composable get() = stringResource(R.string.home_data_fetch_failed)

    override val homeRefreshFailedStale: String
        @Composable get() = stringResource(R.string.home_refresh_failed_stale)

    override val homeAllCities: String
        @Composable get() = stringResource(R.string.home_all_cities)

    override val homeRetryButton: String
        @Composable get() = stringResource(R.string.home_retry_button)

    override val homeNoRacesToShow: String
        @Composable get() = stringResource(R.string.home_no_races_to_show)

    override val homeUpcomingRacesTitle: String
        @Composable get() = stringResource(R.string.home_upcoming_races_title)

    override val homeUpcomingRacesSubtitle: String
        @Composable get() = stringResource(R.string.home_upcoming_races_subtitle)

    override val homeRacesPreparing: String
        @Composable get() = stringResource(R.string.home_races_preparing)

    override val homeLiveUpdating: String
        @Composable get() = stringResource(R.string.home_live_updating)

    override val homeTagline: String
        @Composable get() = stringResource(R.string.home_tagline)

    override val homeLogoDescription: String
        @Composable get() = stringResource(R.string.home_logo_description)

    override val homeAccountDescription: String
        @Composable get() = stringResource(R.string.home_account_description)

    override val homeHistoryDescription: String
        @Composable get() = stringResource(R.string.home_history_description)

    override val homeRefreshDescription: String
        @Composable get() = stringResource(R.string.home_refresh_description)

    override val homeNextRaceLabel: String
        @Composable get() = stringResource(R.string.home_next_race_label)

    override val homeOpenAnalysis: String
        @Composable get() = stringResource(R.string.home_open_analysis)

    override val homeModelFavorite: String
        @Composable get() = stringResource(R.string.home_model_favorite)

    @Composable
    override fun homeCourseNumber(n: Int): String =
        stringResource(R.string.home_course_number, n)

    override val homeOpenRaceAnalysis: String
        @Composable get() = stringResource(R.string.home_open_race_analysis)

    @Composable
    override fun homeCourseNumberCaps(n: Int): String =
        stringResource(R.string.home_course_number_caps, n)

    @Composable
    override fun homeSurprisePrefix(number: Int, name: String): String =
        stringResource(R.string.home_surprise_prefix, number, name)

    override val homeOtherRemainingRaces: String
        @Composable get() = stringResource(R.string.home_other_remaining_races)

    @Composable
    override fun homeUpcomingCount(n: Int): String =
        stringResource(R.string.home_upcoming_count, n)

    override val homeCloseOtherRaces: String
        @Composable get() = stringResource(R.string.home_close_other_races)

    override val homeOpenOtherRaces: String
        @Composable get() = stringResource(R.string.home_open_other_races)

    override val homeSixfoldTitle: String
        @Composable get() = stringResource(R.string.home_sixfold_title)

    override val homeSixfoldSubtitle: String
        @Composable get() = stringResource(R.string.home_sixfold_subtitle)

    override val homeOpenSixfold: String
        @Composable get() = stringResource(R.string.home_open_sixfold)

    override val homeFavoriteSummaryFallback: String
        @Composable get() = stringResource(R.string.home_favorite_summary_fallback)

    @Composable
    override fun homeAgf(pct: String): String =
        stringResource(R.string.home_agf, pct)

    @Composable
    override fun homeExpertSourceLabel(n: Int): String =
        stringResource(R.string.home_expert_source_label, n)

    @Composable
    override fun homeHpLabel(n: Int): String =
        stringResource(R.string.home_hp_label, n)

    @Composable
    override fun homeCityRaceLabel(city: String, number: Int): String =
        stringResource(R.string.home_city_race_label, city, number)

    @Composable
    override fun homeFavoritePrefix(number: Int, name: String): String =
        stringResource(R.string.home_favorite_prefix, number, name)

    override val homeCountdownKaldi: String
        @Composable get() = stringResource(R.string.home_countdown_kaldi)

    override val uncertaintyLow: String
        @Composable get() = stringResource(R.string.uncertainty_low)

    override val uncertaintyMedium: String
        @Composable get() = stringResource(R.string.uncertainty_medium)

    override val uncertaintyHigh: String
        @Composable get() = stringResource(R.string.uncertainty_high)

    override val uncertaintyVeryHigh: String
        @Composable get() = stringResource(R.string.uncertainty_very_high)

    override val uncertaintyLowCaps: String
        @Composable get() = stringResource(R.string.uncertainty_low_caps)

    override val uncertaintyMediumCaps: String
        @Composable get() = stringResource(R.string.uncertainty_medium_caps)

    override val uncertaintyHighCaps: String
        @Composable get() = stringResource(R.string.uncertainty_high_caps)

    override val uncertaintyVeryHighCaps: String
        @Composable get() = stringResource(R.string.uncertainty_very_high_caps)

    override val explanationClose: String
        @Composable get() = stringResource(R.string.explanation_close)

    override val explanationTop3Close: String
        @Composable get() = stringResource(R.string.explanation_top3_close)

    override val explanationClearLeader: String
        @Composable get() = stringResource(R.string.explanation_clear_leader)

    @Composable
    override fun uncertaintyLine(level: String, explanation: String): String =
        stringResource(R.string.uncertainty_line, level, explanation)

    override val strategySingle: String
        @Composable get() = stringResource(R.string.strategy_single)

    override val strategyCompact: String
        @Composable get() = stringResource(R.string.strategy_compact)

    override val strategySpread: String
        @Composable get() = stringResource(R.string.strategy_spread)

    override val strategyBalanced: String
        @Composable get() = stringResource(R.string.strategy_balanced)

    override val strategyOneCandidate: String
        @Composable get() = stringResource(R.string.strategy_one_candidate)

    @Composable
    override fun strategyCandidates(n: Int): String =
        stringResource(R.string.strategy_candidates, n)

    override val strategyBackendDefault: String
        @Composable get() = stringResource(R.string.strategy_backend_default)

    @Composable
    override fun strategyLine(mode: String, reason: String): String =
        stringResource(R.string.strategy_line, mode, reason)

    override val componentAgf: String
        @Composable get() = stringResource(R.string.component_agf)

    override val componentExpert: String
        @Composable get() = stringResource(R.string.component_expert)

    override val componentForm: String
        @Composable get() = stringResource(R.string.component_form)

    override val componentHp: String
        @Composable get() = stringResource(R.string.component_hp)

    override val componentMarket: String
        @Composable get() = stringResource(R.string.component_market)

    override val componentWeight: String
        @Composable get() = stringResource(R.string.component_weight)

    override val componentField: String
        @Composable get() = stringResource(R.string.component_field)

    override val marketStrongUp: String
        @Composable get() = stringResource(R.string.market_strong_up)

    override val marketUp: String
        @Composable get() = stringResource(R.string.market_up)

    override val marketFlat: String
        @Composable get() = stringResource(R.string.market_flat)

    override val marketDown: String
        @Composable get() = stringResource(R.string.market_down)

    override val marketStrongDown: String
        @Composable get() = stringResource(R.string.market_strong_down)

    override val marketNone: String
        @Composable get() = stringResource(R.string.market_none)

    override val historyFetchFailed: String
        @Composable get() = stringResource(R.string.history_fetch_failed)

    override val historyLoading: String
        @Composable get() = stringResource(R.string.history_loading)

    override val historyEmptyTitle: String
        @Composable get() = stringResource(R.string.history_empty_title)

    override val historyEmptyBody: String
        @Composable get() = stringResource(R.string.history_empty_body)

    override val historyTitle: String
        @Composable get() = stringResource(R.string.history_title)

    @Composable
    override fun historySnapshotCount(n: Int): String =
        stringResource(R.string.history_snapshot_count, n)

    override val historyHeaderTitle: String
        @Composable get() = stringResource(R.string.history_header_title)

    @Composable
    override fun historyExpertRowCount(n: Int): String =
        stringResource(R.string.history_expert_row_count, n)

    override val historyModelPerformanceTitle: String
        @Composable get() = stringResource(R.string.history_model_performance_title)

    @Composable
    override fun historyBasedOnRaces(n: Int): String =
        stringResource(R.string.history_based_on_races, n)

    override val historyTop1Hit: String
        @Composable get() = stringResource(R.string.history_top1_hit)

    override val historyTop3Coverage: String
        @Composable get() = stringResource(R.string.history_top3_coverage)

    override val historyRaceLabel: String
        @Composable get() = stringResource(R.string.history_race_label)

    override val historyCityTop1: String
        @Composable get() = stringResource(R.string.history_city_top1)

    @Composable
    override fun historyResultRatio(hits: Int, total: Int, pct: Int): String =
        stringResource(R.string.history_result_ratio, hits, total, pct)

    override val historyDetailRankTitle: String
        @Composable get() = stringResource(R.string.history_detail_rank_title)

    override val historyDetailRankSubtitle: String
        @Composable get() = stringResource(R.string.history_detail_rank_subtitle)

    override val historyResultNotReady: String
        @Composable get() = stringResource(R.string.history_result_not_ready)

    override val historyModelLeaderWon: String
        @Composable get() = stringResource(R.string.history_model_leader_won)

    override val historyRaceCompleted: String
        @Composable get() = stringResource(R.string.history_race_completed)

    override val historyModelLeaderLabel: String
        @Composable get() = stringResource(R.string.history_model_leader_label)

    override val historyWinnerLabel: String
        @Composable get() = stringResource(R.string.history_winner_label)

    override val historyPending: String
        @Composable get() = stringResource(R.string.history_pending)

    @Composable
    override fun historyExpertRecordCount(n: Int): String =
        stringResource(R.string.history_expert_record_count, n)

    override val historyWon: String
        @Composable get() = stringResource(R.string.history_won)

    override val historyExactHit: String
        @Composable get() = stringResource(R.string.history_exact_hit)

    override val historyModelRank: String
        @Composable get() = stringResource(R.string.history_model_rank)

    override val historyActualRank: String
        @Composable get() = stringResource(R.string.history_actual_rank)

    override val historyModelScore: String
        @Composable get() = stringResource(R.string.history_model_score)

    @Composable
    override fun historyCityAndDate(city: String, date: String): String =
        stringResource(R.string.history_city_and_date, city, date)

    @Composable
    override fun historyCityAndRace(city: String, raceNumber: Int): String =
        stringResource(R.string.history_city_and_race, city, raceNumber)

    @Composable
    override fun historyDateAndTime(date: String, time: String): String =
        stringResource(R.string.history_date_and_time, date, time)

    @Composable
    override fun historyRaceNumberAbbrev(n: Int): String =
        stringResource(R.string.history_race_number_abbrev, n)

    @Composable
    override fun accountTierTitle(tier: String): String =
        when (tier) {
            "gold" -> "Gold"
            "premium" -> "Premium"
            else -> "Free"
        }
}

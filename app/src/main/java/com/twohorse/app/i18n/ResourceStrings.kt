package com.twohorse.app.i18n

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
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
    override fun loginErrorGoogleFailed(code: String): String =
        stringResource(R.string.login_error_google_failed, code)

    override val loginNameLabel: String
        @Composable get() = stringResource(R.string.login_name_label)

    override val loginRegisterButton: String
        @Composable get() = stringResource(R.string.login_register_button)

    override val loginPasswordHint: String
        @Composable get() = stringResource(R.string.login_password_hint)

    override val loginLegalNotice: String
        @Composable get() = stringResource(R.string.login_legal_notice)

    override val loginErrorEmailTaken: String
        @Composable get() = stringResource(R.string.login_error_email_taken)

    override val loginErrorInvalidEmail: String
        @Composable get() = stringResource(R.string.login_error_invalid_email)

    override val loginErrorWeakPassword: String
        @Composable get() = stringResource(R.string.login_error_weak_password)

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

    override val accountManageSubscription: String
        @Composable get() = stringResource(R.string.account_manage_subscription)

    override val accountDeleteButton: String
        @Composable get() = stringResource(R.string.account_delete_button)

    override val accountDeleteTitle: String
        @Composable get() = stringResource(R.string.account_delete_title)

    override val accountDeleteMessage: String
        @Composable get() = stringResource(R.string.account_delete_message)

    override val accountDeleteConfirm: String
        @Composable get() = stringResource(R.string.account_delete_confirm)

    override val accountDeleteCancel: String
        @Composable get() = stringResource(R.string.account_delete_cancel)

    override val accountDeleteFailed: String
        @Composable get() = stringResource(R.string.account_delete_failed)

    override val accountPurchaseOtherAccount: String
        @Composable get() = stringResource(R.string.account_purchase_other_account)

    override val loginShowPassword: String
        @Composable get() = stringResource(R.string.login_show_password)

    override val loginHidePassword: String
        @Composable get() = stringResource(R.string.login_hide_password)

    override val loginBenefitTrial: String
        @Composable get() = stringResource(R.string.login_benefit_trial)

    override val loginBenefitSignals: String
        @Composable get() = stringResource(R.string.login_benefit_signals)

    override val loginBenefitCancel: String
        @Composable get() = stringResource(R.string.login_benefit_cancel)

    override val accountChoosePlan: String
        @Composable get() = stringResource(R.string.account_choose_plan)

    override val accountBadgePopular: String
        @Composable get() = stringResource(R.string.account_badge_popular)

    override val accountPerMonth: String
        @Composable get() = stringResource(R.string.account_per_month)

    override val accountRenewalTerms: String
        @Composable get() = stringResource(R.string.account_renewal_terms)

    override val accountRestorePurchases: String
        @Composable get() = stringResource(R.string.account_restore_purchases)

    override val accountRestoreNone: String
        @Composable get() = stringResource(R.string.account_restore_none)

    override val accountTrialBadge: String
        @Composable get() = stringResource(R.string.account_trial_badge)

    override val accountCurrentPlan: String
        @Composable get() = stringResource(R.string.account_current_plan)

    override val accountFeatureProgram: String
        @Composable get() = stringResource(R.string.account_feature_program)

    override val accountFeatureTraining: String
        @Composable get() = stringResource(R.string.account_feature_training)

    override val accountFeatureSignals: String
        @Composable get() = stringResource(R.string.account_feature_signals)

    override val accountFeatureCoupons: String
        @Composable get() = stringResource(R.string.account_feature_coupons)

    override val accountFeatureVideos: String
        @Composable get() = stringResource(R.string.account_feature_videos)

    override val accountCouponLimitGold: String
        @Composable get() = stringResource(R.string.account_coupon_limit_gold)

    override val accountCouponUnlimited: String
        @Composable get() = stringResource(R.string.account_coupon_unlimited)

    @Composable
    override fun accountDaysLeft(days: Int): String =
        pluralStringResource(R.plurals.account_days_left, days, days)

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

    override val homeForeignTitle: String
        @Composable get() = stringResource(R.string.home_foreign_title)

    override val homeForeignSubtitle: String
        @Composable get() = stringResource(R.string.home_foreign_subtitle)

    override val homeOpenForeign: String
        @Composable get() = stringResource(R.string.home_open_foreign)

    override val foreignTitle: String
        @Composable get() = stringResource(R.string.foreign_title)

    override val foreignSubtitle: String
        @Composable get() = stringResource(R.string.foreign_subtitle)

    override val foreignEmpty: String
        @Composable get() = stringResource(R.string.foreign_empty)

    override val foreignLoadFailed: String
        @Composable get() = stringResource(R.string.foreign_load_failed)

    override val foreignAgfNote: String
        @Composable get() = stringResource(R.string.foreign_agf_note)

    @Composable
    override fun foreignRaceTitle(number: String): String =
        stringResource(R.string.foreign_race_title, number)

    @Composable
    override fun foreignAiTop(horse: String): String =
        stringResource(R.string.foreign_ai_top, horse)

    @Composable
    override fun foreignAiSelection(numbers: String): String =
        stringResource(R.string.foreign_ai_selection, numbers)

    @Composable
    override fun foreignAiCouponTitle(number: Int): String =
        stringResource(R.string.foreign_ai_coupon_title, number)

    @Composable
    override fun foreignAiCouponStart(time: String): String =
        stringResource(R.string.foreign_ai_coupon_start, time)

    @Composable
    override fun foreignAiCouponTotal(combinations: Int, amount: String): String =
        stringResource(R.string.foreign_ai_coupon_total, combinations, amount)

    @Composable
    override fun foreignAiLeg(raceNumber: Int, numbers: String): String =
        stringResource(R.string.foreign_ai_leg, raceNumber, numbers)

    override val foreignBack: String
        @Composable get() = stringResource(R.string.foreign_back)

    override val raceTrainingTitle: String
        @Composable get() = stringResource(R.string.race_training_title)

    override val raceTrainingSubtitle: String
        @Composable get() = stringResource(R.string.race_training_subtitle)

    override val raceTrainingOpen: String
        @Composable get() = stringResource(R.string.race_training_open)

    override val raceTrainingClose: String
        @Composable get() = stringResource(R.string.race_training_close)

    override val raceTrainingEmpty: String
        @Composable get() = stringResource(R.string.race_training_empty)

    override val raceTrainingUnavailable: String
        @Composable get() = stringResource(R.string.race_training_unavailable)

    override val raceTrainingRetry: String
        @Composable get() = stringResource(R.string.race_training_retry)

    override val raceTrainingVideo: String
        @Composable get() = stringResource(R.string.race_training_video)

    @Composable
    override fun raceTrainingJockey(name: String): String =
        stringResource(R.string.race_training_jockey, name)

    override val raceFormTitle: String
        @Composable get() = stringResource(R.string.race_form_title)

    override val raceFormSubtitle: String
        @Composable get() = stringResource(R.string.race_form_subtitle)

    override val raceFormOpen: String
        @Composable get() = stringResource(R.string.race_form_open)

    override val raceFormClose: String
        @Composable get() = stringResource(R.string.race_form_close)

    override val raceFormEmpty: String
        @Composable get() = stringResource(R.string.race_form_empty)

    override val raceFormNoRuns: String
        @Composable get() = stringResource(R.string.race_form_no_runs)

    @Composable
    override fun raceFormOdds(odds: String): String =
        stringResource(R.string.race_form_odds, odds)

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

    @Composable
    override fun raceConsensusAvoid(sources: Int, avoid: Int, pct: Int): String =
        stringResource(R.string.race_consensus_avoid, sources, avoid, pct)

    @Composable
    override fun raceConsensusPositive(sources: Int, n: Int, category: String, pct: Int): String =
        stringResource(R.string.race_consensus_positive, sources, n, category, pct)

    @Composable
    override fun raceConsensusPositiveWithAvoid(sources: Int, n: Int, category: String, pct: Int, avoid: Int): String =
        stringResource(R.string.race_consensus_positive_with_avoid, sources, n, category, pct, avoid)

    @Composable
    override fun raceConsensusNoDirection(sources: Int): String =
        stringResource(R.string.race_consensus_no_direction, sources)

    override val raceCategoryBanko: String
        @Composable get() = stringResource(R.string.race_category_banko)

    override val raceCategoryFavorite: String
        @Composable get() = stringResource(R.string.race_category_favorite)

    override val raceCategoryStrong: String
        @Composable get() = stringResource(R.string.race_category_strong)

    override val raceCategoryStar: String
        @Composable get() = stringResource(R.string.race_category_star)

    override val raceCategorySurprise: String
        @Composable get() = stringResource(R.string.race_category_surprise)

    override val raceCategoryRival: String
        @Composable get() = stringResource(R.string.race_category_rival)

    override val raceValueModelTitle: String
        @Composable get() = stringResource(R.string.race_value_model_title)

    override val raceValueUnderrated: String
        @Composable get() = stringResource(R.string.race_value_underrated)

    override val raceValueOverrated: String
        @Composable get() = stringResource(R.string.race_value_overrated)

    @Composable
    override fun raceValueVsAgf(market: String, model: String): String =
        stringResource(R.string.race_value_vs_agf, market, model)

    @Composable
    override fun raceValueVsGanyan(market: String, model: String): String =
        stringResource(R.string.race_value_vs_ganyan, market, model)

    override val raceValueNote: String
        @Composable get() = stringResource(R.string.race_value_note)

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
            else -> stringResource(R.string.account_tier_free)
        }

    override val couponHistoryButton: String
        @Composable get() = stringResource(R.string.coupon_history_button)

    override val couponHistoryTitle: String
        @Composable get() = stringResource(R.string.coupon_history_title)

    override val couponHistorySubtitle: String
        @Composable get() = stringResource(R.string.coupon_history_subtitle)

    override val couponHistoryEmpty: String
        @Composable get() = stringResource(R.string.coupon_history_empty)

    override val couponHistoryPending: String
        @Composable get() = stringResource(R.string.coupon_history_pending)

    override val couponHistoryAllHit: String
        @Composable get() = stringResource(R.string.coupon_history_all_hit)

    override val couponHistoryBudget: String
        @Composable get() = stringResource(R.string.coupon_history_budget)

    override val myCouponsButton: String
        @Composable get() = stringResource(R.string.my_coupons_button)

    override val myCouponsTitle: String
        @Composable get() = stringResource(R.string.my_coupons_title)

    override val myCouponsSubtitle: String
        @Composable get() = stringResource(R.string.my_coupons_subtitle)

    override val myCouponsEmpty: String
        @Composable get() = stringResource(R.string.my_coupons_empty)

    override val myCouponsSave: String
        @Composable get() = stringResource(R.string.my_coupons_save)

    override val myCouponsSaved: String
        @Composable get() = stringResource(R.string.my_coupons_saved)

    override val myCouponsSaveFailed: String
        @Composable get() = stringResource(R.string.my_coupons_save_failed)

    override val myCouponsDelete: String
        @Composable get() = stringResource(R.string.my_coupons_delete)

    override val myCouponsLoadFailed: String
        @Composable get() = stringResource(R.string.my_coupons_load_failed)

    @Composable
    override fun myCouponsWinner(horseNumber: Int): String =
        stringResource(R.string.my_coupons_winner, horseNumber)

    @Composable
    override fun couponHistoryLegsHit(hit: Int, total: Int): String =
        stringResource(R.string.coupon_history_legs_hit, hit, total)

    @Composable
    override fun couponHistoryCost(totalTl: String, combinations: Int): String =
        stringResource(R.string.coupon_history_cost, totalTl, combinations)

    @Composable
    override fun couponHistoryLeg(leg: Int, raceNumber: Int, horses: String): String =
        stringResource(R.string.coupon_history_leg, leg, raceNumber, horses)

    override val couponErrorDailyLimit: String
        @Composable get() = stringResource(R.string.coupon_error_daily_limit)

    override val accountFeatureValueModel: String
        @Composable get() = stringResource(R.string.account_feature_value_model)

    override val accountFeatureCouponHistory: String
        @Composable get() = stringResource(R.string.account_feature_coupon_history)

    override val accountFeatureDailyCoupons: String
        @Composable get() = stringResource(R.string.account_feature_daily_coupons)

    override val accountDailyCouponsGold: String
        @Composable get() = stringResource(R.string.account_daily_coupons_gold)

    override val accountPeriodMonthly: String
        @Composable get() = stringResource(R.string.account_period_monthly)

    override val accountPeriodYearly: String
        @Composable get() = stringResource(R.string.account_period_yearly)

    override val accountYearlySaving: String
        @Composable get() = stringResource(R.string.account_yearly_saving)

    override val accountPerYear: String
        @Composable get() = stringResource(R.string.account_per_year)

    @Composable
    override fun couponHistoryWindow(pool: String, windowNumber: Int): String =
        if (pool == "fivefold")
            stringResource(R.string.coupon_history_window_fivefold, windowNumber)
        else
            stringResource(R.string.coupon_history_window_sixfold, windowNumber)

    override val accountFeatureAskAi: String
        @Composable get() = stringResource(R.string.account_feature_ask_ai)

    override val accountAskAiPremium: String
        @Composable get() = stringResource(R.string.account_ask_ai_premium)

    override val askAiTitle: String
        @Composable get() = stringResource(R.string.ask_ai_title)

    override val askAiSubtitle: String
        @Composable get() = stringResource(R.string.ask_ai_subtitle)

    override val askAiPlaceholder: String
        @Composable get() = stringResource(R.string.ask_ai_placeholder)

    override val askAiSend: String
        @Composable get() = stringResource(R.string.ask_ai_send)

    override val askAiSuggestionFavorite: String
        @Composable get() = stringResource(R.string.ask_ai_suggestion_favorite)

    override val askAiSuggestionUpset: String
        @Composable get() = stringResource(R.string.ask_ai_suggestion_upset)

    override val askAiSuggestionAgf: String
        @Composable get() = stringResource(R.string.ask_ai_suggestion_agf)

    override val askAiDisclaimer: String
        @Composable get() = stringResource(R.string.ask_ai_disclaimer)

    override val askAiYou: String
        @Composable get() = stringResource(R.string.ask_ai_you)

    override val askAiLockedTitle: String
        @Composable get() = stringResource(R.string.ask_ai_locked_title)

    override val askAiLockedBody: String
        @Composable get() = stringResource(R.string.ask_ai_locked_body)

    override val askAiUpgrade: String
        @Composable get() = stringResource(R.string.ask_ai_upgrade)

    override val askAiErrorBusy: String
        @Composable get() = stringResource(R.string.ask_ai_error_busy)

    override val askAiErrorFailed: String
        @Composable get() = stringResource(R.string.ask_ai_error_failed)

    override val askAiErrorInvalid: String
        @Composable get() = stringResource(R.string.ask_ai_error_invalid)

    override val askAiErrorRaceNotFound: String
        @Composable get() = stringResource(R.string.ask_ai_error_race_not_found)

    override val askAiErrorUpgrade: String
        @Composable get() = stringResource(R.string.ask_ai_error_upgrade)

    @Composable
    override fun askAiRemaining(remaining: Int, limit: Int): String =
        stringResource(R.string.ask_ai_remaining, remaining, limit)

    @Composable
    override fun askAiErrorDailyLimit(limit: Int): String =
        stringResource(R.string.ask_ai_error_daily_limit, limit)
}

package com.twohorse.app.i18n

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat

enum class Language(
    val code: String
) {
    TR("tr"),
    EN("en")
}

private const val PREFS_NAME = "two_horse_prefs"
private const val KEY_LOCALE_INITIALIZED = "locale_initialized"

val LocalStrings =
    staticCompositionLocalOf<Strings> {
        ResourceStrings
    }

/*
 * Reads the app's current per-app language via AndroidX's standard
 * per-app language API (AppCompatDelegate), the same mechanism
 * backing the OS-level "App languages" settings screen.
 */
fun currentLanguage(): Language {
    val tag =
        AppCompatDelegate.getApplicationLocales()
            .toLanguageTags()

    return if (tag.startsWith("en"))
        Language.EN
    else
        Language.TR
}

/*
 * Switches the app's per-app language. AppCompatDelegate persists
 * the choice itself and recreates the current Activity to apply it.
 */
fun setLanguage(
    language: Language
) {
    AppCompatDelegate.setApplicationLocales(
        LocaleListCompat.forLanguageTags(
            language.code
        )
    )
}

/*
 * Wraps the whole app: on the very first launch ever (independent
 * of the device's system locale) the app is pinned to Turkish via
 * AppCompatDelegate; afterwards whatever the user picks via the
 * language toggle persists using AppCompatDelegate's own storage.
 */
@Composable
fun ProvideLanguage(
    content: @Composable () -> Unit
) {
    val context =
        LocalContext.current

    LaunchedEffect(Unit) {
        val prefs =
            context.applicationContext
                .getSharedPreferences(
                    PREFS_NAME,
                    Context.MODE_PRIVATE
                )

        if (!prefs.getBoolean(KEY_LOCALE_INITIALIZED, false)) {
            prefs.edit()
                .putBoolean(KEY_LOCALE_INITIALIZED, true)
                .apply()

            setLanguage(Language.TR)
        }
    }

    CompositionLocalProvider(
        LocalStrings provides ResourceStrings
    ) {
        content()
    }
}

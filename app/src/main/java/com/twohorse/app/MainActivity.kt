package com.twohorse.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.twohorse.app.i18n.ProvideLanguage
import com.twohorse.app.ui.theme.TwoHorseTheme

/*
 * AppCompatActivity (not plain ComponentActivity) so the in-app
 * language choice made through AppCompatDelegate.setApplicationLocales
 * is applied on every Android version; on Android 12 and older only
 * AppCompat activities pick it up.
 */
class MainActivity :
    AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        setContent {
            TwoHorseTheme {
                ProvideLanguage {
                    TwoHorseApp()
                }
            }
        }
    }
}

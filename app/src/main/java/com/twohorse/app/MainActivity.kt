package com.twohorse.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.twohorse.app.i18n.ProvideLanguage
import com.twohorse.app.ui.theme.TwoHorseTheme

class MainActivity :
    ComponentActivity() {

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

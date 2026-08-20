package com.twohorse.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.domain.model.TodayData

@Composable
fun HomeScreen() {
    val repository =
        remember {
            TwoHorseRepository()
        }

    var loading by
        remember {
            mutableStateOf(true)
        }

    var data by
        remember {
            mutableStateOf<TodayData?>(
                null
            )
        }

    var error by
        remember {
            mutableStateOf<String?>(
                null
            )
        }

    LaunchedEffect(Unit) {
        repository
            .today()
            .onSuccess {
                data = it
            }
            .onFailure {
                error =
                    it.message
            }

        loading = false
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {
        when {
            loading ->
                CircularProgressIndicator()

            error != null ->
                Text(
                    text =
                        "Backend hatası: $error"
                )

            else ->
                Text(
                    text =
                        "Two Horse · " +
                        "${data?.meetings?.size ?: 0} şehir"
                )
        }
    }
}

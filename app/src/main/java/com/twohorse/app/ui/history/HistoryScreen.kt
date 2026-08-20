package com.twohorse.app.ui.history

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.domain.model.HistoryRace
import com.twohorse.app.ui.theme.Border
import com.twohorse.app.ui.theme.Green
import com.twohorse.app.ui.theme.Ink
import com.twohorse.app.ui.theme.Muted
import com.twohorse.app.ui.theme.PaleGreen
import com.twohorse.app.ui.theme.PaleRed
import com.twohorse.app.ui.theme.Red
import com.twohorse.app.ui.theme.Surface

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onRaceClick: (HistoryRace) -> Unit
) {
    BackHandler(
        onBack = onBack
    )

    val repository =
        remember {
            TwoHorseRepository()
        }

    var loading by
        remember {
            mutableStateOf(true)
        }

    var history by
        remember {
            mutableStateOf<List<HistoryRace>>(
                emptyList()
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
            .history()
            .onSuccess {
                history = it
            }
            .onFailure {
                error =
                    it.message
                        ?: "Geçmiş alınamadı"
            }

        loading = false
    }

    LazyColumn(
        modifier =
            Modifier.fillMaxSize(),
        verticalArrangement =
            Arrangement.spacedBy(
                10.dp
            )
    ) {
        item {
            HistoryHeader(
                onBack = onBack
            )
        }

        when {
            loading -> {
                item {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 70.dp
                                ),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Green
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    12.dp
                                )
                        )

                        Text(
                            text =
                                "Geçmiş yarışlar yükleniyor",
                            color =
                                Muted,
                            fontSize =
                                12.sp
                        )
                    }
                }
            }

            error != null -> {
                item {
                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 18.dp
                                ),
                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    PaleRed
                            ),
                        shape =
                            RoundedCornerShape(
                                16.dp
                            )
                    ) {
                        Text(
                            text =
                                error
                                    ?: "Geçmiş alınamadı",
                            modifier =
                                Modifier.padding(
                                    14.dp
                                ),
                            color =
                                Red,
                            fontSize =
                                12.sp,
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }
            }

            history.isEmpty() -> {
                item {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 70.dp
                                ),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.History,
                            contentDescription =
                                null,
                            tint =
                                Muted
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    8.dp
                                )
                        )

                        Text(
                            text =
                                "Henüz tamamlanmış yarış yok",
                            color =
                                Ink,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "Başlayan yarışların snapshotları burada görünür.",
                            color =
                                Muted,
                            fontSize =
                                11.sp
                        )
                    }
                }
            }

            else -> {
                item {
                    Column(
                        modifier =
                            Modifier.padding(
                                horizontal = 18.dp
                            )
                    ) {
                        Text(
                            text =
                                "Geçmiş",
                            color =
                                Ink,
                            fontSize =
                                19.sp,
                            fontWeight =
                                FontWeight.ExtraBold
                        )

                        Text(
                            text =
                                "${history.size} dondurulmuş yarış snapshotı",
                            color =
                                Muted,
                            fontSize =
                                11.sp
                        )
                    }
                }

                items(
                    items =
                        history,
                    key = {
                        "${it.raceDate}-${it.city}-${it.raceNumber}"
                    }
                ) { race ->
                    Column(
                        modifier =
                            Modifier.padding(
                                horizontal = 18.dp
                            )
                    ) {
                        HistoryRaceCard(
                            race =
                                race,
                            onClick = {
                                onRaceClick(
                                    race
                                )
                            }
                        )
                    }
                }
            }
        }

        item {
            Spacer(
                modifier =
                    Modifier.height(
                        28.dp
                    )
            )
        }
    }
}

@Composable
private fun HistoryHeader(
    onBack: () -> Unit
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp,
                    vertical = 8.dp
                ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack
        ) {
            Icon(
                imageVector =
                    Icons.Default.ArrowBack,
                contentDescription =
                    "Geri",
                tint =
                    Ink
            )
        }

        Text(
            text =
                "Geçmiş Yarışlar",
            color =
                Ink,
            fontSize =
                19.sp,
            fontWeight =
                FontWeight.ExtraBold
        )
    }
}

@Composable
private fun HistoryRaceCard(
    race: HistoryRace,
    onClick: () -> Unit
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Surface
            ),
        shape =
            RoundedCornerShape(
                18.dp
            ),
        border =
            BorderStroke(
                1.dp,
                Border
            )
    ) {
        Row(
            modifier =
                Modifier.padding(
                    15.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Surface(
                color =
                    PaleGreen,
                shape =
                    RoundedCornerShape(
                        13.dp
                    )
            ) {
                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 11.dp,
                            vertical = 9.dp
                        ),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {
                    Text(
                        text =
                            race.startTime
                                .ifBlank {
                                    "--:--"
                                },
                        color =
                            Green,
                        fontSize =
                            13.sp,
                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Text(
                        text =
                            "${race.raceNumber}. K",
                        color =
                            Green,
                        fontSize =
                            9.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(
                            start = 12.dp
                        )
            ) {
                Text(
                    text =
                        "${race.city} · ${race.raceDate}",
                    color =
                        Ink,
                    fontSize =
                        15.sp,
                    fontWeight =
                        FontWeight.ExtraBold,
                    maxLines =
                        1,
                    overflow =
                        TextOverflow.Ellipsis
                )

                Text(
                    text =
                        listOfNotNull(
                            race.distanceMeters
                                ?.let {
                                    "$it m"
                                },
                            race.track
                                .takeIf {
                                    it.isNotBlank()
                                }
                        )
                            .joinToString(
                                " · "
                            )
                            .ifBlank {
                                race.raceDate
                            },
                    color =
                        Muted,
                    fontSize =
                        11.sp
                )

                Text(
                    text =
                        "${race.runners.size} at · " +
                        "${race.expertPredictionCount} uzman satırı",
                    color =
                        Muted,
                    fontSize =
                        10.sp
                )
            }

            Icon(
                imageVector =
                    Icons.Default.KeyboardArrowRight,
                contentDescription =
                    null,
                tint =
                    Muted
            )
        }
    }
}

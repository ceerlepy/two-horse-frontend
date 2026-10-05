package com.twohorse.app.ui.race

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.data.api.ApiException
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.i18n.LocalStrings
import com.twohorse.app.i18n.currentLanguage
import com.twohorse.app.ui.theme.*
import kotlinx.coroutines.launch

/* Must match the server's ASK_AI_CONFIG.maxQuestionChars. */
private const val MAX_QUESTION_CHARS = 300

private data class AskExchange(
    val question: String,
    val answer: String
)

private sealed interface AskError {
    data object Upgrade : AskError
    data class DailyLimit(val limit: Int) : AskError
    data object Busy : AskError
    data object Invalid : AskError
    data object RaceNotFound : AskError
    data object Failed : AskError
}

private fun askError(error: Throwable): AskError =
    when ((error as? ApiException)?.apiCode) {
        "TIER_UPGRADE_REQUIRED" -> AskError.Upgrade
        "DAILY_LIMIT_REACHED" -> AskError.DailyLimit(20)
        "ASK_BUSY" -> AskError.Busy
        "INVALID_QUESTION" -> AskError.Invalid
        "RACE_NOT_FOUND" -> AskError.RaceNotFound
        else -> AskError.Failed
    }

/*
 * "AI'ya sor": a Premium member asks a free-text question about this
 * race; the server answers from our own data for it. Other plans see
 * a locked card that leads to the membership screen.
 */
@Composable
fun AskAiSection(
    city: String,
    raceNumber: Int,
    isPremium: Boolean,
    repository: TwoHorseRepository,
    onUpgradeClick: () -> Unit
) {
    val strings = LocalStrings.current

    Card(
        colors =
            CardDefaults.cardColors(
                containerColor = Surface
            ),
        border =
            BorderStroke(
                1.dp,
                Border
            ),
        shape =
            RoundedCornerShape(18.dp)
    ) {
        Column(
            Modifier.padding(15.dp),
            verticalArrangement =
                Arrangement.spacedBy(9.dp)
        ) {
            if (!isPremium) {
                Text(
                    text = strings.askAiLockedTitle,
                    color = Ink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = strings.askAiLockedBody,
                    color = Muted,
                    fontSize = 12.sp
                )

                OutlinedButton(
                    onClick = onUpgradeClick,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = strings.askAiUpgrade,
                        color = Green,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                AskAiChat(
                    city = city,
                    raceNumber = raceNumber,
                    repository = repository
                )
            }
        }
    }
}

@Composable
private fun AskAiChat(
    city: String,
    raceNumber: Int,
    repository: TwoHorseRepository
) {
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()

    var question by remember(city, raceNumber) { mutableStateOf("") }
    var loading by remember(city, raceNumber) { mutableStateOf(false) }
    var error by remember(city, raceNumber) { mutableStateOf<AskError?>(null) }
    var allowance by remember(city, raceNumber) { mutableStateOf<Pair<Int, Int>?>(null) }
    val exchanges = remember(city, raceNumber) { mutableStateListOf<AskExchange>() }

    fun send(text: String) {
        val trimmed = text.trim()

        if (loading || trimmed.length < 3) {
            return
        }

        loading = true
        error = null

        scope.launch {
            repository
                .ask(city, raceNumber, trimmed, currentLanguage().code)
                .onSuccess {
                    exchanges.add(AskExchange(trimmed, it.answer))
                    allowance = it.used to it.limit
                    question = ""
                }
                .onFailure {
                    error = askError(it)
                }

            loading = false
        }
    }

    Text(
        text = strings.askAiTitle,
        color = Ink,
        fontSize = 14.sp,
        fontWeight = FontWeight.Black
    )

    Text(
        text = strings.askAiSubtitle,
        color = Muted,
        fontSize = 12.sp
    )

    exchanges.forEach { exchange ->
        Column(
            verticalArrangement =
                Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${strings.askAiYou}: ${exchange.question}",
                color = Ink,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = exchange.answer,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            PaleGreen,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(10.dp),
                color = Ink,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }

    if (exchanges.isEmpty()) {
        Row(
            modifier =
                Modifier.horizontalScroll(
                    rememberScrollState()
                ),
            horizontalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                strings.askAiSuggestionFavorite,
                strings.askAiSuggestionUpset,
                strings.askAiSuggestionAgf
            ).forEach { suggestion ->
                AssistChip(
                    onClick = {
                        question = suggestion
                        send(suggestion)
                    },
                    enabled = !loading,
                    label = {
                        Text(
                            text = suggestion,
                            fontSize = 11.sp
                        )
                    }
                )
            }
        }
    }

    Row(
        verticalAlignment =
            Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = question,
            onValueChange = {
                question = it.take(MAX_QUESTION_CHARS)
            },
            modifier = Modifier.weight(1f),
            enabled = !loading,
            placeholder = {
                Text(
                    text = strings.askAiPlaceholder,
                    fontSize = 13.sp
                )
            },
            textStyle =
                LocalTextStyle.current.copy(
                    fontSize = 13.sp
                ),
            maxLines = 3,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions =
                KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
            keyboardActions =
                KeyboardActions(
                    onSend = {
                        send(question)
                    }
                )
        )

        Button(
            onClick = {
                send(question)
            },
            enabled =
                !loading &&
                question.trim().length >= 3,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Green
                ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Surface,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = strings.askAiSend,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }

    error?.let {
        Text(
            text =
                when (it) {
                    AskError.Upgrade -> strings.askAiErrorUpgrade
                    is AskError.DailyLimit -> strings.askAiErrorDailyLimit(allowance?.second ?: it.limit)
                    AskError.Busy -> strings.askAiErrorBusy
                    AskError.Invalid -> strings.askAiErrorInvalid
                    AskError.RaceNotFound -> strings.askAiErrorRaceNotFound
                    AskError.Failed -> strings.askAiErrorFailed
                },
            color = Red,
            fontSize = 12.sp
        )
    }

    allowance?.let { (used, limit) ->
        Text(
            text = strings.askAiRemaining((limit - used).coerceAtLeast(0), limit),
            color = Muted,
            fontSize = 11.sp
        )
    }

    Text(
        text = strings.askAiDisclaimer,
        color = Muted,
        fontSize = 10.sp
    )
}

@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.twohorse.app.ui.race

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.data.api.ApiException
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.domain.model.Race
import com.twohorse.app.i18n.LocalStrings
import com.twohorse.app.i18n.currentLanguage
import com.twohorse.app.ui.theme.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

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
 * Chat history per race, kept on the phone so closing the chat (or the
 * app) doesn't lose it. Bounded so it can't grow: the last
 * MAX_EXCHANGES questions per race, the MAX_RACES most recently used
 * races, nothing older than MAX_AGE_MILLIS. At most ~10 x 20 short
 * answers, i.e. a few hundred KB in the worst case.
 */
private object AskAiHistory {
    private const val PREFS = "ask_ai_history"
    private const val KEY = "chats"
    private const val MAX_EXCHANGES = 20
    private const val MAX_RACES = 10
    private const val MAX_AGE_MILLIS = 3L * 24 * 60 * 60 * 1000

    fun raceKey(
        raceDate: String?,
        city: String,
        raceNumber: Int,
        foreign: Boolean
    ): String =
        "${raceDate.orEmpty()}|${if (foreign) "yd|" else ""}$city|$raceNumber"

    private fun read(context: Context): JSONObject =
        runCatching {
            JSONObject(
                context
                    .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .getString(KEY, null)
                    ?: "{}"
            )
        }.getOrElse { JSONObject() }

    fun load(context: Context, key: String): List<AskExchange> {
        val items =
            read(context)
                .optJSONObject(key)
                ?.optJSONArray("x")
                ?: return emptyList()

        return (0 until items.length()).mapNotNull { i ->
            items.optJSONArray(i)?.let {
                AskExchange(it.optString(0), it.optString(1))
            }
        }
    }

    fun save(context: Context, key: String, exchanges: List<AskExchange>) {
        val now = System.currentTimeMillis()
        val all = read(context)

        if (exchanges.isEmpty()) {
            all.remove(key)
        } else {
            all.put(
                key,
                JSONObject()
                    .put("t", now)
                    .put(
                        "x",
                        JSONArray().apply {
                            exchanges.takeLast(MAX_EXCHANGES).forEach {
                                put(JSONArray().put(it.question).put(it.answer))
                            }
                        }
                    )
            )
        }

        val kept =
            all.keys().asSequence().toList()
                .map { it to all.optJSONObject(it)?.optLong("t") }
                .filter { (_, t) -> t != null && now - t <= MAX_AGE_MILLIS }
                .sortedByDescending { it.second }
                .take(MAX_RACES)
                .map { it.first }
                .toSet()

        val trimmed = JSONObject()
        kept.forEach { trimmed.put(it, all.get(it)) }

        context
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, trimmed.toString())
            .apply()
    }
}

/* Round "AI'ya sor" button that sits at the bottom right of the race screen. */
@Composable
fun AskAiFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val strings = LocalStrings.current

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(60.dp),
        shape = CircleShape,
        containerColor = Green,
        contentColor = Color.White
    ) {
        Icon(
            Icons.AutoMirrored.Filled.Chat,
            contentDescription = strings.askAiOpen,
            modifier = Modifier.size(26.dp)
        )
    }
}

/*
 * "AI'ya sor" chat for one race, in a sheet over the race screen.
 * Premium members ask free-text questions answered from our own data;
 * other plans see what it is and a way to the membership screen.
 */
@Composable
fun AskAiSheet(
    race: Race,
    isPremium: Boolean,
    repository: TwoHorseRepository,
    onUpgradeClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AskAiSheet(
        city = race.city,
        raceNumber = race.number,
        raceDate = race.raceDate,
        isPremium = isPremium,
        repository = repository,
        onUpgradeClick = onUpgradeClick,
        onDismiss = onDismiss
    )
}

/*
 * The same sheet for a foreign meeting: only the race's identity and
 * the foreign flag differ, and the server answers from that card's own
 * numbers (see ask/service.ts).
 */
@Composable
fun AskAiSheet(
    city: String,
    raceNumber: Int,
    raceDate: String?,
    isPremium: Boolean,
    repository: TwoHorseRepository,
    onUpgradeClick: () -> Unit,
    onDismiss: () -> Unit,
    foreign: Boolean = false
) {
    val strings = LocalStrings.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState =
            rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
        containerColor = CardTone
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.92f)
                    .imePadding()
                    .padding(horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = strings.askAiTitle,
                        color = Ink,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = strings.raceCityAndNumber(city, raceNumber),
                        color = Muted,
                        fontSize = 12.sp
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = strings.askAiClose,
                        tint = Ink
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            ToneDivider()

            if (isPremium) {
                AskAiChat(
                    city = city,
                    raceNumber = raceNumber,
                    raceDate = raceDate,
                    foreign = foreign,
                    repository = repository
                )
            } else {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = strings.askAiLockedTitle,
                        color = Ink,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = strings.askAiLockedBody,
                        color = Muted,
                        fontSize = 13.sp
                    )

                    Button(
                        onClick = onUpgradeClick,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Green
                            ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = strings.askAiUpgrade,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.AskAiChat(
    city: String,
    raceNumber: Int,
    raceDate: String?,
    foreign: Boolean,
    repository: TwoHorseRepository
) {
    val strings = LocalStrings.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val key = AskAiHistory.raceKey(raceDate, city, raceNumber, foreign)

    var question by remember(key) { mutableStateOf("") }
    var loading by remember(key) { mutableStateOf(false) }
    var error by remember(key) { mutableStateOf<AskError?>(null) }
    var allowance by remember(key) { mutableStateOf<Pair<Int, Int>?>(null) }
    val exchanges =
        remember(key) {
            mutableStateListOf<AskExchange>().apply {
                addAll(AskAiHistory.load(context, key))
            }
        }
    val listState = rememberLazyListState()

    LaunchedEffect(exchanges.size, loading) {
        val last = exchanges.size + (if (loading) 1 else 0)
        if (last > 0) {
            listState.animateScrollToItem(last - 1)
        }
    }

    fun send(text: String) {
        val trimmed = text.trim()

        if (loading || trimmed.length < 3) {
            return
        }

        loading = true
        error = null
        question = ""

        scope.launch {
            repository
                .ask(city, raceNumber, trimmed, currentLanguage().code, foreign)
                .onSuccess {
                    exchanges.add(AskExchange(trimmed, it.answer))
                    AskAiHistory.save(context, key, exchanges)
                    allowance = it.used to it.limit
                }
                .onFailure {
                    error = askError(it)
                    question = trimmed
                }

            loading = false
        }
    }

    LazyColumn(
        state = listState,
        modifier =
            Modifier
                .weight(1f)
                .fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (exchanges.isEmpty() && !loading) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = strings.askAiSubtitle,
                        color = Muted,
                        fontSize = 13.sp
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            strings.askAiSuggestionFavorite,
                            strings.askAiSuggestionUpset,
                            strings.askAiSuggestionAgf
                        ).forEach { suggestion ->
                            SuggestionChip(
                                onClick = { send(suggestion) },
                                label = {
                                    Text(
                                        text = suggestion,
                                        fontSize = 12.sp
                                    )
                                },
                                colors =
                                    SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = InsetTone,
                                        labelColor = Ink
                                    ),
                                border = BorderStroke(1.dp, InsetToneBorder)
                            )
                        }
                    }
                }
            }
        }

        items(exchanges) { exchange ->
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ChatBubble(text = exchange.question, mine = true)
                ChatBubble(text = exchange.answer, mine = false)
            }
        }

        if (loading) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Green
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = strings.askAiThinking,
                        color = Muted,
                        fontSize = 12.sp
                    )
                }
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
            modifier = Modifier.padding(bottom = 6.dp),
            color = Red,
            fontSize = 12.sp
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
            maxLines = 4,
            shape = RoundedCornerShape(22.dp),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedBorderColor = Green,
                    unfocusedBorderColor = InsetToneBorder
                ),
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

        FilledIconButton(
            onClick = {
                send(question)
            },
            enabled =
                !loading &&
                question.trim().length >= 3,
            modifier = Modifier.size(48.dp),
            colors =
                IconButtonDefaults.filledIconButtonColors(
                    containerColor = Green,
                    contentColor = Color.White
                )
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = strings.askAiSend
            )
        }
    }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text =
                listOfNotNull(
                    allowance?.let { (used, limit) ->
                        strings.askAiRemaining((limit - used).coerceAtLeast(0), limit)
                    },
                    strings.askAiDisclaimer
                ).joinToString("\n"),
            modifier = Modifier.weight(1f),
            color = Muted,
            fontSize = 10.sp
        )

        if (exchanges.isNotEmpty() && !loading) {
            TextButton(
                onClick = {
                    exchanges.clear()
                    AskAiHistory.save(context, key, exchanges)
                }
            ) {
                Text(
                    text = strings.askAiClear,
                    color = Muted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(
    text: String,
    mine: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            if (mine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 300.dp),
            color = if (mine) Green else Color.White,
            border = if (mine) null else BorderStroke(1.dp, InsetToneBorder),
            shape =
                RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (mine) 16.dp else 4.dp,
                    bottomEnd = if (mine) 4.dp else 16.dp
                )
        ) {
            Text(
                text = text,
                modifier =
                    Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 9.dp
                    ),
                color = if (mine) Color.White else Ink,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

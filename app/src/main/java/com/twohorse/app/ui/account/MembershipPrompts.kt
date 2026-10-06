package com.twohorse.app.ui.account

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.twohorse.app.domain.model.MembershipUser
import com.twohorse.app.i18n.LocalStrings
import com.twohorse.app.i18n.currentLanguage
import com.twohorse.app.ui.theme.*
import java.time.Instant

private const val PREFS = "membership_prompts"

private fun isTrialActive(user: MembershipUser): Boolean =
    user.tierSource == "trial" && !user.isFree

/* The trial ran out and no paid plan replaced it. */
private fun isTrialEnded(user: MembershipUser): Boolean {
    if (user.tierSource != "trial" || !user.isFree) return false

    val end =
        user.trialEndsAt
            ?.let { runCatching { Instant.parse(it) }.getOrNull() }
            ?: return false

    return end.isBefore(Instant.now())
}

private fun Context.promptSeen(key: String): Boolean =
    getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getBoolean(key, false)

private fun Context.markPromptSeen(key: String) {
    getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(key, true)
        .apply()
}

/*
 * One-time membership prompts on the home screen: a welcome with the
 * plan table when the free Premium trial starts, and a plan choice
 * once it has ended. Each is shown once per account on this device.
 */
@Composable
fun MembershipPrompts(
    user: MembershipUser?,
    onOpenPlans: () -> Unit
) {
    val context = LocalContext.current

    val activeUser =
        user ?: return

    val welcomeKey = "welcome_${activeUser.id}"
    val endedKey = "trial_ended_${activeUser.id}"

    var dismissed by
        remember(activeUser.id) { mutableStateOf(false) }

    if (dismissed) return

    fun close(key: String, openPlans: Boolean) {
        context.markPromptSeen(key)
        dismissed = true
        if (openPlans) onOpenPlans()
    }

    when {
        isTrialActive(activeUser) && !context.promptSeen(welcomeKey) ->
            WelcomeDialog(
                user = activeUser,
                onStart = { close(welcomeKey, openPlans = false) },
                onPlans = { close(welcomeKey, openPlans = true) }
            )

        isTrialEnded(activeUser) && !context.promptSeen(endedKey) ->
            TrialEndedDialog(
                onCompare = { close(endedKey, openPlans = true) },
                onContinueFree = { close(endedKey, openPlans = false) }
            )
    }
}

/*
 * Dialog card whose body scrolls while the buttons stay pinned at the
 * bottom, kept clear of the status and navigation bars (the app draws
 * edge to edge, so a tall dialog otherwise slides under the nav bar).
 */
@Composable
private fun PromptShell(
    onDismiss: () -> Unit,
    buttons: @Composable ColumnScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier =
                Modifier
                    .systemBarsPadding()
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
            colors = CardDefaults.cardColors(containerColor = CardTone),
            border = BorderStroke(1.dp, CardToneBorder),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Column(
                    modifier =
                        Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                    content = content
                )

                Spacer(modifier = Modifier.height(14.dp))

                buttons()
            }
        }
    }
}

@Composable
private fun WelcomeDialog(
    user: MembershipUser,
    onStart: () -> Unit,
    onPlans: () -> Unit
) {
    val strings = LocalStrings.current

    val endDate =
        formatIsoDate(user.trialEndsAt, currentLanguage()) ?: ""

    PromptShell(
        onDismiss = onStart,
        buttons = {
            Button(
                onClick = onStart,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green)
            ) {
                Text(
                    text = strings.welcomeStart,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(
                onClick = onPlans,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = strings.welcomePlans,
                    color = Green,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) {
        Text(
            text = strings.welcomeTitle,
            color = Ink,
            fontSize = 19.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = strings.welcomeBody(endDate),
            color = Muted,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        PlanComparisonTable(currentTier = user.tier)
    }
}

@Composable
private fun TrialEndedDialog(
    onCompare: () -> Unit,
    onContinueFree: () -> Unit
) {
    val strings = LocalStrings.current

    PromptShell(
        onDismiss = onContinueFree,
        buttons = {
            Button(
                onClick = onCompare,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Ink)
            ) {
                Text(
                    text = strings.trialEndedCompare,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(
                onClick = onContinueFree,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = strings.trialEndedContinueFree,
                    color = Muted,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) {
        Text(
            text = strings.trialEndedTitle,
            color = Ink,
            fontSize = 19.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = strings.trialEndedBody,
            color = Muted,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        ShortPlan(
            title = strings.accountTierTitle("gold"),
            description = strings.accountGoldDescription,
            accent = Gold
        )

        Spacer(modifier = Modifier.height(10.dp))

        ShortPlan(
            title = strings.accountTierTitle("premium"),
            description = strings.accountPremiumDescription,
            accent = Green
        )
    }
}

@Composable
private fun ShortPlan(
    title: String,
    description: String,
    accent: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = InsetTone),
        border = BorderStroke(1.dp, InsetToneBorder),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                color = accent,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description,
                color = Ink,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

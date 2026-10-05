package com.twohorse.app.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.domain.model.Horse
import com.twohorse.app.domain.model.NextDayProgram
import com.twohorse.app.domain.model.Race
import com.twohorse.app.i18n.LocalStrings
import com.twohorse.app.i18n.Language
import com.twohorse.app.i18n.currentLanguage
import com.twohorse.app.ui.components.AnalyticsChip
import com.twohorse.app.ui.components.CityChip
import com.twohorse.app.ui.coupons.CouponCardHeader
import com.twohorse.app.ui.theme.Border
import com.twohorse.app.ui.theme.Green
import com.twohorse.app.ui.theme.Gold
import com.twohorse.app.ui.theme.Ink
import com.twohorse.app.ui.theme.Muted
import com.twohorse.app.ui.theme.PaleGreen
import com.twohorse.app.ui.theme.Surface
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/*
 * Tomorrow's TJK card, shown in place of the "day over" card once
 * every race of today has started and the backend has tomorrow's
 * program (`nextDay` on /api/today). Read-only: schedule and runners,
 * no scores or AGF -- those arrive on race-day morning. Early expert
 * picks show as a count pill ("N uzman") when the backend has them
 * (paid tiers only); never which sources.
 */

private val TurkeyZone: ZoneId = ZoneId.of("Europe/Istanbul")

private fun nextDayDateLabel(
    isoDate: String,
    localeCode: String
): String =
    runCatching {
        LocalDate
            .parse(isoDate)
            .format(
                DateTimeFormatter.ofPattern(
                    "d MMMM",
                    Locale(localeCode)
                )
            )
    }.getOrDefault(isoDate)

/* TJK post times are Turkey local; starts_at is a UTC instant. */
private fun nextDayRaceTime(
    race: Race
): String =
    raceTimeMillis(race)
        ?.let {
            Instant
                .ofEpochMilli(it)
                .atZone(TurkeyZone)
                .toLocalTime()
                .toString()
                .take(5)
        }
        ?: "--:--"

/*
 * Header (date + note), city chips and one card per race of the
 * selected city. `selectedCity` null or unknown means the first city.
 */
internal fun LazyListScope.nextDayProgramItems(
    program: NextDayProgram,
    selectedCity: String?,
    onCitySelected: (String) -> Unit
) {
    val meeting =
        program.meetings.firstOrNull { it.city == selectedCity }
            ?: program.meetings.firstOrNull()
            ?: return

    item(key = "next-day-header") {
        Column(
            modifier =
                Modifier.padding(horizontal = 18.dp)
        ) {
            NextDayHeader(
                program = program,
                selectedCity = meeting.city,
                onCitySelected = onCitySelected
            )
        }
    }

    items(
        items = meeting.races,
        key = { "next-day-${meeting.city}-${it.number}" }
    ) { race ->
        Column(
            modifier =
                Modifier.padding(horizontal = 18.dp)
        ) {
            NextDayRaceCard(race)
        }
    }
}

@Composable
internal fun NextDayHeader(
    program: NextDayProgram,
    selectedCity: String?,
    onCitySelected: (String) -> Unit
) {
    val strings = LocalStrings.current
    val language = currentLanguage()

    Column(
        verticalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {
        CouponCardHeader(
            title =
                strings.homeNextDayTitle(
                    nextDayDateLabel(
                        program.date,
                        language.code
                    )
                ),
            subtitle =
                strings.homeNextDayNote,
            modifier =
                Modifier.clip(
                    RoundedCornerShape(18.dp)
                ),
            trailing = {
                Icon(
                    imageVector = Icons.Default.NightsStay,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(26.dp)
                )
            }
        )

        if (program.meetings.size > 1) {
            LazyRow(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                items(
                    program.meetings.map { it.city }
                ) { city ->
                    CityChip(
                        city = city,
                        selected = city == selectedCity,
                        onClick = {
                            onCitySelected(city)
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun NextDayRaceCard(
    race: Race
) {
    val strings = LocalStrings.current

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = Surface
            ),
        border =
            BorderStroke(1.dp, Border),
        shape =
            RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier =
                Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 12.dp
                )
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = nextDayRaceTime(race),
                    color = Ink,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    modifier =
                        Modifier.padding(start = 10.dp),
                    text = strings.homeCourseNumber(race.number),
                    color = Green,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(start = 10.dp),
                    text =
                        listOf(race.distance, race.surface)
                            .filter { it.isNotBlank() }
                            .joinToString(" · "),
                    color = Muted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = strings.homeNextDayRunnerCount(race.horses.size),
                    color = Muted,
                    fontSize = 11.sp
                )
            }

            HorizontalDivider(
                modifier =
                    Modifier.padding(vertical = 8.dp),
                color = Border
            )

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {
                race.horses.forEach { horse ->
                    NextDayRunnerRow(horse)
                }
            }
        }
    }
}

@Composable
private fun NextDayRunnerRow(
    horse: Horse
) {
    val strings = LocalStrings.current

    val weight =
        horse.weight?.let {
            strings.homeNextDayRunnerWeight(
                if (it % 1.0 == 0.0)
                    it.toInt().toString()
                else
                    it.toString()
            )
        }

    val form =
        horse.recentForm
            .takeIf { it.isNotBlank() }
            ?.let { strings.homeNextDayRunnerForm(it) }

    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Text(
            modifier =
                Modifier
                    .width(26.dp)
                    .background(
                        PaleGreen,
                        RoundedCornerShape(7.dp)
                    )
                    .padding(vertical = 3.dp),
            text = horse.number.toString(),
            color = Green,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
        ) {
            Text(
                text = horse.name,
                color = Ink,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            val meta =
                listOfNotNull(
                    horse.jockey.takeIf { it.isNotBlank() },
                    weight,
                    form
                ).joinToString(" · ")

            if (meta.isNotEmpty()) {
                Text(
                    text = meta,
                    color = Muted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            /*
             * The backend's consensus sentence is Turkish (built from
             * counts only); English users get the count pill alone.
             */
            if (
                horse.expertPickCount > 0 &&
                horse.expertSummary.isNotBlank() &&
                currentLanguage() == Language.TR
            ) {
                Text(
                    text = horse.expertSummary,
                    color = Muted,
                    fontSize = 10.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (horse.expertPickCount > 0) {
            Spacer(
                modifier = Modifier.width(8.dp)
            )

            AnalyticsChip(
                text = strings.homeNextDayExpertCount(horse.expertPickCount),
                strong = true
            )
        }
    }
}

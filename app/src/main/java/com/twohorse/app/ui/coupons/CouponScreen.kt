package com.twohorse.app.ui.coupons

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.Config
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.domain.model.Coupon
import com.twohorse.app.domain.model.CouponLeg
import com.twohorse.app.domain.model.CouponResult
import com.twohorse.app.domain.model.MembershipUser
import com.twohorse.app.domain.model.Race
import com.twohorse.app.i18n.LocalStrings
import com.twohorse.app.ui.race.InfoButton
import com.twohorse.app.ui.theme.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.OffsetDateTime

private fun couponRaceStartMillis(
    race: Race
): Long? {
    val value =
        race.startsAt
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: return null

    return runCatching {
        Instant.parse(value).toEpochMilli()
    }.recoverCatching {
        OffsetDateTime.parse(value).toInstant().toEpochMilli()
    }.getOrNull()
}

@Composable
private fun poolLabel(pool: String): String {
    val strings = LocalStrings.current
    return if (pool == "fivefold") strings.poolLabelFivefold else strings.poolLabelSixfold
}

private const val GOLD_MAX_COUPON_BUDGET_TL = 1500.0

@Composable
fun CouponScreen(
    cities: List<String>,
    initialCity: String?,
    currentUser: MembershipUser?,
    /*
     * A foreign meeting: its races are not in /api/today, so the
     * already-started warning does not apply. TJK runs both altılı and
     * beşli there, like a domestic card.
     */
    foreign: Boolean = false,
    /* Tomorrow's cities, listed before their AGF exists; nothing to build yet. */
    pendingDateLabel: String? = null,
    onBack: () -> Unit,
    onUpgradeClick: () -> Unit = {},
    onOpenHistory: () -> Unit = {},
    onOpenMyCoupons: () -> Unit = {}
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val strings = LocalStrings.current

    val tier = currentUser?.tier ?: "free"
    val canGenerateCoupons = tier != "free"
    val maxBudgetTl =
        if (tier == "gold") GOLD_MAX_COUPON_BUDGET_TL else Double.MAX_VALUE

    val repository =
        remember {
            TwoHorseRepository(context)
        }

    var selectedCity by
        remember(cities, initialCity) {
            mutableStateOf(
                initialCity
                    ?.takeIf { it in cities }
                    ?: cities.firstOrNull()
                    .orEmpty()
            )
        }

    var sixfold by
        remember {
            mutableIntStateOf(1)
        }

    var pool by
        remember {
            mutableStateOf("sixfold")
        }

    var todayRaces by
        remember {
            mutableStateOf<List<Race>>(emptyList())
        }

    LaunchedEffect(selectedCity, foreign) {
        if (foreign) return@LaunchedEffect

        repository.today()
            .onSuccess { today ->
                todayRaces =
                    today.meetings
                        .firstOrNull {
                            it.city.equals(selectedCity, ignoreCase = true)
                        }
                        ?.races
                        ?: emptyList()
            }
    }

    var budget by
        remember {
            mutableStateOf(
                if (tier == "gold")
                    GOLD_MAX_COUPON_BUDGET_TL
                else
                    3000.0
            )
        }

    var loading by
        remember {
            mutableStateOf(false)
        }

    var result by
        remember {
            mutableStateOf<CouponResult?>(null)
        }

    var error by
        remember {
            mutableStateOf<CouponError?>(null)
        }

    var requestVersion by
        remember {
            mutableLongStateOf(0L)
        }

    var lastGeneratedCity by
        remember {
            mutableStateOf<String?>(null)
        }

    var lastGeneratedSixfold by
        remember {
            mutableStateOf<Int?>(null)
        }

    var lastGeneratedBudget by
        remember {
            mutableStateOf<Double?>(null)
        }

    var lastGeneratedPool by
        remember {
            mutableStateOf<String?>(null)
        }

    fun invalidateGeneration() {
        requestVersion++

        loading =
            false

        result =
            null

        error =
            null

        lastGeneratedCity =
            null

        lastGeneratedSixfold =
            null

        lastGeneratedBudget =
            null

        lastGeneratedPool =
            null
    }

    suspend fun generate() {
        if (
            loading
        ) {
            return
        }

        val requestCity =
            selectedCity

        val requestSixfold =
            sixfold

        val requestPool =
            pool

        val requestBudget =
            budget

        if (
            requestCity.isBlank()
        ) {
            error =
                CouponError.CitySelectFailed

            return
        }

        requestVersion++

        val myRequestVersion =
            requestVersion

        loading =
            true

        error =
            null

        val response =
            repository.coupons(
                city =
                    requestCity,
                budgetTl =
                    requestBudget,
                sixfold =
                    requestSixfold,
                multiplier =
                    1,
                pool =
                    requestPool,
                foreign =
                    foreign
            )

        if (
            requestVersion !=
            myRequestVersion
        ) {
            return
        }

        response
            .onSuccess {
                couponResult ->

                val cityMatches =
                    couponResult.city.equals(
                        requestCity,
                        ignoreCase =
                            true
                    )

                val sixfoldMatches =
                    couponResult.sixfold ==
                        requestSixfold

                val poolMatches =
                    couponResult.pool ==
                        requestPool

                val budgetMatches =
                    couponResult.budgetTl <=
                        requestBudget +
                        0.01

                val couponsWithinBudget =
                    couponResult.coupons.all {
                        coupon ->
                        coupon.totalTl >=
                            0.0 &&
                        coupon.totalTl <=
                            requestBudget +
                            0.01
                    }

                when {
                    !cityMatches ||
                    !sixfoldMatches ||
                    !poolMatches -> {
                        result =
                            null

                        error =
                            CouponError.UnexpectedWindow
                    }

                    !budgetMatches ||
                    !couponsWithinBudget -> {
                        result =
                            null

                        error =
                            CouponError.BudgetExceeded
                    }

                    else -> {
                        result =
                            couponResult

                        error =
                            null

                        lastGeneratedCity =
                            requestCity

                        lastGeneratedSixfold =
                            requestSixfold

                        lastGeneratedBudget =
                            requestBudget

                        lastGeneratedPool =
                            requestPool
                    }
                }
            }
            .onFailure {
                throwable ->

                if (
                    requestVersion ==
                    myRequestVersion
                ) {
                    result =
                        null

                    error =
                        couponErrorFromThrowable(
                            throwable
                        )
                }
            }

        if (
            requestVersion ==
            myRequestVersion
        ) {
            loading =
                false
        }
    }

    val scope = rememberCoroutineScope()

    val listState = rememberLazyListState()

    /* Freshly generated coupons scroll into view under the form. */
    LaunchedEffect(result) {
        if (result != null) {
            listState.animateScrollToItem(GENERATED_SUMMARY_INDEX)
        }
    }

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            if (pendingDateLabel == null) Surface(
                color = Bg
            ) {
                if (canGenerateCoupons) {
                    Button(
                        onClick = {
                            scope.launch {
                                generate()
                            }
                        },
                        enabled =
                            !loading &&
                            selectedCity.isNotBlank(),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 18.dp,
                                    vertical = 12.dp
                                )
                                .height(54.dp),
                        shape =
                            RoundedCornerShape(16.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Green
                            )
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(
                                text = strings.couponGenerateButton,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onUpgradeClick,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 18.dp,
                                    vertical = 12.dp
                                )
                                .height(54.dp),
                        shape =
                            RoundedCornerShape(16.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Gold
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = strings.couponUpgradeButton,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
        item {
            CouponHeader(
                onBack = onBack,
                historyUnlocked = tier == "premium",
                onOpenHistory = {
                    if (tier == "premium") onOpenHistory() else onUpgradeClick()
                },
                myCouponsUnlocked = canGenerateCoupons,
                onOpenMyCoupons = {
                    if (canGenerateCoupons) onOpenMyCoupons() else onUpgradeClick()
                }
            )
        }

        item {
            Column(
                modifier =
                    Modifier.padding(
                        horizontal = 18.dp
                    )
            ) {
                CouponIntro()

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                CouponBudgetLadderInfo()
            }
        }

        item {
            Column(
                modifier =
                    Modifier.padding(
                        horizontal = 18.dp
                    )
            ) {
                SectionTitle(
                    title = strings.couponCityTitle,
                    subtitle = strings.couponCitySubtitle
                )
            }
        }

        item {
            LazyRow(
                modifier =
                    Modifier.padding(
                        horizontal = 18.dp
                    ),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                items(cities) { city ->
                    SelectChip(
                        text = city,
                        selected =
                            selectedCity == city,
                        onClick = {
                            if (
                                selectedCity !=
                                city
                            ) {
                                invalidateGeneration()

                                selectedCity =
                                    city
                            }
                        }
                    )
                }
            }
        }

        if (pendingDateLabel != null) {
            item {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                    color = PaleGold,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = strings.couponNextDayNotice(pendingDateLabel),
                        modifier = Modifier.padding(14.dp),
                        color = Ink,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {

        item {
            Column(
                modifier =
                    Modifier.padding(
                        horizontal = 18.dp
                    )
            ) {
                SectionTitle(
                    title = strings.couponTypeTitle,
                    subtitle = strings.couponTypeSubtitle
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    SelectChip(
                        text = strings.poolLabelSixfold,
                        selected =
                            pool == "sixfold",
                        onClick = {
                            if (pool != "sixfold") {
                                invalidateGeneration()

                                pool = "sixfold"
                            }
                        }
                    )

                    SelectChip(
                        text = strings.poolLabelFivefold,
                        selected =
                            pool == "fivefold",
                        onClick = {
                            if (pool != "fivefold") {
                                invalidateGeneration()

                                pool = "fivefold"
                            }
                        }
                    )
                }
            }
        }

        item {
            Column(
                modifier =
                    Modifier.padding(
                        horizontal = 18.dp
                    )
            ) {
                SectionTitle(
                    title = poolLabel(pool),
                    subtitle = strings.couponWindowSubtitle(poolLabel(pool).lowercase())
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    SelectChip(
                        text = strings.couponWindowOrdinal(1, poolLabel(pool)),
                        selected =
                            sixfold == 1,
                        onClick = {
                            if (
                                sixfold != 1
                            ) {
                                invalidateGeneration()

                                sixfold =
                                    1
                            }
                        }
                    )

                    SelectChip(
                        text = strings.couponWindowOrdinal(2, poolLabel(pool)),
                        selected =
                            sixfold == 2,
                        onClick = {
                            if (
                                sixfold != 2
                            ) {
                                invalidateGeneration()

                                sixfold =
                                    2
                            }
                        }
                    )
                }
            }
        }

        item {
            Column(
                modifier =
                    Modifier.padding(
                        horizontal = 18.dp
                    )
            ) {
                SectionTitle(
                    title = strings.couponBudgetTitle,
                    subtitle = strings.couponBudgetSubtitle
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                LazyRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        listOf(
                            500.0,
                            750.0,
                            1500.0,
                            3000.0,
                            5000.0,
                            10000.0
                        ).filter { it <= maxBudgetTl }
                    ) { value ->
                        SelectChip(
                            text =
                                "${value.toInt()} TL",
                            selected =
                                budget == value,
                            onClick = {
                                if (
                                    budget !=
                                    value
                                ) {
                                    invalidateGeneration()

                                    budget =
                                        value
                                }
                            }
                        )
                    }
                }
            }
        }

        error?.let { couponError ->
            item {
                ErrorCard(
                    message = couponErrorText(couponError, strings)
                )
            }
        }

        if (
            result != null &&
            lastGeneratedCity != null &&
            lastGeneratedSixfold != null &&
            lastGeneratedBudget != null
        ) {
            item {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal =
                                    18.dp
                            ),
                    color =
                        PaleGreen,
                    shape =
                        RoundedCornerShape(
                            14.dp
                        )
                ) {
                    Text(
                        text =
                            strings.couponGeneratedSummary(
                                lastGeneratedCity ?: "",
                                lastGeneratedSixfold ?: 1,
                                poolLabel(lastGeneratedPool ?: "sixfold"),
                                lastGeneratedBudget?.toInt() ?: 0
                            ),
                        modifier =
                            Modifier.padding(
                                horizontal =
                                    12.dp,
                                vertical =
                                    10.dp
                            ),
                        color =
                            Green,
                        fontSize =
                            11.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }

        val windowStartMillis =
            result?.startRace?.let { raceNumber ->
                todayRaces
                    .firstOrNull { it.number == raceNumber }
                    ?.let(::couponRaceStartMillis)
            }

        val windowStarted =
            windowStartMillis != null &&
            System.currentTimeMillis() >= windowStartMillis

        result?.let { couponResult ->
            if (windowStarted) {
                item {
                    ErrorCard(
                        message =
                            strings.couponWindowStarted(
                                poolLabel(lastGeneratedPool ?: couponResult.pool)
                            )
                    )
                }
            } else {

            if (couponResult.coupons.isEmpty()) {
                item {
                    ErrorCard(
                        message = strings.couponNoneGenerated
                    )
                }
            }

            item {
                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp
                        )
                ) {
                    ResultSummary(
                        result = couponResult
                    )
                }
            }

            val visibleCoupons =
                couponResult.coupons
                    .filter {
                        coupon ->
                        coupon.totalTl >=
                            0.0 &&
                        coupon.totalTl <=
                            couponResult.budgetTl +
                            0.01
                    }

            itemsIndexed(
                visibleCoupons
            ) { index, coupon ->
                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp
                        )
                ) {
                    CouponCard(
                        coupon = coupon,
                        tierIndex = index + 1,
                        tierCount = visibleCoupons.size,
                        onSave =
                            if (canGenerateCoupons) {
                                { repository.saveMyCoupon(couponResult, coupon) }
                            } else {
                                null
                            }
                    )
                }
            }
            }
        }

            item {
                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
        }
        }
    }
}

/* Header, intro, city title, cities, pool, window, budget come first. */
private const val GENERATED_SUMMARY_INDEX = 7

@Composable
private fun CouponHeader(
    onBack: () -> Unit,
    historyUnlocked: Boolean,
    onOpenHistory: () -> Unit,
    myCouponsUnlocked: Boolean,
    onOpenMyCoupons: () -> Unit
) {
    val strings = LocalStrings.current

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
                    strings.back,
                tint = Ink
            )
        }

        Column {
            Text(
                text =
                    strings.couponHeaderTitle,
                color =
                    Ink,
                fontSize =
                    19.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )

            Text(
                text =
                    strings.couponHeaderSubtitle,
                color =
                    Muted,
                fontSize =
                    11.sp
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        TextButton(
            onClick = onOpenMyCoupons
        ) {
            Icon(
                imageVector =
                    if (myCouponsUnlocked) Icons.Default.ConfirmationNumber
                    else Icons.Default.Lock,
                contentDescription = null,
                tint = if (myCouponsUnlocked) Green else Muted,
                modifier = Modifier.size(16.dp)
            )

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Text(
                text = strings.myCouponsButton,
                color = if (myCouponsUnlocked) Green else Muted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (Config.SHOW_COUPON_HISTORY) {
            TextButton(
                onClick = onOpenHistory
            ) {
                Icon(
                    imageVector =
                        if (historyUnlocked) Icons.Default.History
                        else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (historyUnlocked) Green else Muted,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Text(
                    text = strings.couponHistoryButton,
                    color = if (historyUnlocked) Green else Muted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CouponIntro() {
    val strings = LocalStrings.current

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    PaleGold
            )
    ) {
        Row(
            modifier =
                Modifier.padding(17.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Surface(
                color = Gold,
                shape =
                    RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector =
                        Icons.Default.Stars,
                    contentDescription =
                        null,
                    tint =
                        Color.White,
                    modifier =
                        Modifier.padding(11.dp)
                )
            }

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(
                            start = 13.dp
                        )
            ) {
                Text(
                    text =
                        strings.couponIntroTitle,
                    color =
                        Ink,
                    fontWeight =
                        FontWeight.ExtraBold,
                    fontSize =
                        16.sp
                )

                Text(
                    text =
                        strings.couponIntroSubtitle,
                    color =
                        Muted,
                    fontSize =
                        12.sp
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String
) {
    Text(
        text = title,
        color = Ink,
        fontSize = 16.sp,
        fontWeight =
            FontWeight.ExtraBold
    )

    Text(
        text = subtitle,
        color = Muted,
        fontSize = 11.sp
    )
}

@Composable
internal fun SelectChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier =
            Modifier.clickable(
                onClick = onClick
            ),
        shape =
            RoundedCornerShape(50),
        color =
            if (selected)
                Green
            else
                Surface,
        border =
            BorderStroke(
                1.dp,
                if (selected)
                    Green
                else
                    Border
            )
    ) {
        Text(
            text = text,
            modifier =
                Modifier.padding(
                    horizontal = 15.dp,
                    vertical = 9.dp
                ),
            color =
                if (selected)
                    Color.White
                else
                    Ink,
            fontSize =
                12.sp,
            fontWeight =
                FontWeight.Bold
        )
    }
}

@Composable
private fun ErrorCard(
    message: String
) {
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
            RoundedCornerShape(16.dp)
    ) {
        Text(
            text = message,
            modifier =
                Modifier.padding(14.dp),
            color =
                Red,
            fontSize =
                12.sp,
            fontWeight =
                FontWeight.SemiBold
        )
    }
}

@Composable
private fun ResultSummary(
    result: CouponResult
) {
    val strings = LocalStrings.current

    Column {
        Text(
            text =
                "${result.city} · ${strings.couponWindowOrdinal(result.sixfold, poolLabel(result.pool))}",
            color =
                Ink,
            fontSize =
                18.sp,
            fontWeight =
                FontWeight.ExtraBold
        )

        Text(
            text =
                "${strings.couponRaceRange(result.startRace?.toString() ?: "?", result.endRace?.toString() ?: "?")} · " +
                strings.couponMaxBudgetAndCount(result.budgetTl.toInt(), result.coupons.size),
            color =
                Muted,
            fontSize =
                12.sp
        )

        if (
            result.unitPriceTl != null
        ) {
            Text(
                text =
                    strings.couponUnitPriceAndMultiplier(
                        "%.2f".format(result.unitPriceTl),
                        result.multiplier
                    ),
                color =
                    Muted,
                fontSize =
                    10.sp
            )
        }

        result.generatedAt?.let {
            Text(
                text =
                    strings.couponGeneratedAt(it),
                color =
                    Muted,
                fontSize =
                    9.sp
            )
        }
    }
}

private enum class SaveState { Idle, Saving, Saved, Failed }

@Composable
private fun CouponCard(
    coupon: Coupon,
    tierIndex: Int,
    tierCount: Int,
    onSave: (suspend () -> Result<Long>)?
) {
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()

    var saveState by
        remember(coupon) {
            mutableStateOf(SaveState.Idle)
        }

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Surface
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
    ) {
        CouponCardHeader(
            title =
                strings.couponAmountLabel(coupon.budgetTl.toInt()),
            subtitle =
                strings.couponTierLabel(tierIndex, tierCount),
            trailing = {
                CouponPill(
                    text = "${coupon.totalTl.toInt()} TL"
                )
            }
        )

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {
            Row(
                modifier =
                    Modifier.fillMaxWidth()
            ) {
                SmallMetric(
                    modifier =
                        Modifier.weight(1f),
                    title =
                        strings.couponMetricCombinations,
                    value =
                        coupon.combinations
                            .toString()
                )

                SmallMetric(
                    modifier =
                        Modifier.weight(1f),
                    title =
                        strings.couponMetricCoverage,
                    value =
                        probabilityText(
                            coupon.estimatedSurvivalProbability
                        ),
                    infoTitle =
                        strings.couponCoverageInfoTitle,
                    infoBody =
                        strings.couponCoverageInfo
                )
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            coupon.legs.forEachIndexed {
                index,
                leg ->

                CouponLegRow(
                    legIndex =
                        index + 1,
                    leg =
                        leg
                )

                if (
                    index <
                    coupon.legs.lastIndex
                ) {
                    HorizontalDivider(
                        modifier =
                            Modifier.padding(
                                vertical = 9.dp
                            ),
                        color =
                            Border
                    )
                }
            }

            if (onSave != null) {
                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                if (saveState == SaveState.Saved) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    PaleGreen,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(vertical = 11.dp),
                        horizontalArrangement =
                            Arrangement.Center,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkAdded,
                            contentDescription = null,
                            tint = Green,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text(
                            text = strings.myCouponsSaved,
                            color = Green,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            saveState = SaveState.Saving

                            scope.launch {
                                saveState =
                                    if (onSave().isSuccess) SaveState.Saved
                                    else SaveState.Failed
                            }
                        },
                        enabled =
                            saveState != SaveState.Saving,
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(12.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Green
                            )
                    ) {
                        if (saveState == SaveState.Saving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.BookmarkAdd,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text(
                                text = strings.myCouponsSave,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (saveState == SaveState.Failed) {
                        Text(
                            text = strings.myCouponsSaveFailed,
                            modifier = Modifier.padding(top = 6.dp),
                            color = Red,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CouponLegRow(
    legIndex: Int,
    leg: CouponLeg
) {
    val strings = LocalStrings.current

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.Top
    ) {
        Surface(
            modifier =
                Modifier.width(62.dp),
            color =
                PaleGreen,
            shape =
                RoundedCornerShape(11.dp)
        ) {
            Text(
                text =
                    strings.couponLegRace(leg.raceNumber),
                modifier =
                    Modifier.padding(
                        vertical = 8.dp
                    ),
                color =
                    Green,
                fontSize =
                    11.sp,
                fontWeight =
                    FontWeight.ExtraBold,
                textAlign =
                    TextAlign.Center,
                maxLines = 1
            )
        }

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(
                        start = 10.dp
                    ),
            verticalArrangement =
                Arrangement.spacedBy(5.dp)
        ) {
            // One line per horse: number in a green circle, then the name.
            leg.horses.forEach { horse ->
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(24.dp)
                                .background(
                                    Green,
                                    CircleShape
                                ),
                        contentAlignment =
                            Alignment.Center
                    ) {
                        Text(
                            text =
                                horse.horseNumber.toString(),
                            color =
                                Color.White,
                            fontSize =
                                11.sp,
                            fontWeight =
                                FontWeight.ExtraBold
                        )
                    }

                    Text(
                        text =
                            horse.horseName,
                        modifier =
                            Modifier.padding(
                                start = 8.dp
                            ),
                        color =
                            Ink,
                        fontSize =
                            12.sp,
                        fontWeight =
                            FontWeight.Bold,
                        maxLines = 1,
                        overflow =
                            TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text =
                    strings.couponLegCoverage(
                        probabilityText(
                            leg.coverageProbability
                        )
                    ),
                color =
                    Muted,
                fontSize =
                    10.sp
            )
        }
    }
}

@Composable
private fun SmallMetric(
    modifier: Modifier,
    title: String,
    value: String,
    infoTitle: String? = null,
    infoBody: String? = null
) {
    Surface(
        modifier =
            modifier.padding(
                end = 5.dp
            ),
        color =
            Color(0xFFF7F9F8),
        shape =
            RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier =
                Modifier.padding(9.dp)
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Muted,
                    fontSize = 9.sp
                )

                if (
                    infoTitle != null &&
                    infoBody != null
                ) {
                    InfoButton(
                        title = infoTitle,
                        body = infoBody
                    )
                }
            }

            Text(
                text = value,
                color = Ink,
                fontSize = 13.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )
        }
    }
}

private fun probabilityText(
    value: Double?
): String {
    if (value == null)
        return "—"

    val percent =
        if (value <= 1.0)
            value * 100.0
        else
            value

    return "%.1f%%"
        .format(percent)
}


@Composable
private fun CouponBudgetLadderInfo() {
    val strings = LocalStrings.current

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Surface
            ),
        border =
            BorderStroke(
                1.dp,
                Border
            ),
        shape =
            RoundedCornerShape(
                18.dp
            )
    ) {
        Column(
            modifier =
                Modifier.padding(
                    14.dp
                ),
            verticalArrangement =
                Arrangement.spacedBy(
                    9.dp
                )
        ) {
            Text(
                text =
                    strings.couponLadderTitle,
                color =
                    Ink,
                fontSize =
                    13.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )

            LadderLegendRow(
                symbol =
                    "1",
                title =
                    strings.couponLadderFixed1Title,
                description =
                    strings.couponLadderFixed1Desc,
                foreground =
                    Green,
                background =
                    PaleGreen
            )

            LadderLegendRow(
                symbol =
                    "2",
                title =
                    strings.couponLadderFixed2Title,
                description =
                    strings.couponLadderFixed2Desc,
                foreground =
                    Gold,
                background =
                    PaleGold
            )

            LadderLegendRow(
                symbol =
                    "3-6",
                title =
                    strings.couponLadderVariableTitle,
                description =
                    strings.couponLadderVariableDesc,
                foreground =
                    Red,
                background =
                    PaleRed
            )

            Text(
                text =
                    strings.couponLadderFooter,
                color =
                    Muted,
                fontSize =
                    9.sp
            )
        }
    }
}

@Composable
private fun LadderLegendRow(
    symbol: String,
    title: String,
    description: String,
    foreground: Color,
    background: Color
) {
    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Surface(
            color =
                background,
            shape =
                RoundedCornerShape(
                    11.dp
                )
        ) {
            Text(
                text =
                    symbol,
                modifier =
                    Modifier.padding(
                        horizontal =
                            10.dp,
                        vertical =
                            7.dp
                    ),
                color =
                    foreground,
                fontWeight =
                    FontWeight.ExtraBold
            )
        }

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(
                        start =
                            9.dp
                    )
        ) {
            Text(
                text =
                    title,
                color =
                    Ink,
                fontSize =
                    11.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )

            Text(
                text =
                    description,
                color =
                    Muted,
                fontSize =
                    9.sp
            )
        }
    }
}

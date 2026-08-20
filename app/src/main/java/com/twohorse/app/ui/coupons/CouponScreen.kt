package com.twohorse.app.ui.coupons

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twohorse.app.data.repository.TwoHorseRepository
import com.twohorse.app.domain.model.Coupon
import com.twohorse.app.domain.model.CouponLeg
import com.twohorse.app.domain.model.CouponResult
import com.twohorse.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun CouponScreen(
    cities: List<String>,
    initialCity: String?,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val repository =
        remember {
            TwoHorseRepository()
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

    var budget by
        remember {
            mutableStateOf(500.0)
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
            mutableStateOf<String?>(null)
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

        val requestBudget =
            budget

        if (
            requestCity.isBlank()
        ) {
            error =
                "Şehir seçilemedi."

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
                    1
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
                    !sixfoldMatches -> {
                        result =
                            null

                        error =
                            "Backend beklenmeyen kupon penceresi döndürdü."
                    }

                    !budgetMatches ||
                    !couponsWithinBudget -> {
                        result =
                            null

                        error =
                            "Backend bütçe sınırını aşan kupon döndürdü."
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
                        couponErrorMessage(
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

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            Surface(
                color = Bg
            ) {
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
                            text = "Kuponları Oluştur",
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
        item {
            CouponHeader(
                onBack = onBack
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
                    title = "Şehir",
                    subtitle = "Kupon oluşturulacak yarış programı"
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

        item {
            Column(
                modifier =
                    Modifier.padding(
                        horizontal = 18.dp
                    )
            ) {
                SectionTitle(
                    title = "Altılı",
                    subtitle = "Programdaki altılı penceresini seç"
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
                        text = "1. Altılı",
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
                        text = "2. Altılı",
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
                    title = "Bütçe",
                    subtitle = "Optimizer bu tavanı aşmadan seçim yapar"
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
                            100.0,
                            250.0,
                            500.0,
                            1000.0,
                            2000.0,
                            3000.0
                        )
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

        error?.let { message ->
            item {
                ErrorCard(
                    message = message
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
                            "${lastGeneratedCity} · ${lastGeneratedSixfold}. Altılı · ${lastGeneratedBudget?.toInt()} TL bütçe",
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

        result?.let { couponResult ->
            if (couponResult.coupons.isEmpty()) {
                item {
                    ErrorCard(
                        message =
                            "Bu seçim için uygun kupon üretilemedi. Bütçeyi artırmayı veya diğer altılıyı seçmeyi dene."
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

            items(
                couponResult.coupons
                    .filter {
                        coupon ->
                        coupon.totalTl >=
                            0.0 &&
                        coupon.totalTl <=
                            couponResult.budgetTl +
                            0.01
                    }
            ) { coupon ->
                Column(
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp
                        )
                ) {
                    CouponCard(
                        coupon = coupon
                    )
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

@Composable
private fun CouponHeader(
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
                tint = Ink
            )
        }

        Column {
            Text(
                text =
                    "Altılı Kupon",
                color =
                    Ink,
                fontSize =
                    19.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )

            Text(
                text =
                    "Two Horse optimizer",
                color =
                    Muted,
                fontSize =
                    11.sp
            )
        }
    }
}

@Composable
private fun CouponIntro() {
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
                        "Bütçeye göre optimize et",
                    color =
                        Ink,
                    fontWeight =
                        FontWeight.ExtraBold,
                    fontSize =
                        16.sp
                )

                Text(
                    text =
                        "Model olasılıklarını kullanarak altı ayağı birlikte optimize eder.",
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
private fun SelectChip(
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
    Column {
        Text(
            text =
                "${result.city} · ${result.sixfold}. Altılı",
            color =
                Ink,
            fontSize =
                18.sp,
            fontWeight =
                FontWeight.ExtraBold
        )

        Text(
            text =
                "${result.startRace ?: "?"}. koşu → " +
                "${result.endRace ?: "?"}. koşu · " +
                "Bütçe ${result.budgetTl.toInt()} TL",
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
                    "Birim fiyat ${"%.2f".format(result.unitPriceTl)} TL · " +
                    "Çarpan ${result.multiplier}",
                color =
                    Muted,
                fontSize =
                    10.sp
            )
        }

        result.generatedAt?.let {
            Text(
                text =
                    "Backend üretim zamanı: $it",
                color =
                    Muted,
                fontSize =
                    9.sp
            )
        }
    }
}

@Composable
private fun CouponCard(
    coupon: Coupon
) {
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
        border =
            BorderStroke(
                1.dp,
                Border
            )
    ) {
        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {
                    Text(
                        text =
                            profileTitle(
                                coupon.profile
                            ),
                        color =
                            Ink,
                        fontSize =
                            17.sp,
                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Text(
                        text =
                            coupon.profile,
                        color =
                            Muted,
                        fontSize =
                            10.sp
                    )
                }

                Surface(
                    color =
                        PaleGreen,
                    shape =
                        RoundedCornerShape(50)
                ) {
                    Text(
                        text =
                            "${coupon.totalTl.toInt()} TL",
                        modifier =
                            Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),
                        color =
                            Green,
                        fontWeight =
                            FontWeight.ExtraBold,
                        fontSize =
                            12.sp
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth()
            ) {
                SmallMetric(
                    modifier =
                        Modifier.weight(1f),
                    title =
                        "Kombinasyon",
                    value =
                        coupon.combinations
                            .toString()
                )

                SmallMetric(
                    modifier =
                        Modifier.weight(1f),
                    title =
                        "Kapsama",
                    value =
                        probabilityText(
                            coupon.estimatedSurvivalProbability
                        )
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
        }
    }
}

@Composable
private fun CouponLegRow(
    legIndex: Int,
    leg: CouponLeg
) {
    Row(
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.Top
    ) {
        Surface(
            color =
                PaleGreen,
            shape =
                RoundedCornerShape(11.dp)
        ) {
            Column(
                modifier =
                    Modifier.padding(
                        horizontal = 9.dp,
                        vertical = 7.dp
                    ),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {
                Text(
                    text =
                        "$legIndex.",
                    color =
                        Green,
                    fontSize =
                        10.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        "${leg.raceNumber}.K",
                    color =
                        Green,
                    fontSize =
                        11.sp,
                    fontWeight =
                        FontWeight.ExtraBold
                )
            }
        }

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(
                        start = 10.dp
                    )
        ) {
            Text(
                text =
                    leg.horses
                        .joinToString(
                            " · "
                        ) {
                            "${it.horseNumber} ${it.horseName}"
                        },
                color =
                    Ink,
                fontSize =
                    12.sp,
                fontWeight =
                    FontWeight.Bold,
                maxLines = 3,
                overflow =
                    TextOverflow.Ellipsis
            )

            Text(
                text =
                    "Ayak kapsama: " +
                    probabilityText(
                        leg.coverageProbability
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
    value: String
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
            Text(
                text = title,
                color = Muted,
                fontSize = 9.sp
            )

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

private fun profileTitle(
    profile: String
): String =
    when (
        profile.lowercase()
    ) {
        "cautious" ->
            "Temkinli"

        "balanced" ->
            "Dengeli"

        "maximum-coverage",
        "maximum_coverage",
        "max-coverage" ->
            "Maksimum Kapsama"

        else ->
            profile.ifBlank {
                "Optimize Kupon"
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


private fun visualCouponProfileTitle(
    profile: String
): String {
    val normalized =
        profile
            .trim()
            .lowercase()

    return when {
        normalized.contains("safe") ||
        normalized.contains("conservative") ||
        normalized.contains("guven") ->
            "Güvenli Kupon"

        normalized.contains("balanced") ||
        normalized.contains("denge") ->
            "Dengeli Kupon"

        normalized.contains("aggressive") ||
        normalized.contains("agres") ->
            "Agresif Kupon"

        else ->
            profile
    }
}

private fun visualCouponProfileDescription(
    profile: String
): String {
    val normalized =
        profile
            .trim()
            .lowercase()

    return when {
        normalized.contains("safe") ||
        normalized.contains("conservative") ||
        normalized.contains("guven") ->
            "Daha güçlü adaylara yoğunlaşır; kuponu kontrollü tutmayı hedefler."

        normalized.contains("balanced") ||
        normalized.contains("denge") ->
            "Güçlü adaylarla alternatifleri dengeler."

        normalized.contains("aggressive") ||
        normalized.contains("agres") ->
            "Daha geniş kapsama ve sürpriz senaryolarına alan açar."

        else ->
            "Optimizer tarafından oluşturulan kupon profili."
    }
}

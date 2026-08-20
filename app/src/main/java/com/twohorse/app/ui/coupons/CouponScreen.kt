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

    suspend fun generate() {
        if (selectedCity.isBlank()) {
            error = "Şehir seçilemedi."
            return
        }

        loading = true
        error = null

        repository
            .coupons(
                city = selectedCity,
                budgetTl = budget,
                sixfold = sixfold,
                multiplier = 1
            )
            .onSuccess {
                result = it
            }
            .onFailure {
                error =
                    it.message
                        ?: "Kupon oluşturulamadı."
            }

        loading = false
    }

    LazyColumn(
        modifier =
            Modifier.fillMaxSize(),
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
                            selectedCity = city
                            result = null
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
                            sixfold = 1
                            result = null
                        }
                    )

                    SelectChip(
                        text = "2. Altılı",
                        selected =
                            sixfold == 2,
                        onClick = {
                            sixfold = 2
                            result = null
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
                            2000.0
                        )
                    ) { value ->
                        SelectChip(
                            text =
                                "${value.toInt()} TL",
                            selected =
                                budget == value,
                            onClick = {
                                budget = value
                                result = null
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

        result?.let { couponResult ->
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
                modifier =
                    Modifier.height(30.dp)
            )
        }
    }

    val scope =
        rememberCoroutineScope()

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    bottom = 18.dp
                ),
        contentAlignment =
            Alignment.BottomCenter
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
                    modifier =
                        Modifier.size(22.dp),
                    color =
                        Color.White,
                    strokeWidth =
                        2.dp
                )
            } else {
                Icon(
                    imageVector =
                        Icons.Default.Stars,
                    contentDescription =
                        null
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(
                    text =
                        "Kuponları Oluştur",
                    fontWeight =
                        FontWeight.ExtraBold
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

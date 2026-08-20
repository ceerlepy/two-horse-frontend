package com.twohorse.app.ui.coupons

import com.twohorse.app.data.api.ApiException

fun couponErrorMessage(throwable: Throwable): String {
    val api = throwable as? ApiException

    return when (api?.apiCode) {
        "CITY_REQUIRED" ->
            "Şehir seçimi gerekli."

        "VALID_BUDGET_REQUIRED" ->
            "Geçerli bir bütçe seç."

        "SIXFOLD_WINDOW_NOT_FOUND",
        "SIXFOLD_NOT_FOUND",
        "NO_SIXFOLD_WINDOW" ->
            "Bu şehir için seçtiğin altılı henüz tanımlı değil."

        "NOT_ENOUGH_RACES",
        "INSUFFICIENT_RACES" ->
            "Bu altılıyı oluşturmak için yeterli yarış yok."

        "NO_RUNNERS",
        "NO_USABLE_RUNNERS" ->
            "Kupon oluşturmak için yeterli at verisi bulunamadı."

        else -> when {
            api?.statusCode == 404 ->
                "İstenen yarış veya altılı bulunamadı."

            api?.statusCode == 400 ->
                "Kupon isteği geçersiz. Şehir, altılı ve bütçeyi kontrol et."

            api?.statusCode != null && api.statusCode >= 500 ->
                "Backend şu anda kupon oluşturamıyor. Biraz sonra tekrar dene."

            throwable.message?.contains(
                "Unable to resolve host",
                ignoreCase = true
            ) == true ->
                "İnternet bağlantısı kurulamadı."

            throwable.message?.contains(
                "timeout",
                ignoreCase = true
            ) == true ->
                "İstek zaman aşımına uğradı. Tekrar dene."

            else ->
                "Kupon oluşturulamadı."
        }
    }
}

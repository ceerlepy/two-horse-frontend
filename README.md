# Two Horse Frontend

Native Android / Jetpack Compose frontend for Two Horse.

Android uygulaması thin-client mimarisindedir.

Scraping, TJK extraction, expert aggregation, prediction, learning, scoring ve coupon optimization Android içinde yapılmaz.

Bunların source of truth'u Two Horse Cloudflare Worker backend'idir.

## Architecture

Compose UI
  -> TwoHorseRepository
  -> TwoHorseApi
  -> Cloudflare Worker
      -> TJK canonical program
      -> expert aggregation
      -> AGF / market
      -> form
      -> field signals
      -> scoring
      -> learning
      -> sixfold optimizer
      -> D1 history

## Screens

### Home

Home ekranı bugünkü yarış programını gösterir.

Görevleri:

- today's meetings
- city filters
- nearest race
- countdown
- remaining races
- Altılı entry
- History entry

### Race Detail

Race Detail backend tarafından üretilmiş runner/model verilerini gösterir.

Gösterilen temel alanlar:

- model score
- confidence
- AGF
- HP
- weight
- recent form
- jockey

Frontend bu skorları hesaplamaz.

### Altılı Kupon

Endpoint:

GET /api/coupons/generate

Inputs:

- city
- budgetTl
- sixfold
- multiplier

Backend optimizer kupon sonucunu üretir.

Frontend yalnızca sonucu render eder.

### History

Endpoint:

GET /api/history

History frozen backend race snapshotlarından gelir.

Frontend geçmiş yarışları bugünkü modelle yeniden hesaplamaz.

## Public Android API

GET /api/today
GET /api/history
GET /api/coupons/generate

## Today Contract

Race alanları:

race_number
start_time
starts_at
distance_meters
track
runners

Runner alanları:

horse_number
horse_name
jockey
weight
hp
agf_percent
recent_form_raw
modelScore

Model score:

modelScore.score
modelScore.confidence

## Security

Android APK admin/debug endpoint kullanmaz.

ADMIN_TOKEN APK içine konulmaz.

Secret şu alanlara girmemelidir:

- Kotlin source
- Android resources
- BuildConfig
- repository
- APK
- CI artifact

## Clean Frontend Rule

Aşağıdaki backend sorumlulukları Android uygulamasına taşınmamalıdır:

Jsoup
TjkRepository
ExpertRepository
Predictor
scraping
model training
D1 writes

Backend source of truth'tur.

## Error Handling

HTTP hataları ApiException üzerinden normalize edilir.

ApiException:

- HTTP status code
- backend error code
- normalized message

taşır.

CouponErrors kullanıcıya backend'in ham hata mesajı yerine anlaşılır Türkçe mesaj gösterir.

404:
İstenen yarış veya altılı bulunamadı.

400:
Kupon parametreleri kontrol edilir.

5xx:
Backend geçici olarak kupon üretemiyor mesajı gösterilir.

Network:
İnternet bağlantısı mesajı gösterilir.

Timeout:
İstek zaman aşımı mesajı gösterilir.

## Coupon Layout

Altılı ekranı Scaffold kullanır.

Kupon üretme butonu Scaffold bottomBar içindedir.

Bu nedenle uzun kupon sonuçları butonun arkasında kalmaz.

## CI

Main push sonrasında:

1. API/static contract check
2. Android debug compile
3. APK artifact upload

Artifact:

two-horse-debug-apk

## Termux Workflow

git status
git diff --check
git add -A
git commit
git push origin main

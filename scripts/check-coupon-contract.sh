#!/usr/bin/env bash
set -e

API="app/src/main/java/com/twohorse/app/data/api/TwoHorseApi.kt"
UI="app/src/main/java/com/twohorse/app/ui/coupons/CouponScreen.kt"
APP="app/src/main/java/com/twohorse/app/TwoHorseApp.kt"

grep -q 'INVALID_COUPON_RESPONSE_WINDOW' "$API"
grep -q 'coupon.legs.size == 6' "$API"
grep -q 'requestVersion' "$UI"
grep -q 'myRequestVersion' "$UI"
grep -q 'couponErrorMessage' "$UI"
grep -q 'Backend üretim zamanı' "$UI"
grep -q 'returnRace' "$APP"
grep -q 'onOpenCoupons' \
  app/src/main/java/com/twohorse/app/ui/race/RaceDetailScreen.kt

echo "COUPON CONTRACT CHECK OK"

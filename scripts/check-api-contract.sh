#!/usr/bin/env bash
set -e

API_FILE="app/src/main/java/com/twohorse/app/data/api/TwoHorseApi.kt"
COUPON_FILE="app/src/main/java/com/twohorse/app/ui/coupons/CouponScreen.kt"

test -f "$API_FILE"
test -f "$COUPON_FILE"

grep -q 'modelScore' "$API_FILE"
grep -q 'distance_meters' "$API_FILE"
grep -q 'track' "$API_FILE"
grep -q 'agf_percent' "$API_FILE"
grep -q 'recent_form_raw' "$API_FILE"

grep -q '/api/today' "$API_FILE"
grep -q '/api/history' "$API_FILE"
grep -q '/api/coupons/generate' "$API_FILE"

grep -q 'Scaffold' "$COUPON_FILE"
grep -q 'bottomBar' "$COUPON_FILE"
grep -q 'couponErrorMessage' "$COUPON_FILE"

grep -q 'modelScore' README.md
grep -q 'GET /api/today' README.md
grep -q 'GET /api/history' README.md
grep -q 'GET /api/coupons/generate' README.md

if grep -R -E 'Bearer [A-Za-z0-9_-]{20,}' app scripts README.md
then
    echo "ERROR: secret/token bulundu"
    exit 1
fi

echo "API CONTRACT CHECK OK"
echo "FRONTEND ARCHITECTURE CHECK OK"

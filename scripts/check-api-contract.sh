#!/usr/bin/env bash
set -e

API_FILE="app/src/main/java/com/twohorse/app/data/api/TwoHorseApi.kt"

grep -q '"modelScore"' "$API_FILE"
grep -q '"distance_meters"' "$API_FILE"
grep -q '"track"' "$API_FILE"
grep -q '"agf_percent"' "$API_FILE"
grep -q '"recent_form_raw"' "$API_FILE"
grep -q '"/api/today"' "$API_FILE"
grep -q '"/api/history"' "$API_FILE"
grep -q '"/api/coupons/generate"' "$API_FILE"

if grep -R \
  -E 'ADMIN_TOKEN|Authorization: Bearer|jMW7_' \
  app/src/main
then
  echo "ERROR: frontend secret leakage"
  exit 1
fi

if grep -R \
  -E 'Jsoup|TjkRepository|ExpertRepository|Predictor' \
  app/src/main/java/com/twohorse/app
then
  echo "ERROR: backend/scraping logic leaked into frontend"
  exit 1
fi

echo "API CONTRACT STATIC CHECK OK"

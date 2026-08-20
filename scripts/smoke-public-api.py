import json
import sys
import urllib.parse
import urllib.request
import urllib.error

BASE = "https://two-horse-backend.veyseltosun-vt.workers.dev"

def get(path):
    req = urllib.request.Request(
        BASE + path,
        headers={
            "User-Agent":
                "two-horse-frontend-smoke/1.0"
        }
    )

    with urllib.request.urlopen(
        req,
        timeout=20
    ) as response:
        if response.status != 200:
            raise RuntimeError(
                f"{path}: HTTP {response.status}"
            )

        return json.load(response)

print("SMOKE health")
health = get("/api/health")
assert health.get("ok") is True

print("SMOKE today")
today = get("/api/today")

assert isinstance(
    today.get("meetings"),
    list
)

meetings = today["meetings"]

print(
    "meetings:",
    len(meetings)
)

print("SMOKE history")
history = get("/api/history")

assert isinstance(
    history.get("history"),
    list
)

print(
    "history:",
    len(history["history"])
)

coupon_success = False

for meeting in meetings:
    city = meeting.get("city")

    if not city:
        continue

    for sixfold in (1, 2):
        query = urllib.parse.urlencode(
            {
                "city": city,
                "budgetTl": 500,
                "sixfold": sixfold,
                "multiplier": 1,
            }
        )

        path = (
            "/api/coupons/generate?"
            + query
        )

        try:
            result = get(path)

        except urllib.error.HTTPError as exc:
            body = exc.read().decode(
                "utf-8",
                errors="replace"
            )

            print(
                "coupon unavailable:",
                city,
                sixfold,
                exc.code,
                body[:160]
            )

            continue

        assert result.get("ok") is True
        assert result.get("city")
        assert result.get("sixfold") in (1, 2)

        start = result.get("startRace")
        end = result.get("endRace")

        assert isinstance(start, int)
        assert isinstance(end, int)
        assert end - start == 5

        coupons = result.get("coupons")

        assert isinstance(
            coupons,
            list
        )

        for coupon in coupons:
            assert len(
                coupon.get(
                    "legs",
                    []
                )
            ) == 6

            assert (
                float(
                    coupon.get(
                        "totalTl",
                        0
                    )
                )
                <=
                float(
                    result["budgetTl"]
                )
                + 0.01
            )

        print(
            "coupon OK:",
            city,
            sixfold,
            "profiles=",
            len(coupons)
        )

        coupon_success = True
        break

    if coupon_success:
        break

if not coupon_success:
    print(
        "NOTICE: bugün kullanılabilir sixfold window bulunamadı; health/today/history smoke geçti."
    )

print("PUBLIC API SMOKE OK")

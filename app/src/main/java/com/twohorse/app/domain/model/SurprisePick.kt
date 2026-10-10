package com.twohorse.app.domain.model

/*
 * "Sürpriz" used to be simply the third horse by model score, which was
 * one of the AGF public's top three in 56% of races (875 races, 19 Aug -
 * 9 Oct 2026). It is now a real outsider: the best-scored horse the
 * public does NOT have in its AGF top three. That horse won 8.6% of
 * races at an average AGF of 8.3% (actual/expected 1.03), while horses
 * experts call "sürpriz" won 0.78x what their AGF implied.
 *
 * [ranked] is the race sorted by model score, best first. Until AGF is
 * published (race morning) there is no public ranking, so the third
 * horse by score is kept.
 */
fun pickSurprise(
    ranked: List<Horse>
): Horse? {
    if (ranked.size < 3) {
        return null
    }

    if (ranked.any { it.agfPercent == null }) {
        return ranked[2]
    }

    val agfTopThree =
        ranked
            .sortedWith(
                compareByDescending<Horse> {
                    it.agfPercent ?: 0.0
                }.thenBy {
                    it.number
                }
            )
            .take(3)
            .map { it.number }
            .toSet()

    val favorite = ranked[0].number
    val rival = ranked[1].number

    return ranked.firstOrNull {
        it.number !in agfTopThree &&
            it.number != favorite &&
            it.number != rival
    }
}

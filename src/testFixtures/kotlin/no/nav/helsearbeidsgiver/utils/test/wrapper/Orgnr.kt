package no.nav.helsearbeidsgiver.utils.test.wrapper

import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr
import no.nav.helsearbeidsgiver.utils.wrapper.orgnrSifferVekter
import no.nav.helsearbeidsgiver.utils.wrapper.sjekksum
import kotlin.random.Random

fun Orgnr.Companion.genererGyldig(): Orgnr {
    var orgnrSiffer = listOf(10)

    while (orgnrSiffer.last() == 10) {
        val orgnrUtenSjekksum = List(8) { Random.nextInt(10) }

        val sjekksum = sjekksum(orgnrUtenSjekksum, orgnrSifferVekter)

        orgnrSiffer = orgnrUtenSjekksum + sjekksum
    }

    return orgnrSiffer.joinToString(separator = "").let(::Orgnr)
}

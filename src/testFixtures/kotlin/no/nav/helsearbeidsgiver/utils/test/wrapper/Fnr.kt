package no.nav.helsearbeidsgiver.utils.test.wrapper

import no.nav.helsearbeidsgiver.utils.wrapper.Fnr
import no.nav.helsearbeidsgiver.utils.wrapper.fnrSifferVekter1
import no.nav.helsearbeidsgiver.utils.wrapper.fnrSifferVekter2
import no.nav.helsearbeidsgiver.utils.wrapper.sjekksum
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

private val foedselsdatoFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyy")

enum class TestPerson {
    NAV,
    TEST_NORGE
}

fun Fnr.Companion.genererGyldig(
    somDnr: Boolean = false,
    forTestPerson: TestPerson? = null
): Fnr {
    var fnrSiffer = listOf(10)

    while (10 in fnrSiffer.takeLast(2)) {
        val foedseldato = LocalDate.ofYearDay(
            Random.nextInt(1900, 2024),
            Random.nextInt(1, 366)
        )
            .format(foedselsdatoFormatter)

        val individSiffer = List(3) { Random.nextInt(10) }

        val fnrUtenSjekksum = foedseldato.map(Char::digitToInt)
            .justerForDnr(somDnr)
            .justerForTestPerson(forTestPerson)
            .plus(individSiffer)

        val sjekksum1 = sjekksum(fnrUtenSjekksum, fnrSifferVekter1)
        val sjekksum2 = sjekksum(fnrUtenSjekksum.plus(sjekksum1), fnrSifferVekter2)

        fnrSiffer = fnrUtenSjekksum + sjekksum1 + sjekksum2
    }

    return fnrSiffer.joinToString(separator = "").let(::Fnr)
}

private fun List<Int>.justerForDnr(somDnr: Boolean): List<Int> =
    if (somDnr) {
        val dnrJustertSiffer = first() + 4
        listOf(dnrJustertSiffer) + drop(1)
    } else {
        this
    }

private fun List<Int>.justerForTestPerson(forTestPerson: TestPerson?): List<Int> =
    if (forTestPerson != null) {
        val testPersonJustertSiffer = when (forTestPerson) {
            TestPerson.NAV -> get(2) + 4
            TestPerson.TEST_NORGE -> get(2) + 8
        }

        take(2) + testPersonJustertSiffer + drop(3)
    } else {
        this
    }

package no.nav.helsearbeidsgiver.utils.wrapper

internal fun sjekksum(
    siffer: List<Int>,
    sifferVekter: List<Int>,
): Int =
    siffer
        .zip(sifferVekter)
        .sumOf { (siffer, vekt) -> siffer * vekt }
        .mod(11)
        .let { 11 - it }
        .mod(11)

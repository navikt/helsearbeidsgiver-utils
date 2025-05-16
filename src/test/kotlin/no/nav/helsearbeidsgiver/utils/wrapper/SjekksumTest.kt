package no.nav.helsearbeidsgiver.utils.wrapper

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class SjekksumTest :
    FunSpec({
        context("gyldig") {
            withData(
                mapOf(
                    "siffer og sifferVekter er tomme" to
                        TestData(
                            siffer = emptyList(),
                            sifferVekter = emptyList(),
                            forventetSjekksum = 0,
                        ),
                    "siffer er tom" to
                        TestData(
                            siffer = emptyList(),
                            sifferVekter = listOf(1, 2, 3),
                            forventetSjekksum = 0,
                        ),
                    "sifferVekter er tom" to
                        TestData(
                            siffer = listOf(1, 2, 3),
                            sifferVekter = emptyList(),
                            forventetSjekksum = 0,
                        ),
                    "siffer er kortere enn sifferVekter" to
                        TestData(
                            siffer = listOf(3),
                            sifferVekter = listOf(5, 7, 9),
                            forventetSjekksum = 7,
                        ),
                    "sifferVekter er kortere enn siffer" to
                        TestData(
                            siffer = listOf(2, 4, 6),
                            sifferVekter = listOf(8),
                            forventetSjekksum = 6,
                        ),
                    "krever ikke enkeltsiffer" to
                        TestData(
                            siffer = listOf(13),
                            sifferVekter = listOf(14),
                            forventetSjekksum = 5,
                        ),
                    "sjekksum kalkuleres korrekt (1 av 2)" to
                        TestData(
                            siffer = listOf(4, 4, 9),
                            sifferVekter = listOf(9, 3, 4),
                            forventetSjekksum = 4,
                        ),
                    "sjekksum kalkuleres korrekt (2 av 2)" to
                        TestData(
                            siffer = listOf(4, 8, 4, 8),
                            sifferVekter = listOf(3, 8, 2, 1),
                            forventetSjekksum = 7,
                        ),
                    "sjekksum er tosifret" to
                        TestData(
                            siffer = listOf(3, 1, 0, 5, 3),
                            sifferVekter = listOf(6, 8, 4, 2, 3),
                            forventetSjekksum = 10,
                        ),
                    "sjekksum er 0" to
                        TestData(
                            siffer = listOf(7, 5, 6, 5, 1, 2, 1),
                            sifferVekter = listOf(0, 6, 0, 9, 9, 2, 0),
                            forventetSjekksum = 0,
                        ),
                ),
            ) {
                sjekksum(it.siffer, it.sifferVekter) shouldBe it.forventetSjekksum
            }
        }
    })

private data class TestData(
    val siffer: List<Int>,
    val sifferVekter: List<Int>,
    val forventetSjekksum: Int,
)

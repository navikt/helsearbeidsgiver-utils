package no.nav.helsearbeidsgiver.utils.collection

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MapUtilsKtTest : FunSpec({

    context("mapKeysNotNull") {
        test("transforms keys and removes transformations returning null") {
            val numberStrsByNumbers = mapOf(
                1 to "one",
                2 to "two",
                3 to "three",
                4 to "four"
            )

            val evenNumberStrsByNumbers = numberStrsByNumbers.mapKeysNotNull {
                if (it % 2 == 0) {
                    it
                } else {
                    null
                }
            }

            evenNumberStrsByNumbers shouldBe mapOf(
                2 to "two",
                4 to "four"
            )
        }

        test("supports nullable keys and values in original map") {
            val nullableNumberStrsByNullableNumbers = mapOf(
                1 to "one",
                2 to null,
                null to "null"
            )

            val nullableNumberStrsByNumbers = nullableNumberStrsByNullableNumbers.mapKeysNotNull { it }

            nullableNumberStrsByNumbers shouldBe mapOf(
                1 to "one",
                2 to null
            )
        }
    }

    context("mapValuesNotNull") {
        test("transforms values and removes transformations returning null") {
            val numbersByNumberStrs = mapOf(
                "one" to 1,
                "two" to 2,
                "three" to 3,
                "four" to 4
            )

            val oddNumbersByNumberStrs = numbersByNumberStrs.mapValuesNotNull {
                if (it % 2 == 1) {
                    it
                } else {
                    null
                }
            }

            oddNumbersByNumberStrs shouldBe mapOf(
                "one" to 1,
                "three" to 3
            )
        }

        test("supports nullable keys and values in original map") {
            val nullableNumbersByNullableNumberStrs = mapOf(
                null to 0,
                "one" to 1,
                "two" to null
            )

            val numbersByNullableNumberStrs = nullableNumbersByNullableNumberStrs.mapValuesNotNull { it }

            numbersByNullableNumberStrs shouldBe mapOf(
                null to 0,
                "one" to 1
            )
        }
    }
})

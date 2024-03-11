package no.nav.helsearbeidsgiver.utils.wrapper

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class OrgnrTest : FunSpec({

    context("gyldig") {
        withData(
            listOf(
                "161231657",
                "908498461",
                "135103217",
                "684603132",
                "167484972",
                "796033029"
            )
        ) {
            shouldNotThrowAny {
                Orgnr(it)
            }
        }
    }

    context("ugyldig") {
        withData(
            listOf(
                "1233", // for kort
                "1234567895", // for langt
                "12x4567890", // med bokstav
                "1234 67892", // med mellomrom
                "161231654", // ugyldig kontrollsiffer
                "908498464", // ugyldig kontrollsiffer
                "65432445-", // ugyldig kontrollsiffer (sjekksum = 10)
                "6543244510" // ugyldig kontrollsiffer (sjekksum = 10)
            )
        ) {
            shouldThrowExactly<IllegalArgumentException> {
                Orgnr(it)
            }
        }

        test("tom streng") {
            shouldThrowExactly<IllegalArgumentException> {
                Orgnr("")
            }
        }
    }

    test("toString gir wrappet verdi") {
        Orgnr("123456785").let {
            it.toString() shouldBe it.verdi
        }
    }
})

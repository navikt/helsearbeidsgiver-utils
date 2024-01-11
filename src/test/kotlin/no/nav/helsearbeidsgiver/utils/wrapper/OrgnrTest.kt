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
                "161231654",
                "908498460",
                "135103210",
                "684603132",
                "167484979",
                "796033020"
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
                "123", // for kort
                "0123456789", // for langt
                "12x456789", // med bokstav
                "1234 6789" // med mellomrom
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
        Orgnr("123456789").let {
            it.toString() shouldBe it.verdi
        }
    }
})

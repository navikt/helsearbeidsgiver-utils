package no.nav.helsearbeidsgiver.utils.log

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LogUtilsTest :
    FunSpec({

        test("logger fra streng bruker strengen som navn") {
            val str = "log me please"

            str.logger().name shouldBe str
        }

        test("logger fra klasse bruker klassens 'qualified name' som navn") {
            class EnKlasse

            val enKlasse = EnKlasse()

            enKlasse.logger().name shouldBe enKlasse.javaClass.name
        }

        test("sikkerLogger logger til tjenestekall") {
            sikkerLogger().name shouldBe "tjenestekall"
        }
    })

package no.nav.helsearbeidsgiver.utils.log

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo

class LogUtilsTest : StringSpec({

    "logger fra streng bruker strengen som navn" {
        val str = "log me please"

        str.logger().name shouldBeEqualComparingTo str
    }

    "logger fra klasse bruker klassens 'qualified name' som navn" {
        class EnKlasse

        val enKlasse = EnKlasse()

        enKlasse.logger().name shouldBeEqualComparingTo enKlasse.javaClass.name
    }
})

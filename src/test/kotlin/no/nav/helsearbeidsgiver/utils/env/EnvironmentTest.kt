package no.nav.helsearbeidsgiver.utils.env

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldNotBeEmpty

class EnvironmentTest : StringSpec({

    "skal kunne lese inn miljø variabler" {
        val settings = DemoSettings()
        settings.javaHome.shouldNotBeEmpty()
    }
})

package no.nav.helsearbeidsgiver.utils.json

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.utils.test.json.removeJsonWhitespace

class JsonStringUtilsKtTest :
    FunSpec({
        test("'String.removeJsonWhitespace(): String' fjerner whitespace fra json uten å endre innholdet") {
            val jsonMedWhitespace = """
            {
                "en streng med mellomrom": "det er leet",
                "tall": 1337,
                "bool": true,
                "liste": ["med mellomrom", "uten", "med igjen", "ikke"],
                "objekt": {
                    "det er leet": "1337",
                    "speak": "5p34k"
                }
            }
        """

            val jsonUtenWhitespace =
                """{"en streng med mellomrom":"det er leet","tall":1337,"bool":true,"liste":["med mellomrom","uten","med igjen","ikke"],""" +
                    """"objekt":{"det er leet":"1337","speak":"5p34k"}}"""

            jsonMedWhitespace.removeJsonWhitespace() shouldBe jsonUtenWhitespace
        }
    })

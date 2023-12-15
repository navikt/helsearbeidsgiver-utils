package no.nav.helsearbeidsgiver.utils.json

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import no.nav.helsearbeidsgiver.utils.json.serializer.set
import no.nav.helsearbeidsgiver.utils.test.date.januar
import no.nav.helsearbeidsgiver.utils.test.date.juli
import no.nav.helsearbeidsgiver.utils.test.date.kl
import no.nav.helsearbeidsgiver.utils.test.json.removeJsonWhitespace
import java.time.Month
import java.time.YearMonth
import java.util.UUID

class SerializationUtilsKtTest : FunSpec({

    context("toJson") {
        test("serialiserer korrekt fra generisk T til JsonElement") {
            val samwise = Hobbit(
                name = Name("Samwise", "Gamgee"),
                age = 38
            )

            val expectedJson = """
                {
                    "name": {
                        "first": "Samwise",
                        "last": "Gamgee"
                    },
                    "age": 38
                }
            """.removeJsonWhitespace()

            val actualJson = samwise.toJson(Hobbit.serializer()).toString()

            actualJson shouldBe expectedJson
        }

        test("serialiserer korrekt fra nullable generisk T til JsonElement") {
            val hobbitNames = listOf("Samwise", null)

            val jsonList = hobbitNames.map { it.toJson(String.serializer().nullable).toString() }

            jsonList shouldBe listOf("\"Samwise\"", "null")
        }

        test("serialiserer korrekt fra List<T> til JsonElement") {
            val hobbits = listOf(
                Hobbit(
                    name = Name("Samwise", "Gamgee"),
                    age = 38
                ),
                Hobbit(
                    name = Name("Frodo", "Baggins"),
                    age = 50
                )
            )

            val expectedJson = """
                [
                    {
                        "name": {
                            "first": "Samwise",
                            "last": "Gamgee"
                        },
                        "age": 38
                    },
                    {
                        "name": {
                            "first": "Frodo",
                            "last": "Baggins"
                        },
                        "age": 50
                    }
                ]
            """.removeJsonWhitespace()

            val actualJson = hobbits.toJson(Hobbit.serializer()).toString()

            actualJson shouldBe expectedJson
        }

        test("serialiserer korrekt fra List<T?> til JsonElement") {
            val hobbitNames = listOf("Frodo", null)

            val json = hobbitNames.toJson(String.serializer().nullable).toString()

            json shouldBe """["Frodo",null]"""
        }

        test("serialiserer korrekt fra streng til JsonElement") {
            val str = "Meriadoc"

            val expectedJson = "\"Meriadoc\""

            val actualJson = str.toJson().toString()

            actualJson shouldBe expectedJson
        }

        test("serialiserer korrekt fra Map<String, JsonElement> til JsonElement") {
            val tallgrupper = mapOf(
                "partall" to setOf(2, 4, 6),
                "oddetall" to setOf(1, 3, 5, 7)
            )
                .mapValues { (_, value) -> value.toJson(Int.serializer().set()) }

            val expectedJson = """
                {
                    "partall": [2, 4, 6],
                    "oddetall": [1, 3, 5, 7]
                }
            """.removeJsonWhitespace()

            val actualJson = tallgrupper.toJson().toString()

            actualJson shouldBe expectedJson
        }

        test("serialiserer korrekt fra måned til JsonElement") {
            val maaned = YearMonth.of(1699, Month.AUGUST)

            val json = maaned.toJson().toString()

            json shouldBe "\"1699-08\""
        }

        test("serialiserer korrekt fra dato til JsonElement") {
            val dato = 28.januar(1876)

            val json = dato.toJson().toString()

            json shouldBe "\"1876-01-28\""
        }

        test("serialiserer korrekt fra tidspunkt (LocalDateTime) til JsonElement") {
            val tidspunkt = 3.juli(1777).kl(5, 47, 57, 789_012_345)

            val json = tidspunkt.toJson().toString()

            json shouldBe "\"1777-07-03T05:47:57.789012345\""
        }

        test("serialiserer korrekt fra UUID til JsonElement") {
            val uuid = UUID.randomUUID()

            val json = uuid.toJson().toString()

            json shouldBe "\"$uuid\""
        }
    }

    context("toJsonStr") {
        test("serialiserer korrekt fra generisk T til json-streng") {
            val samwise = Hobbit(
                name = Name("Frodo", "Baggins"),
                age = 50
            )

            val expectedJson = """
                {
                    "name": {
                        "first": "Frodo",
                        "last": "Baggins"
                    },
                    "age": 50
                }
            """.removeJsonWhitespace()

            val actualJson = samwise.toJsonStr(Hobbit.serializer())

            actualJson shouldBe expectedJson
        }

        test("serialiserer korrekt fra nullable generisk T til json-streng") {
            val hobbitNames = listOf("Frodo", null)

            val jsonList = hobbitNames.map { it.toJsonStr(String.serializer().nullable) }

            jsonList shouldBe listOf("\"Frodo\"", "null")
        }
    }
})

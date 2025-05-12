package no.nav.helsearbeidsgiver.utils.json

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import no.nav.helsearbeidsgiver.utils.test.json.removeJsonWhitespace

class DeserializationUtilsKtTest :
    FunSpec({

        context("jsonIgnoreUnknown") {
            test("ignorerer ukjente felt") {
                val bilboJson =
                    """
                {
                    "name": {
                        "first": "Bilbo",
                        "last": "Baggins"
                    },
                    "age": 111,
                    "adoptiveSon": "Frodo"
                }
            """.removeJsonWhitespace()
                        .parseJson()

                val expectedObject =
                    Hobbit(
                        name = Name("Bilbo", "Baggins"),
                        age = 111,
                    )

                val actualObject = jsonConfig.decodeFromJsonElement(Hobbit.serializer(), bilboJson)

                actualObject shouldBe expectedObject
            }
        }

        context("parseJson") {
            test("deserialiserer korrekt fra json-streng til JsonElement") {
                val bilboJson =
                    """
                {
                    "name": {
                        "first": "Bilbo",
                        "last": "Baggins"
                    },
                    "age": 111
                }
            """.removeJsonWhitespace()

                shouldNotThrowAny {
                    val parsed = bilboJson.parseJson()

                    parsed.jsonObject.let { hobbit ->
                        hobbit["name"].shouldNotBeNull().jsonObject.let { name ->
                            name["first"].shouldNotBeNull().jsonPrimitive.content shouldBe "Bilbo"
                            name["last"].shouldNotBeNull().jsonPrimitive.content shouldBe "Baggins"
                        }
                        hobbit["age"].shouldNotBeNull().jsonPrimitive.content shouldBe "111"
                    }
                }
            }
        }

        context("fromJson") {
            test("deserialiserer korrekt fra JsonElement til generisk T") {
                val merryJson =
                    """
                {
                    "name": {
                        "first": "Meriadoc",
                        "last": "Brandybuck"
                    },
                    "age": 36
                }
            """.removeJsonWhitespace()

                val expectedObject =
                    Hobbit(
                        name = Name("Meriadoc", "Brandybuck"),
                        age = 36,
                    )

                val actualObject = merryJson.parseJson().fromJson(Hobbit.serializer())

                actualObject shouldBe expectedObject
            }

            test("deserialiserer korrekt fra JsonElement til nullable generisk T") {
                val nicknamesJsonList = listOf("\"Meriadoc\"", "null")

                val expectedJsonList = listOf("Meriadoc", null)

                val actualJsonList =
                    nicknamesJsonList.map {
                        it.parseJson().fromJson(String.serializer().nullable)
                    }

                actualJsonList shouldBe expectedJsonList
            }

            test("deserialiserer korrekt fra json-streng til generisk T") {
                val pippinJson =
                    """
                {
                    "name": {
                        "first": "Peregrin",
                        "last": "Took"
                    },
                    "age": 28
                }
            """.removeJsonWhitespace()

                val expectedObject =
                    Hobbit(
                        name = Name("Peregrin", "Took"),
                        age = 28,
                    )

                val actualObject = pippinJson.fromJson(Hobbit.serializer())

                actualObject shouldBe expectedObject
            }

            test("deserialiserer korrekt fra json-streng til nullable generisk T") {
                val nicknamesJsonList = listOf("\"Peregin\"", "null")

                val expectedJsonList = listOf("Peregin", null)

                val actualJsonList =
                    nicknamesJsonList.map {
                        it.fromJson(String.serializer().nullable)
                    }

                actualJsonList shouldBe expectedJsonList
            }

            test("ignorerer ukjente felt") {
                val rosieJson =
                    """
                {
                    "name": {
                        "first": "Rosie",
                        "last": "Gamgee"
                    },
                    "age": 34,
                    "maidenName": "Cotton"
                }
            """.removeJsonWhitespace()

                // Forventer to helt like objekter
                val expectedObjects =
                    List(2) {
                        Hobbit(
                            name = Name("Rosie", "Gamgee"),
                            age = 34,
                        )
                    }

                // Begge metoder skal ignorere ukjent felt
                val actualObjects =
                    listOf(
                        rosieJson.parseJson().fromJson(Hobbit.serializer()),
                        rosieJson.fromJson(Hobbit.serializer()),
                    )

                actualObjects shouldBe expectedObjects
            }
        }

        context("fromJsonMap") {
            test("deserialiserer korrekt fra JsonElement til Map<T, JsonElement>") {
                val gandalfJson =
                    """
                {
                    "name": {
                        "first": "Gandalf",
                        "last": "The Grey"
                    },
                    "age": 2000
                }
            """.removeJsonWhitespace()

                val expectedObject =
                    mapOf(
                        "name" to Name("Gandalf", "The Grey").toJson(Name.serializer()),
                        "age" to 2000.toJson(Int.serializer()),
                    )

                val actualObject = gandalfJson.parseJson().fromJsonMap(String.serializer())

                actualObject shouldBe expectedObject
            }
        }

        context("fromJsonMapFiltered") {
            test("deserialiserer korrekt fra JsonElement til Map<T, JsonElement>") {
                val booksJson =
                    """
                {
                    "precursor": "The Hobbit",
                    "1": "The Fellowship of the Ring",
                    "2": "The Two Towers",
                    "3": "The Return of the King"
                }
            """.removeJsonWhitespace()

                val expectedObject =
                    mapOf(
                        1 to "The Fellowship of the Ring".toJson(),
                        2 to "The Two Towers".toJson(),
                        3 to "The Return of the King".toJson(),
                    )

                val actualObject = booksJson.parseJson().fromJsonMapFiltered(Int.serializer())

                actualObject shouldBe expectedObject
            }
        }

        context("tryOrNull-hjelpefunksjon") {
            test("verdi returneres") {
                val result = tryOrNull { "all good" }

                result shouldBe "all good"
            }

            test("exception mappes til null") {
                val result = tryOrNull<String> { throw RuntimeException("shit's on fire, yo") }

                result shouldBe null
            }
        }
    })

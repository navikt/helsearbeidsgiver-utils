package no.nav.helsearbeidsgiver.utils.log

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeUnique
import io.kotest.matchers.collections.shouldNotContainNull
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.UUIDVersion
import io.kotest.matchers.string.shouldBeUUID
import io.kotest.matchers.string.shouldMatch
import org.slf4j.MDC
import kotlin.reflect.KCallable

class MdcUtilsTest :
    FunSpec({

        beforeEach {
            MDC.clear()
        }

        context(MdcUtils::getCallId.name) {
            test("henter callId hvis finnes") {
                MdcUtils.withCallIdAsUuid {
                    // callId er på MDC, så samme callId hentes hver gang
                    MdcUtils.getCallId() shouldBe MdcUtils.getCallId()
                }
            }

            test("lager ny callId hvis ingen finnes") {
                // callId er ikke i MDC, så ny callId hentes/lages hver gang
                MdcUtils.getCallId() shouldNotBe MdcUtils.getCallId()
            }
        }

        context(navn(MdcUtils::withCallId)) {
            test("bruker egendefinert ID som callId") {
                MdcUtils.withCallId {
                    MdcUtils
                        .getCallId()
                        .shouldMatch("CallId_\\d+_\\d+")
                }
            }
        }

        context(navn(MdcUtils::withCallIdAsUuid)) {
            test("bruker uuid4 som callId") {
                MdcUtils.withCallIdAsUuid {
                    MdcUtils
                        .getCallId()
                        .shouldBeUUID(version = UUIDVersion.V4)
                }
            }
        }

        // Bruker eksplisitt typesignatur i liste for å kunne bruke referanser til generiske funksjoner
        listOf<Pair<(() -> String) -> String, String>>(
            Pair(MdcUtils::withCallId, navn(MdcUtils::withCallId)),
            Pair(MdcUtils::withCallIdAsUuid, navn(MdcUtils::withCallIdAsUuid)),
        ).forEach { (withCallIdFnToTest, nameFnToTest) ->
            context(nameFnToTest) {
                test("callId legges til MDC") {
                    withCallIdFnToTest {
                        // callId er på MDC, så samme callId hentes hver gang
                        MdcUtils.getCallId() shouldBe MdcUtils.getCallId()
                    }
                }

                test("callId fjernes fra MDC etter wrapper") {
                    // callId er ikke i MDC, så ny callId hentes/lages hver gang
                    val callIdBeforeWrapper = MdcUtils.getCallId()

                    val callIdInWrapper =
                        withCallIdFnToTest {
                            // callId er på MDC, så samme callId hentes hver gang
                            MdcUtils.getCallId()
                        }

                    // callId er ikke lenger i MDC, så ny callId hentes/lages hver gang
                    val callIdAfterWrapper = MdcUtils.getCallId()

                    listOf(callIdBeforeWrapper, callIdInWrapper, callIdAfterWrapper)
                        .shouldNotContainNull()
                        .shouldBeUnique()
                }
            }
        }

        context(navn(MdcUtils::withLogFields)) {
            test("loggfelter legges til og fjernes") {
                MDC.get("yellow").shouldBeNull()
                MDC.get("red").shouldBeNull()

                MdcUtils.withLogFields(
                    "yellow" to "submarine",
                    "red" to "flag",
                ) {
                    MDC.get("yellow") shouldBe "submarine"
                    MDC.get("red") shouldBe "flag"
                }

                MDC.get("yellow").shouldBeNull()
                MDC.get("red").shouldBeNull()
            }

            test("loggfelter som legges til senere overskriver tidligere loggfelter midlertidig") {
                MDC.get("blue").shouldBeNull()

                MdcUtils.withLogFields(
                    "blue" to "monday",
                ) {
                    MDC.get("blue") shouldBe "monday"

                    MdcUtils.withLogFields(
                        "blue" to "orchid",
                    ) {
                        MDC.get("blue") shouldBe "orchid"
                    }

                    MDC.get("blue") shouldBe "monday"
                }

                MDC.get("blue").shouldBeNull()
            }

            test("returnerer korrekt verdi") {
                val expected = "314"

                val actual =
                    MdcUtils.withLogFields {
                        expected
                    }

                actual shouldBe expected
            }

            test("returnerer korrekt verdi ved non-local return") {
                val expected = "spanish inquisition"

                val actualFn = fun(): String =
                    MdcUtils.withLogFields(
                        "green" to "light",
                    ) {
                        MDC.get("green") shouldBe "light"
                        return expected
                    }

                actualFn() shouldBe expected
            }

            test("fjerner loggfelter ved non-local return") {
                val fnWithNonLocalReturn = fun(): String =
                    MdcUtils.withLogFields(
                        "green" to "light",
                    ) {
                        MDC.get("green") shouldBe "light"
                        return "42"
                    }

                fnWithNonLocalReturn()

                MDC.get("green").shouldBeNull()
            }

            test("fjerner loggfelter ved exception") {
                shouldThrowExactly<NullPointerException> {
                    MdcUtils.withLogFields(
                        "black" to "betty",
                    ) {
                        MDC.get("black") shouldBe "betty"
                        throw NullPointerException()
                    }
                }

                MDC.get("black").shouldBeNull()
            }
        }
    })

// Hent navn fra funksjon med generisk output (castes til String via parameter)
private fun navn(kCallable: KCallable<String>): String = kCallable.name

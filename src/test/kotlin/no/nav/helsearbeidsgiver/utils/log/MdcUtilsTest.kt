package no.nav.helsearbeidsgiver.utils.log

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.Row2
import io.kotest.data.row
import io.kotest.matchers.collections.shouldBeUnique
import io.kotest.matchers.collections.shouldNotContainNull
import io.kotest.matchers.string.UUIDVersion
import io.kotest.matchers.string.shouldBeEqualIgnoringCase
import io.kotest.matchers.string.shouldBeUUID
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldNotBeEqualIgnoringCase
import org.slf4j.MDC
import kotlin.reflect.KCallable

class MdcUtilsTest : FunSpec({

    beforeEach {
        MDC.clear()
    }

    context(MdcUtils::getCallId.name) {
        test("henter callId hvis finnes") {
            MdcUtils.withCallIdAsUuid {
                // callId er på MDC, så samme callId hentes hver gang
                MdcUtils.getCallId() shouldBeEqualIgnoringCase MdcUtils.getCallId()
            }
        }

        test("lager ny callId hvis ingen finnes") {
            // callId er ikke i MDC, så ny callId hentes/lages hver gang
            MdcUtils.getCallId() shouldNotBeEqualIgnoringCase MdcUtils.getCallId()
        }
    }

    context(navn(MdcUtils::withCallId)) {
        test("bruker egendefinert ID som callId") {
            MdcUtils.withCallId {
                MdcUtils.getCallId()
                    .shouldMatch("CallId_\\d+_\\d+")
            }
        }

        test("returnerer korrekt verdi ved non local return") {
            val expected = "112358"

            val actualFn = fun(): String = MdcUtils.withCallId { return expected }

            actualFn() shouldBeEqualIgnoringCase expected
        }

        test("fjerner callId fra MDC ved non local return") {
            val fnWithNonLocalReturn = fun(): String = MdcUtils.withCallId { return "42" }

            fnWithNonLocalReturn()

            // callId er ikke i MDC, så ny callId hentes/lages hver gang
            MdcUtils.getCallId() shouldNotBeEqualIgnoringCase MdcUtils.getCallId()
        }
    }

    context(navn(MdcUtils::withCallIdAsUuid)) {
        test("bruker uuid4 som callId") {
            MdcUtils.withCallIdAsUuid {
                MdcUtils.getCallId()
                    .shouldBeUUID(version = UUIDVersion.V4)
            }
        }

        test("returnerer korrekt verdi ved non local return") {
            val expected = "112358"

            val actualFn = fun(): String = MdcUtils.withCallIdAsUuid { return expected }

            actualFn() shouldBeEqualIgnoringCase expected
        }

        test("fjerner callId fra MDC ved non local return") {
            val fnWithNonLocalReturn = fun(): String = MdcUtils.withCallIdAsUuid { return "42" }

            fnWithNonLocalReturn()

            // callId er ikke i MDC, så ny callId hentes/lages hver gang
            MdcUtils.getCallId() shouldNotBeEqualIgnoringCase MdcUtils.getCallId()
        }
    }

    // Bruker eksplisitt typesignatur i liste for å kunne bruke referanser til generiske funksjoner
    listOf<Row2<(() -> String) -> String, String>>(
        row(MdcUtils::withCallId, navn(MdcUtils::withCallId)),
        row(MdcUtils::withCallIdAsUuid, navn(MdcUtils::withCallIdAsUuid))
    )
        // Denne loopen kan ikke inneholde tester som involverer non-local returns pga. bruk av referanser
        .forEach { (withCallIdFnToTest, nameFnToTest) ->
            context(nameFnToTest) {
                test("legger callId midlertidig på MDC") {
                    // callId er ikke i MDC, så ny callId hentes/lages hver gang
                    val callIdBeforeWrapper = MdcUtils.getCallId()
                        .shouldNotBeEqualIgnoringCase(MdcUtils.getCallId())

                    val callIdInWrapper = withCallIdFnToTest {
                        // callId er på MDC, så samme callId hentes hver gang
                        MdcUtils.getCallId()
                            .also {
                                it shouldBeEqualIgnoringCase MdcUtils.getCallId()
                            }
                    }

                    // callId er ikke lenger i MDC, så ny callId hentes/lages hver gang
                    val callIdAfterWrapper = MdcUtils.getCallId()
                        .shouldNotBeEqualIgnoringCase(MdcUtils.getCallId())

                    listOf(callIdBeforeWrapper, callIdInWrapper, callIdAfterWrapper)
                        .shouldNotContainNull()
                        .shouldBeUnique()
                }

                test("returnerer korrekt verdi") {
                    val expected = "314"

                    withCallIdFnToTest { expected }
                        .shouldBeEqualIgnoringCase(expected)
                }

                test("fjerner callId fra MDC ved exception") {
                    shouldThrowExactly<NullPointerException> {
                        withCallIdFnToTest {
                            throw NullPointerException()
                        }
                    }

                    // callId er ikke i MDC, så ny callId hentes/lages hver gang
                    MdcUtils.getCallId() shouldNotBeEqualIgnoringCase MdcUtils.getCallId()
                }
            }
        }
})

// Hent navn fra funksjon med generisk output (castes til String via parameter)
private fun navn(kCallable: KCallable<String>): String =
    kCallable.name

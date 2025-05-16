package no.nav.helsearbeidsgiver.utils.wrapper

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.utils.test.wrapper.TestPerson
import no.nav.helsearbeidsgiver.utils.test.wrapper.genererGyldig

class FnrTest :
    FunSpec({

        context("gyldig") {
            withData(
                listOf(
                    "01010012356",
                    "19092212338",
                    "29104512346",
                    "31129912319",
                    "25056812310",
                    "11085812345",
                    "41066712395", // D-nummer
                    "45066612310", // D-nummer
                    "50066612306", // D-nummer
                    "56066612320", // D-nummer
                    "60066612387", // D-nummer
                    "69066612309", // D-nummer
                    "70066712379", // D-nummer
                    "71066612397", // D-nummer
                    "01490012369", // Testperson fra NAV
                    "01500012397", // Testperson fra NAV
                    "01890012341", // Testperson fra TestNorge
                    "01900912338", // Testperson fra TestNorge
                ),
            ) {
                shouldNotThrowAny {
                    Fnr(it)
                }
            }
        }

        context("ugyldig") {
            withData(
                listOf(
                    "00010012317", // dag 0, andre siffer feil
                    "32010012308", // dag 32, andre siffer feil
                    "40010012300", // dag 40, andre siffer feil (D-nummer)
                    "72010012482", // dag 72, andre siffer feil (D-nummer)
                    "80010012485", // dag 80, første siffer feil
                    "01000012366", // måned 0, fjerde siffer feil
                    "01130012384", // måned 13, fjerde siffer feil
                    "01200012352", // måned 20, tredje siffer feil
                    "01390012310", // måned 39, tredje og fjerde siffer feil
                    "01400012349", // måned 40, fjerde siffer feil (testperson)
                    "01530012367", // måned 53, fjerde siffer feil (testperson)
                    "01790012484", // måned 79, tredje og fjerde siffer feil
                    "01800012321", // måned 80, fjerde siffer feil (testperson)
                    "01930012420", // måned 93, fjerde siffer feil (testperson)
                    "0101001234", // for kort
                    "010100123567", // for langt
                    "010100x2345", // med bokstav
                    "010100 1234", // med mellomrom
                    "01010012313", // ugyldig kontrollsiffer 1
                    "19092212389", // ugyldig kontrollsiffer 1
                    "060606123-3", // ugyldig kontrollsiffer 1 (sjekksum = 10)
                    "060606123103", // ugyldig kontrollsiffer 1 (sjekksum = 10)
                    "41066712336", // ugyldig kontrollsiffer 1 (D-nummer)
                    "01490012326", // ugyldig kontrollsiffer 1 (Testperson fra NAV)
                    "01890012309", // ugyldig kontrollsiffer 1 (Testperson fra TestNorge)
                    "29104512344", // ugyldig kontrollsiffer 2
                    "31129912310", // ugyldig kontrollsiffer 2
                    "1403551234-", // ugyldig kontrollsiffer 2 (sjekksum = 10)
                    "140355123410", // ugyldig kontrollsiffer 2 (sjekksum = 10)
                    "45066612316", // ugyldig kontrollsiffer 2 (D-nummer)
                    "01500012390", // ugyldig kontrollsiffer 2 (Testperson fra NAV)
                    "01900912339", // ugyldig kontrollsiffer 2 (Testperson fra TestNorge)
                ),
            ) {
                shouldThrowExactly<IllegalArgumentException> {
                    Fnr(it)
                }
            }

            test("tom streng") {
                shouldThrowExactly<IllegalArgumentException> {
                    Fnr("")
                }
            }
        }

        test("toString gir wrappet verdi") {
            Fnr("24120612359").let {
                it.toString() shouldBe it.verdi
            }
        }

        context(Fnr::genererGyldig.name) {

            withData(
                mapOf(
                    "fnr" to
                        TestFnrData(
                            fnrGenerator = { Fnr.genererGyldig() },
                            forventetDagFoersteSiffer = 0..3,
                            forventetMaanedFoersteSiffer = 0..1,
                        ),
                    "fnr for NAV-testperson" to
                        TestFnrData(
                            fnrGenerator = { Fnr.genererGyldig(forTestPerson = TestPerson.NAV) },
                            forventetDagFoersteSiffer = 0..3,
                            forventetMaanedFoersteSiffer = 4..5,
                        ),
                    "fnr for TestNorge-testperson" to
                        TestFnrData(
                            fnrGenerator = { Fnr.genererGyldig(forTestPerson = TestPerson.TEST_NORGE) },
                            forventetDagFoersteSiffer = 0..3,
                            forventetMaanedFoersteSiffer = 8..9,
                        ),
                    "dnr" to
                        TestFnrData(
                            fnrGenerator = { Fnr.genererGyldig(somDnr = true) },
                            forventetDagFoersteSiffer = 4..7,
                            forventetMaanedFoersteSiffer = 0..1,
                        ),
                    "dnr for NAV-testperson" to
                        TestFnrData(
                            fnrGenerator = { Fnr.genererGyldig(somDnr = true, forTestPerson = TestPerson.NAV) },
                            forventetDagFoersteSiffer = 4..7,
                            forventetMaanedFoersteSiffer = 4..5,
                        ),
                    "dnr for TestNorge-testperson" to
                        TestFnrData(
                            fnrGenerator = { Fnr.genererGyldig(somDnr = true, forTestPerson = TestPerson.TEST_NORGE) },
                            forventetDagFoersteSiffer = 4..7,
                            forventetMaanedFoersteSiffer = 8..9,
                        ),
                ),
            ) { testData ->
                repeat(1000) {
                    val fnr =
                        shouldNotThrowAny {
                            testData.fnrGenerator()
                        }

                    fnr.verdi[0].digitToInt() shouldBeInRange testData.forventetDagFoersteSiffer
                    fnr.verdi[2].digitToInt() shouldBeInRange testData.forventetMaanedFoersteSiffer
                }
            }
        }
    })

private class TestFnrData(
    val fnrGenerator: () -> Fnr,
    val forventetDagFoersteSiffer: IntRange,
    val forventetMaanedFoersteSiffer: IntRange,
)

package no.nav.helsearbeidsgiver.utils.date

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class DateUtilsKtTest :
    FunSpec({

        context(LocalDate::tilNorskFormat.name) {
            test("LocalDate konverteres til norsk format") {
                val dato = LocalDate.of(2007, 5, 22)

                val expectedFormat = "22.05.2007"

                val actualFormat = dato.tilNorskFormat()

                actualFormat shouldBe expectedFormat
            }
        }

        context(LocalDateTime::toOffsetDateTimeOslo.name) {
            test("LocalDateTime konverteres til OffsetDateTime med Oslo-tid uten sommertid") {
                val dato = LocalDateTime.of(2020, 11, 8, 1, 2, 3, 4)

                val expectedOffsetDateTime = OffsetDateTime.of(2020, 11, 8, 1, 2, 3, 4, ZoneOffset.of("+1"))
                val expectedOffsetDateTimeFormat = "2020-11-08T01:02:03.000000004+01:00"

                val actualOffsetDateTime = dato.toOffsetDateTimeOslo()

                actualOffsetDateTime shouldBe expectedOffsetDateTime
                actualOffsetDateTime.toString() shouldBe expectedOffsetDateTimeFormat
            }

            test("LocalDateTime konverteres til OffsetDateTime med Oslo-tid med sommertid") {
                val dato = LocalDateTime.of(2019, 6, 30, 11, 22, 33, 44)

                val expectedOffsetDateTime = OffsetDateTime.of(2019, 6, 30, 11, 22, 33, 44, ZoneOffset.of("+2"))
                val expectedOffsetDateTimeFormat = "2019-06-30T11:22:33.000000044+02:00"

                val actualOffsetDateTime = dato.toOffsetDateTimeOslo()

                actualOffsetDateTime shouldBe expectedOffsetDateTime
                actualOffsetDateTime.toString() shouldBe expectedOffsetDateTimeFormat
            }
        }
    })

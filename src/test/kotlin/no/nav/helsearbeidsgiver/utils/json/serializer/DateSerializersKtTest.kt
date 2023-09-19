package no.nav.helsearbeidsgiver.utils.json.serializer

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import no.nav.helsearbeidsgiver.utils.json.fromJson
import no.nav.helsearbeidsgiver.utils.json.toJsonStr
import no.nav.helsearbeidsgiver.utils.test.date.februar
import no.nav.helsearbeidsgiver.utils.test.date.juni
import java.time.LocalDateTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.ZoneOffset

class DateSerializersKtTest : FunSpec({
    context("YearMonthSerializer") {
        test("serialiserer korrekt") {
            val maaned = YearMonth.of(1800, Month.APRIL)

            val json = maaned.toJsonStr(YearMonthSerializer)

            json shouldBe "\"1800-04\""
        }

        test("deserialiserer korrekt") {
            val json = "\"1900-08\""

            val maaned = json.fromJson(YearMonthSerializer)

            maaned shouldBe YearMonth.of(1900, Month.AUGUST)
        }

        test("gir SerializationException ved deserialiseringsfeil") {
            shouldThrow<SerializationException> {
                "ikke en måned".fromJson(YearMonthSerializer)
            }
        }
    }

    context("LocalDateSerializer") {
        test("serialiserer korrekt") {
            val dato = 12.februar(1815)

            val json = dato.toJsonStr(LocalDateSerializer)

            json shouldBe "\"1815-02-12\""
        }

        test("deserialiserer korrekt") {
            val json = "\"1915-06-22\""

            val dato = json.fromJson(LocalDateSerializer)

            dato shouldBe 22.juni(1915)
        }

        test("gir SerializationException ved deserialiseringsfeil") {
            shouldThrow<SerializationException> {
                "ikke en dato".fromJson(LocalDateSerializer)
            }
        }
    }

    context("LocalDateTimeSerializer") {
        test("serialiserer korrekt") {
            val tidspunkt = LocalDateTime.of(1830, Month.MARCH, 30, 10, 41, 42, 444_444_444)

            val json = tidspunkt.toJsonStr(LocalDateTimeSerializer)

            json shouldBe "\"1830-03-30T10:41:42.444444444\""
        }

        test("deserialiserer korrekt") {
            val json = "\"1930-07-31T20:51:52.555555555\""

            val tidspunkt = json.fromJson(LocalDateTimeSerializer)

            tidspunkt shouldBe LocalDateTime.of(1930, Month.JULY, 31, 20, 51, 52, 555_555_555)
        }

        test("gir SerializationException ved deserialiseringsfeil") {
            shouldThrow<SerializationException> {
                "ikke et tidspunkt".fromJson(LocalDateTimeSerializer)
            }
        }
    }

    context("OffsetDateTimeSerializer") {
        val sevenHourOffset = ZoneOffset.ofHours(7)

        test("serialiserer korrekt") {
            val tidspunkt = OffsetDateTime.of(1907, 8, 12, 12, 23, 34, 555_555_555, sevenHourOffset)

            val json = tidspunkt.toJsonStr(OffsetDateTimeSerializer)

            json shouldBe "\"1907-08-12T12:23:34.555555555+07:00\""
        }

        test("deserialiserer korrekt") {
            val json = "\"1927-05-13T22:33:44.666666666+07:00\""

            val tidspunkt = json.fromJson(OffsetDateTimeSerializer)

            tidspunkt shouldBe OffsetDateTime.of(1927, 5, 13, 22, 33, 44, 666_666_666, sevenHourOffset)
        }

        test("gir SerializationException ved deserialiseringsfeil") {
            shouldThrow<SerializationException> {
                "ikke et tidspunkt".fromJson(OffsetDateTimeSerializer)
            }
        }
    }
})

package no.nav.helsearbeidsgiver.utils.json.serializer

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import no.nav.helsearbeidsgiver.utils.json.fromJson
import no.nav.helsearbeidsgiver.utils.json.toJsonStr
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.YearMonth

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
            val dato = LocalDate.of(1815, Month.FEBRUARY, 12)

            val json = dato.toJsonStr(LocalDateSerializer)

            json shouldBe "\"1815-02-12\""
        }

        test("deserialiserer korrekt") {
            val json = "\"1915-06-22\""

            val dato = json.fromJson(LocalDateSerializer)

            dato shouldBe LocalDate.of(1915, Month.JUNE, 22)
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
})

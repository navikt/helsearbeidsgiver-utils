package no.nav.helsearbeidsgiver.utils.json.serializer

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.YearMonth

object YearMonthSerializer : AsStringSerializer<YearMonth>(
    serialName = "helsearbeidsgiver.kotlinx.YearMonthSerializer",
    parse = YearMonth::parse
)

object LocalDateSerializer : AsStringSerializer<LocalDate>(
    serialName = "helsearbeidsgiver.kotlinx.LocalDateSerializer",
    parse = LocalDate::parse
)

object LocalDateTimeSerializer : AsStringSerializer<LocalDateTime>(
    serialName = "helsearbeidsgiver.kotlinx.LocalDateTimeSerializer",
    parse = LocalDateTime::parse
)

object OffsetDateTimeSerializer : AsStringSerializer<OffsetDateTime>(
    serialName = "helsearbeidsgiver.kotlinx.OffsetDateTimeSerializer",
    parse = OffsetDateTime::parse
)

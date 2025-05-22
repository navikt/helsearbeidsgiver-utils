package no.nav.helsearbeidsgiver.utils.date

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val norskDatoFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val zoneIdOslo: ZoneId = ZoneId.of("Europe/Oslo")

fun LocalDate.tilNorskFormat(): String = format(norskDatoFormat)

fun LocalDateTime.toOffsetDateTimeOslo(): OffsetDateTime = atZone(zoneIdOslo).toOffsetDateTime()

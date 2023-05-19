package no.nav.helsearbeidsgiver.utils.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import no.nav.helsearbeidsgiver.utils.json.serializer.GenericObjectSerializer
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateTimeSerializer
import no.nav.helsearbeidsgiver.utils.json.serializer.UuidSerializer
import no.nav.helsearbeidsgiver.utils.json.serializer.YearMonthSerializer
import no.nav.helsearbeidsgiver.utils.json.serializer.list
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.util.UUID

fun <T> T.toJson(serializer: KSerializer<T>): JsonElement =
    Json.encodeToJsonElement(serializer, this)

fun <T> T.toJsonStr(serializer: KSerializer<T>): String =
    toJson(serializer).toString()

fun <T> List<T>.toJson(elementSerializer: KSerializer<T>): JsonElement =
    toJson(
        elementSerializer.list()
    )

fun String.toJson(): JsonElement =
    toJson(String.serializer())

fun Map<String, JsonElement>.toJson(): JsonElement =
    toJson(GenericObjectSerializer)

fun YearMonth.toJson(): JsonElement =
    toJson(YearMonthSerializer)

fun LocalDate.toJson(): JsonElement =
    toJson(LocalDateSerializer)

fun LocalDateTime.toJson(): JsonElement =
    toJson(LocalDateTimeSerializer)

fun UUID.toJson(): JsonElement =
    toJson(UuidSerializer)

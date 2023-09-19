package no.nav.helsearbeidsgiver.utils.json

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

private val jsonPrettyPrint = Json { prettyPrint = true }

fun JsonElement.toPretty(): String =
    jsonPrettyPrint.encodeToString(JsonElement.serializer(), this)

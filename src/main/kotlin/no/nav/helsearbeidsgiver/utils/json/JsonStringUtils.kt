package no.nav.helsearbeidsgiver.utils.json

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

private val jsonPrettyPrint = Json { prettyPrint = true }

private val jsonWhitespaceRegex = Regex("""("(?:\\"|[^"])*")|\s""")

fun JsonElement.toPretty(): String =
    jsonPrettyPrint.encodeToString(JsonElement.serializer(), this)

/** NB! I hovedsak brukt i tester. Bruk i prod med omhu. */
fun String.removeJsonWhitespace(): String =
    replace(jsonWhitespaceRegex, "$1")

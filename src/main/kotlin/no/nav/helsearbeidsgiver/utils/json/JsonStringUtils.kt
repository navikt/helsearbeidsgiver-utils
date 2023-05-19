package no.nav.helsearbeidsgiver.utils.json

private val jsonWhitespaceRegex = Regex("""("(?:\\"|[^"])*")|\s""")

/** NB! I hovedsak brukt i tester. Bruk i prod med omhu. */
fun String.removeJsonWhitespace(): String =
    replace(jsonWhitespaceRegex, "$1")

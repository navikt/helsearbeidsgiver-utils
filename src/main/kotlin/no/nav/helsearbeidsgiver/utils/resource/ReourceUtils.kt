package no.nav.helsearbeidsgiver.utils.resource

fun String.loadFromResources(): String {
    return ClassLoader.getSystemResource(this).readText()
}

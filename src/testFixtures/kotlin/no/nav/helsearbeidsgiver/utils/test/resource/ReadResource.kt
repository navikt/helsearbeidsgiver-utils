package no.nav.helsearbeidsgiver.utils.test.resource

fun String.readResource(): String =
    ClassLoader.getSystemResource(this)?.readText()!!

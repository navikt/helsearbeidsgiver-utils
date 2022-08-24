package no.nav.helsearbeidsgiver.utils.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun <T : Any> T.logger(): Logger =
    when (this) {
        is String -> this.let(LoggerFactory::getLogger)
        else -> this.javaClass.let(LoggerFactory::getLogger)
    }

package no.nav.helsearbeidsgiver.utils.env

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
annotation class EnvironmentValue(val name: String)
package no.nav.helsearbeidsgiver.utils.env

class DemoSettings {
    @EnvironmentValue(name = "JAVA_HOME")
    var javaHome: String? = null
}
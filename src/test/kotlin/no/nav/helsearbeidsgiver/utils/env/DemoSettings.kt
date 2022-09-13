package no.nav.helsearbeidsgiver.utils.env

class DemoSettings {
    @EnvironmentValue(name = "PATH")
    var javaHome: String? = null
}

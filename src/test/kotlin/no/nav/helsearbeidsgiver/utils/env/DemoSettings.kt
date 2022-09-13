package no.nav.helsearbeidsgiver.utils.env

class DemoSettings {
    @EnvironmentValue(name = "FAKE_ENV")
    var javaHome: String? = null
}

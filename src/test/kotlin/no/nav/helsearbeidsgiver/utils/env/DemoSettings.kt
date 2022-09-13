package no.nav.helsearbeidsgiver.utils.env

class DemoSettings : Environment() {
    @EnvironmentValue(name = "FAKE_ENV")
    var javaHome: String? = null
}

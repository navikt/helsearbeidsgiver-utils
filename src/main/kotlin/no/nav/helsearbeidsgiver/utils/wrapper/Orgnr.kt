package no.nav.helsearbeidsgiver.utils.wrapper

import kotlinx.serialization.Serializable

/** Sjekker at streng er riktig lengde og kun består av siffer. */
private val orgnrRgx = Regex("\\d{9}")

private val sifferVekter = listOf(3, 2, 7, 6, 5, 4, 3, 2)

@Serializable
@JvmInline
value class Orgnr(val verdi: String) {
    init {
        require(erGyldig(verdi)) { "Ugyldig organisasjonsnummer." }
    }

    override fun toString(): String =
        verdi

    companion object {
        /** Les [her](https://www.brreg.no/om-oss/registrene-vare/om-enhetsregisteret/organisasjonsnummeret/) for forklaring av regler. */
        fun erGyldig(orgnr: String): Boolean =
            if (orgnr.matches(orgnrRgx)) {
                val orgnrSiffer = orgnr.toList().map(Char::digitToInt)

                val sjekksum = sjekksum(orgnrSiffer, sifferVekter)

                sjekksum != 10 &&
                    sjekksum == orgnrSiffer.last()
            } else {
                false
            }
    }
}

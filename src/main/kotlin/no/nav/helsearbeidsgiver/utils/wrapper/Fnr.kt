package no.nav.helsearbeidsgiver.utils.wrapper

import kotlinx.serialization.Serializable

/** Sjekker at streng er riktig lengde, kun består av siffer, og at 6 første siffer er gyldig dato. */
private val fnrRgx =
    Regex(
        "(?:[04][1-9]|[1256]\\d|[37][01])" + // to første siffer er gyldig dag (+40 for D-nummer)
            "(?:[048][1-9]|[159][012])" + // to neste siffer er gyldig måned, med støtte for testpersoner (+40 for NAV, +80 for TestNorge)
            "\\d{7}", // resten er tall
    )

internal val fnrSifferVekter1 = listOf(3, 7, 6, 1, 8, 9, 4, 5, 2)
internal val fnrSifferVekter2 = listOf(5, 4, 3, 2, 7, 6, 5, 4, 3, 2)

@Serializable
@JvmInline
value class Fnr(
    val verdi: String,
) {
    init {
        require(erGyldig(verdi)) { "Ugyldig fødsels- eller d-nummer." }
    }

    override fun toString(): String = verdi

    companion object {
        /** Les [her](https://lovdata.no/dokument/SF/forskrift/2017-07-14-1201/KAPITTEL_2#%C2%A72-2-1) for forklaring av regler. */
        fun erGyldig(fnr: String): Boolean =
            if (fnr.matches(fnrRgx)) {
                val fnrSiffer = fnr.toList().map(Char::digitToInt)

                val sjekksum1 = sjekksum(fnrSiffer, fnrSifferVekter1)
                val sjekksum2 = sjekksum(fnrSiffer, fnrSifferVekter2)

                10 !in listOf(sjekksum1, sjekksum2) &&
                    sjekksum1 == fnrSiffer[9] &&
                    sjekksum2 == fnrSiffer[10]
            } else {
                false
            }
    }
}

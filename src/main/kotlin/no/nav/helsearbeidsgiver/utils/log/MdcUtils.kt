package no.nav.helsearbeidsgiver.utils.log

import org.slf4j.MDC
import java.util.UUID
import kotlin.random.Random
import kotlin.random.nextUInt

// Inliner wrapperfunksjoner for å støtte "non-local returns". Se https://kotlinlang.org/docs/inline-functions.html for mer info.
object MdcUtils {
    @PublishedApi
    internal object Keys {
        const val CALL_ID = "callId"
    }

    /** Henter `callId` fra MDC eller lager ny `callId` dersom ingen finnes. */
    fun getCallId(): String =
        MDC.get(Keys.CALL_ID)
            ?: newCallId()

    inline fun <T> withCallId(fn: () -> T): T =
        withLogField(
            Keys.CALL_ID to newCallId(),
            fn,
        )

    inline fun <T> withCallIdAsUuid(fn: () -> T): T =
        withLogField(
            Keys.CALL_ID to uuid4(),
            fn,
        )

    /**
     * Ikke bruk. Er kun `internal` pga. krav for bruk i `public inline`-funksjoner.
     *
     * Se alternativer under.
     * @see withCallId
     * @see withCallIdAsUuid
     * */
    @PublishedApi
    internal inline fun <T> withLogField(logField: Pair<String, String>, fn: () -> T): T {
        val (key, value) = logField
        MDC.put(key, value)
        return try {
            fn()
        } finally {
            MDC.remove(key)
        }
    }

    @PublishedApi
    internal fun newCallId(): String =
        "CallId_${Random.nextUInt()}_${System.currentTimeMillis()}"

    @PublishedApi
    internal fun uuid4(): String =
        UUID.randomUUID().toString()
}

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

    inline fun <T> withCallId(block: () -> T): T =
        withLogFields(
            Keys.CALL_ID to newCallId(),
            block = block,
        )

    inline fun <T> withCallIdAsUuid(block: () -> T): T =
        withLogFields(
            Keys.CALL_ID to uuid4(),
            block = block,
        )

    inline fun <T> withLogFields(
        vararg logFields: Pair<String, String>,
        block: () -> T,
    ): T {
        val backup =
            logFields
                .map { (key, _) ->
                    key to MDC.get(key)
                }.mapValuesNotNull()

        logFields.forEach { (key, value) ->
            MDC.put(key, value)
        }

        return try {
            block()
        } finally {
            logFields.forEach { (key, _) ->
                MDC.remove(key)
            }

            backup.forEach { (key, value) ->
                MDC.put(key, value)
            }
        }
    }

    @PublishedApi
    internal fun newCallId(): String = "CallId_${Random.nextUInt()}_${System.currentTimeMillis()}"

    @PublishedApi
    internal fun uuid4(): String = UUID.randomUUID().toString()

    @PublishedApi
    internal fun List<Pair<String, String?>>.mapValuesNotNull(): List<Pair<String, String>> =
        mapNotNull { (key, value) ->
            if (value == null) {
                null
            } else {
                key to value
            }
        }
}

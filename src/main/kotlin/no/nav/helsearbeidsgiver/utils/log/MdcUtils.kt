package no.nav.helsearbeidsgiver.utils.log

import org.slf4j.MDC
import java.util.UUID
import kotlin.random.Random
import kotlin.random.nextUInt

object MdcUtils {
    private object Keys {
        const val CALL_ID = "callId"
    }

    fun withCallId(fn: () -> Unit): Unit =
        withLogField(
            Keys.CALL_ID to newCallId(),
            fn,
        )

    fun withCallIdAsUuid(fn: () -> Unit): Unit =
        withLogField(
            Keys.CALL_ID to uuid4(),
            fn,
        )

    fun getCallId(): String =
        MDC.get(Keys.CALL_ID)
            ?: newCallId()

    private fun withLogField(logField: Pair<String, String>, fn: () -> Unit) {
        val (key, value) = logField
        MDC.put(key, value)
        try {
            fn()
        } finally {
            MDC.remove(key)
        }
    }
}

private fun newCallId(): String =
    "CallId_${Random.nextUInt()}_${System.currentTimeMillis()}"

private fun uuid4(): String =
    UUID.randomUUID().toString()

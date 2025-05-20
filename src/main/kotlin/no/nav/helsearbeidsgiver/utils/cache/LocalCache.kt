package no.nav.helsearbeidsgiver.utils.cache

import no.nav.helsearbeidsgiver.utils.collection.mapValuesNotNull
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class LocalCache<T>(
    private val config: Config,
) {
    data class Config(
        val entryDuration: Duration,
        val maxEntries: Int,
    )

    init {
        require(config.maxEntries > 0) { "Parameter `maxEntries` must be greater than 0, but was ${config.maxEntries}." }
    }

    private val cache = mutableMapOf<String, Entry<T>>()

    suspend fun getOrPut(
        key: String,
        default: suspend () -> T,
    ): T =
        getNotExpired(key)
            ?: put(key, default())

    /** Parameter in `default`-function is keys which were not found in cache. */
    suspend fun getOrPut(
        keys: Set<String>,
        default: suspend (Set<String>) -> Map<String, T>,
    ): Map<String, T> {
        val inCache =
            keys
                .associateWith(::getNotExpired)
                .mapValuesNotNull { it }

        val notInCacheKeys = keys - inCache.keys
        val notInCache =
            if (notInCacheKeys.isNotEmpty()) {
                default(notInCacheKeys)
            } else {
                emptyMap()
            }

        notInCache.forEach(::put)

        return inCache + notInCache
    }

    private fun getNotExpired(key: String): T? =
        cache[key]
            ?.takeIf { it.isNotExpired() }
            ?.value

    private fun put(
        key: String,
        value: T,
    ): T {
        while (cache.size >= config.maxEntries) {
            removeEntryExpiringEarliest()
        }

        cache[key] =
            Entry(
                value,
                LocalDateTime.now().plus(config.entryDuration.toJavaDuration()),
            )

        return value
    }

    private fun removeEntryExpiringEarliest() {
        cache
            .minByOrNull { it.value.expiresAt }
            ?.also { cache.remove(it.key) }
    }
}

private data class Entry<T>(
    val value: T,
    val expiresAt: LocalDateTime,
) {
    fun isNotExpired(): Boolean = expiresAt.isAfter(LocalDateTime.now())
}

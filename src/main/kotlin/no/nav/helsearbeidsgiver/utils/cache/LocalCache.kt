package no.nav.helsearbeidsgiver.utils.cache

import no.nav.helsearbeidsgiver.utils.collection.mapValuesNotNull
import no.nav.helsearbeidsgiver.utils.log.logger
import no.nav.helsearbeidsgiver.utils.log.sikkerLogger
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class LocalCache<T : Any>(
    private val config: Config,
) {
    data class Config(
        val entryDuration: Duration,
        val maxEntries: Int,
    )

    init {
        require(config.maxEntries > 0) { "Parameter `maxEntries` must be greater than 0, but was ${config.maxEntries}." }
    }

    private val logger = logger()
    private val sikkerLogger = sikkerLogger()
    private val cache = mutableMapOf<String, Entry<T>>()

    suspend fun getOrPut(
        key: String,
        default: suspend () -> T,
    ): T =
        getNotExpired(key)
            ?: default().let { put(key, it) }

    /** Caches the return value from [default] only if that value is non-null. */
    suspend fun getOrPutOrNull(
        key: String,
        default: suspend () -> T?,
    ): T? =
        getNotExpired(key)
            ?: default()?.let { put(key, it) }

    /** Parameter in [default] is keys which were not found in cache. */
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
        if (cache.size >= config.maxEntries) {
            removeExpiredEntries()
            removeExcessEntries()
        }

        cache[key] =
            Entry(
                value,
                LocalDateTime.now().plus(config.entryDuration.toJavaDuration()),
            )

        return value
    }

    private fun removeExpiredEntries() {
        cache
            .filterNot { it.value.isNotExpired() }
            .forEach { cache.remove(it.key) }
    }

    private fun removeExcessEntries() {
        val excess = 1 + cache.size - config.maxEntries
        if (excess > 0) {
            "Cache fjerner ikke-utgåtte elementer for å få plass til nye. Vurder å øke maks antall elementer.".also {
                logger.warn(it)
                sikkerLogger.warn(it)
            }
            cache
                .toList()
                .sortedBy { it.second.expiresAt }
                .take(excess)
                .forEach { cache.remove(it.first) }
        }
    }
}

private data class Entry<T>(
    val value: T,
    val expiresAt: LocalDateTime,
) {
    fun isNotExpired(): Boolean = expiresAt.isAfter(LocalDateTime.now())
}

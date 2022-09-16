package no.nav.helsearbeidsgiver.utils.cache

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeExactly
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

class LocalCacheTest : StringSpec({
    lateinit var cache: LocalCache<Int>

    beforeTest {
        cache = LocalCache(1.hours, 2)
    }

    "cache kan ikke opprettes med maxEntries-argument som er mindre eller lik 0" {
        shouldThrowExactly<IllegalArgumentException> {
            LocalCache<Unit>(Duration.ZERO, 0)
        }
    }

    "ved ikke-tom cache så hentes resultat fra cache" {
        val key = "key-1"
        val value = 1

        // Insert element i cache
        cache.get(key) { value }

        cache.get(key, ::throwError)
            .shouldBeExactly(value)
    }

    "ved tom cache så insertes resultat fra default-funksjon" {
        val value = 1

        var defaultWasCalled = false

        cache.get("key-1") {
            defaultWasCalled = true
            value
        }
            .shouldBeExactly(value)

        defaultWasCalled.shouldBeTrue()
    }

    "ved utgått cache-element så erstattes element med resultat fra default-funksjon" {
        val duration = Duration.ZERO
        cache = LocalCache(duration, 2)

        val key = "key-1"
        val valueExpired = 1
        val value = 2

        // Insert element som skal utgå
        cache.get(key) { valueExpired }

        // Vent til element utgår
        delay(duration + 10.milliseconds)

        // Sjekk at elementet har utgått og må erstattes
        var defaultWasCalled = false

        cache.get(key) {
            defaultWasCalled = true
            value
        }
            .shouldBeExactly(value)

        defaultWasCalled.shouldBeTrue()
    }

    "ved overstidelse av maks antall cachede elementer kastes første (tidligste) element ut" {
        val keys = listOf("key-0", "key-1", "key-2")
        val values = (0..3).toList()

        // Insert første og andre element (cachen blir full)
        (0..1).map {
            cache.get(keys[it]) { values[it] }
        }

        // Sjekk at første/tidligste element er cachet
        cache.get(keys[0], ::throwError)
            .shouldBeExactly(values[0])

        // Insert nytt element, som betyr at første element blir kastet ut av cachen
        cache.get(keys[2]) { values[2] }

        // Sjekk at andre element fremdeles er cachet og ikke kastet ut
        cache.get(keys[1], ::throwError)
            .shouldBeExactly(values[1])

        // Sjekk at tredje element er cachet
        cache.get(keys[2], ::throwError)
            .shouldBeExactly(values[2])

        // Sjekk at første element har blitt kastet ut og dermed må insertes på ny
        var defaultWasCalled = false

        cache.get(keys[0]) {
            defaultWasCalled = true
            values[3]
        }
            .shouldBeExactly(values[3])

        defaultWasCalled.shouldBeTrue()
    }

    "ved ikke-null cache hentes element fra cache" {
        val key = "key-1"
        val value = 1

        // Insert element i cache
        cache.get(key) { value }

        cache.getIfCacheNotNull(key, ::throwError)
            .shouldBeExactly(value)
    }

    "ved null-cache brukes resultat fra default-funksjon" {
        val nullCache: LocalCache<Int>? = null

        val value = 1

        var defaultWasCalled = false

        nullCache.getIfCacheNotNull("key-1") {
            defaultWasCalled = true
            value
        }
            .shouldBeExactly(value)

        defaultWasCalled.shouldBeTrue()
    }
})

private fun throwError(): Nothing =
    throw AssertionError("Denne funksjonen skal ikke kalles.")

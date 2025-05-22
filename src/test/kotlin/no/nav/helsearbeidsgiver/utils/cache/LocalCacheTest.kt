package no.nav.helsearbeidsgiver.utils.cache

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.maps.shouldContainExactly
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

class LocalCacheTest :
    FunSpec({
        lateinit var cache: LocalCache<Int>

        beforeTest {
            cache = LocalCache(LocalCache.Config(1.hours, 4))
        }

        test("cache kan ikke opprettes med maxEntries-argument som er mindre eller lik 0") {
            shouldThrowExactly<IllegalArgumentException> {
                LocalCache<Unit>(LocalCache.Config(Duration.ZERO, 0))
            }
        }

        context("getOrPut-single") {
            test("ved ikke-tom cache så hentes resultat fra cache") {
                val key = "key-1"
                val value = 1

                // Insert element i cache
                cache.getOrPut(key) { value }

                cache
                    .getOrPut(key, ::throwError)
                    .shouldBeExactly(value)
            }

            test("ved tom cache så insertes resultat fra default-funksjon") {
                val value = 1

                var defaultWasCalled = false

                cache
                    .getOrPut("key-1") {
                        defaultWasCalled = true
                        value
                    }.shouldBeExactly(value)

                defaultWasCalled.shouldBeTrue()
            }

            test("ved utgått cache-element så erstattes element med resultat fra default-funksjon") {
                cache = LocalCache(LocalCache.Config(Duration.ZERO, 4))

                val key = "key-1"
                val valueExpired = 1
                val value = 2

                // Insert element som skal utgå
                cache.getOrPut(key) { valueExpired }

                // Vent til element utgår
                delay(10.milliseconds)

                // Sjekk at elementet har utgått og må erstattes
                var defaultWasCalled = false

                cache
                    .getOrPut(key) {
                        defaultWasCalled = true
                        value
                    }.shouldBeExactly(value)

                defaultWasCalled.shouldBeTrue()
            }

            test("ved overtredelse av maks antall cachede elementer kastes første (tidligste) element ut") {
                val keys = listOf("key-0", "key-1", "key-2", "key-3", "key-4")
                val values = (0..4).toList()

                // Insert fire første elementer (cachen blir full)
                (0..3).map {
                    cache.getOrPut(keys[it]) { values[it] }
                }

                // Sjekk at første/tidligste element er cachet
                cache
                    .getOrPut(keys[0], ::throwError)
                    .shouldBeExactly(values[0])

                // Insert nytt element, som betyr at første element blir kastet ut av cachen
                cache.getOrPut(keys[4]) { values[4] }

                // Sjekk at andre element fremdeles er cachet og ikke kastet ut
                cache
                    .getOrPut(keys[1], ::throwError)
                    .shouldBeExactly(values[1])

                // Sjekk at siste element er cachet
                cache
                    .getOrPut(keys[4], ::throwError)
                    .shouldBeExactly(values[4])

                // Sjekk at første element har blitt kastet ut og dermed må insertes på ny
                var defaultWasCalled = false

                cache
                    .getOrPut(keys[0]) {
                        defaultWasCalled = true
                        5
                    }.shouldBeExactly(5)

                defaultWasCalled.shouldBeTrue()
            }
        }

        context("getOrPut-multiple") {
            test("ved ikke-tom cache så hentes resultat fra cache") {
                val inCache =
                    mapOf(
                        "key-1" to 1,
                        "key-2" to 2,
                    )

                // Insert elementer i cache
                cache.getOrPut(inCache.keys) { inCache }

                cache
                    .getOrPut(inCache.keys) { throwError() }
                    .shouldContainExactly(inCache)
            }

            test("ved tom cache så insertes resultat fra default-funksjon") {
                val notInCache =
                    mapOf(
                        "key-1" to 1,
                        "key-2" to 2,
                    )

                var defaultWasCalled = false

                cache
                    .getOrPut(notInCache.keys) {
                        defaultWasCalled = true
                        notInCache
                    }.shouldContainExactly(notInCache)

                defaultWasCalled.shouldBeTrue()
            }

            test("ved manglende cache-element så får default-funksjon manglende nøkler som argument") {
                val inCache =
                    mapOf(
                        "key-1" to 1,
                        "key-2" to 2,
                    )
                val notInCache =
                    mapOf(
                        "key-3" to 3,
                        "key-4" to 4,
                    )

                var keysNotInCache = emptySet<String>()

                // Insert elementer i cache
                cache.getOrPut(inCache.keys) { inCache }

                cache
                    .getOrPut(inCache.keys + notInCache.keys) { keysNotFound ->
                        keysNotInCache = keysNotFound
                        notInCache
                    }.shouldContainExactly(inCache + notInCache)

                keysNotInCache shouldContainExactly notInCache.keys
            }

            test("ved manglende cache-element så overskrives eksisterende element av resultat fra default-funksjon") {
                val inCache =
                    mapOf(
                        "key-1" to 1,
                    )
                val overwriteCache =
                    mapOf(
                        "key-1" to 111,
                        "key-2" to 222,
                    )

                // Insert elementer i cache
                cache.getOrPut(inCache.keys) { inCache }

                // Overskriv elementer
                cache.getOrPut(overwriteCache.keys) { overwriteCache }

                // Les og valider elementer
                cache
                    .getOrPut(overwriteCache.keys) { throwError() }
                    .shouldContainExactly(overwriteCache)
            }

            test("ved manglende cache-element så tåler default-funksjon tomt resultat") {
                val inCache =
                    mapOf(
                        "key-1" to 1,
                        "key-2" to 2,
                    )
                val keysNotInCache = setOf("key-3", "key-4")

                // Insert elementer i cache
                cache.getOrPut(inCache.keys) { inCache }

                cache
                    .getOrPut(inCache.keys + keysNotInCache) {
                        emptyMap()
                    }.shouldContainExactly(inCache)
            }

            test("ved manglende cache-element så tåler default-funksjon resultat med subset av etterspurte nøkler") {
                val firstElement =
                    mapOf(
                        "key-1" to 1,
                    )
                val secondElement =
                    mapOf(
                        "key-2" to 2,
                    )

                // Kun insert key-1 i cache
                cache
                    .getOrPut(firstElement.keys + secondElement.keys) {
                        firstElement
                    }.shouldContainExactly(firstElement)

                var keysNotInCache = emptySet<String>()

                // Insert key-2 i cache
                cache
                    .getOrPut(firstElement.keys + secondElement.keys) { keysNotFound ->
                        keysNotInCache = keysNotFound
                        secondElement
                    }.shouldContainExactly(firstElement + secondElement)

                keysNotInCache shouldContainExactly secondElement.keys
            }

            test("ved manglende cache-element så tåler default-funksjon resultat med ikke-etterspurte nøkler") {
                val inCache =
                    mapOf(
                        "key-1" to 1,
                        "key-2" to 2,
                    )
                val keysNotInCache = setOf("key-3", "key-4")

                val valuesNotAskedFor = mapOf("key-5" to 5)

                // Insert elementer i cache
                cache.getOrPut(inCache.keys) { inCache }

                cache
                    .getOrPut(inCache.keys + keysNotInCache) {
                        valuesNotAskedFor
                    }.shouldContainExactly(inCache + valuesNotAskedFor)
            }

            test("ved utgått cache-element så erstattes element med resultat fra default-funksjon") {
                cache = LocalCache(LocalCache.Config(Duration.ZERO, 4))

                val inCacheExpired =
                    mapOf(
                        "key-1" to 1,
                        "key-2" to 2,
                    )
                val notInCache =
                    mapOf(
                        "key-1" to 111,
                        "key-2" to 222,
                    )

                inCacheExpired.keys shouldContainExactly notInCache.keys

                // Insert element som skal utgå
                cache.getOrPut(inCacheExpired.keys) { inCacheExpired }

                // Vent til element utgår
                delay(10.milliseconds)

                // Sjekk at elementet har utgått og må erstattes
                var defaultWasCalled = false

                cache
                    .getOrPut(notInCache.keys) {
                        defaultWasCalled = true
                        notInCache
                    }.shouldContainExactly(notInCache)

                defaultWasCalled.shouldBeTrue()
            }

            test("ved overtredelse av maks antall cachede elementer kastes første (tidligste) element ut") {
                val firstKey = "key-1"
                val inCache =
                    mapOf(
                        firstKey to 1,
                        "key-2" to 2,
                        "key-3" to 3,
                    )
                val newToCache =
                    mapOf(
                        "key-4" to 4,
                        "key-5" to 5,
                    )

                // Insert tre første element (cachen blir nesten full)
                cache.getOrPut(inCache.keys) { inCache }

                // Sjekk at første/tidligste element er cachet
                cache
                    .getOrPut(setOf(firstKey)) { throwError() }
                    .shouldContainExactly(
                        mapOf(
                            inCache.toList().first(),
                        ),
                    )

                // Insert to nye elementer, som betyr at første element blir kastet ut av cachen
                cache.getOrPut(newToCache.keys) { newToCache }

                // Sjekk at andre og tredje element fremdeles er cachet og ikke kastet ut
                cache
                    .getOrPut(inCache.minus(firstKey).keys) { throwError() }
                    .shouldContainExactly(inCache.minus(firstKey))

                // Sjekk at fjerde og femte element er cachet
                cache
                    .getOrPut(newToCache.keys) { throwError() }
                    .shouldContainExactly(newToCache)

                // Sjekk at første element har blitt kastet ut og dermed må insertes på ny
                var defaultWasCalled = false

                cache
                    .getOrPut(setOf(firstKey)) {
                        defaultWasCalled = true
                        mapOf(firstKey to 6)
                    }.shouldContainExactly(mapOf(firstKey to 6))

                defaultWasCalled.shouldBeTrue()
            }
        }
    })

private fun throwError(): Nothing = throw AssertionError("Denne funksjonen skal ikke kalles.")

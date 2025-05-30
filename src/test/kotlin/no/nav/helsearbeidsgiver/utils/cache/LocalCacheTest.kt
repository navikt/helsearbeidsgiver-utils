package no.nav.helsearbeidsgiver.utils.cache

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.maps.shouldContainExactly
import io.mockk.every
import no.nav.helsearbeidsgiver.utils.test.date.august
import no.nav.helsearbeidsgiver.utils.test.date.juli
import no.nav.helsearbeidsgiver.utils.test.date.kl
import no.nav.helsearbeidsgiver.utils.test.mock.mockStatic
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.times
import kotlin.time.toJavaDuration

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
                mockStatic(LocalDateTime::class) {
                    val origoDateTime = 7.juli.kl(12, 0, 0, 0)
                    val key = "key-1"
                    val valueExpired = 1
                    val value = 2

                    // Insert element som skal utgå
                    every { LocalDateTime.now() } returns origoDateTime

                    cache.getOrPut(key) {
                        valueExpired
                    }

                    // Sjekk at elementet har utgått og må erstattes
                    every { LocalDateTime.now() } returns origoDateTime.plus(2.hours)

                    var defaultWasCalled = false

                    cache
                        .getOrPut(key) {
                            defaultWasCalled = true
                            value
                        }.shouldBeExactly(value)

                    defaultWasCalled.shouldBeTrue()
                }
            }

            test("ved overtredelse av maks antall elementer hvor ingen utgåtte så kastes eldste elementer ut") {
                mockStatic(LocalDateTime::class) {
                    val origoDateTime = 18.juli.kl(12, 0, 0, 0)
                    val values = (0..5).toList()
                    val keys = values.map { "key-$it" }

                    // Insert fire elementer (cachen blir full)
                    (0..3).forEach {
                        every { LocalDateTime.now() } returns origoDateTime.plus(it * 10.minutes)
                        cache.getOrPut(keys[it]) { values[it] }
                    }

                    // Sjekk at elementer er cachet
                    (0..3).forEach {
                        cache
                            .getOrPut(keys[it], ::throwError)
                            .shouldBeExactly(values[it])
                    }

                    // Insert to nye elementer, som betyr at to første/eldste (ikke-utgåtte) elementer blir kastet ut av cachen
                    (4..5).forEach {
                        every { LocalDateTime.now() } returns origoDateTime.plus(it * 10.minutes)
                        cache.getOrPut(keys[it]) { values[it] }
                    }

                    // Sjekk at tredje element og nye fremdeles er cachet og ikke kastet ut
                    (2..5).forEach {
                        cache
                            .getOrPut(keys[it], ::throwError)
                            .shouldBeExactly(values[it])
                    }

                    // Sjekk at andre element har blitt kastet ut og dermed må insertes på ny
                    var defaultWasCalled = false

                    cache
                        .getOrPut(keys[1]) {
                            defaultWasCalled = true
                            111
                        }.shouldBeExactly(111)

                    defaultWasCalled.shouldBeTrue()
                }
            }

            test("ved overtredelse av maks antall elementer hvor mange utgåtte så kastes utgåtte elementer ut") {
                mockStatic(LocalDateTime::class) {
                    val origoDateTime = 28.juli.kl(12, 0, 0, 0)
                    val values = (0..4).toList()
                    val keys = values.map { "key-$it" }

                    // Insert fire elementer (cachen blir full)
                    (0..3).forEach {
                        every { LocalDateTime.now() } returns origoDateTime.plus(it * 10.minutes)
                        cache.getOrPut(keys[it]) { values[it] }
                    }

                    // Sjekk at elementer er cachet
                    (0..3).forEach {
                        cache
                            .getOrPut(keys[it], ::throwError)
                            .shouldBeExactly(values[it])
                    }

                    // Insert nytt element, som betyr at to utgåtte elementer blir kastet ut av cachen
                    every { LocalDateTime.now() } returns origoDateTime.plus(75.minutes)
                    cache.getOrPut(keys[4]) { values[4] }

                    // Sjekk at ikke-utgåtte elementer fremdeles er cachet og ikke kastet ut
                    (2..4).forEach {
                        cache
                            .getOrPut(keys[it], ::throwError)
                            .shouldBeExactly(values[it])
                    }

                    // Sjekk at andre element har blitt kastet ut og dermed må insertes på ny
                    var defaultWasCalled = false

                    cache
                        .getOrPut(keys[1]) {
                            defaultWasCalled = true
                            111
                        }.shouldBeExactly(111)

                    defaultWasCalled.shouldBeTrue()
                }
            }

            test("ved overtredelse av maks antall elementer hvor få utgåtte så kastes utgåtte og deretter eldste elementer ut") {
                mockStatic(LocalDateTime::class) {
                    cache = LocalCache(LocalCache.Config(1.hours, 5))

                    val origoDateTime = 31.juli.kl(12, 0, 0, 0)
                    val values = (0..8).toList()
                    val keys = values.map { "key-$it" }

                    // Insert fem elementer (cachen blir full)
                    (0..4).forEach {
                        every { LocalDateTime.now() } returns origoDateTime.plus(it * 10.minutes)
                        cache.getOrPut(keys[it]) { values[it] }
                    }

                    // Sjekk at elementer er cachet
                    (0..4).forEach {
                        cache
                            .getOrPut(keys[it], ::throwError)
                            .shouldBeExactly(values[it])
                    }

                    // Insert fire nye elementer, som betyr at to utgåtte og to ikke-utgåtte elementer blir kastet ut av cachen
                    (5..8).forEach {
                        every { LocalDateTime.now() } returns origoDateTime.plus(75.minutes)
                        cache.getOrPut(keys[it]) { values[it] }
                    }

                    // Sjekk at femte element og nye fremdeles er cachet og ikke kastet ut
                    (4..8).forEach {
                        cache
                            .getOrPut(keys[it], ::throwError)
                            .shouldBeExactly(values[it])
                    }

                    // Sjekk at fjerde element har blitt kastet ut og dermed må insertes på ny
                    var defaultWasCalled = false

                    cache
                        .getOrPut(keys[3]) {
                            defaultWasCalled = true
                            333
                        }.shouldBeExactly(333)

                    defaultWasCalled.shouldBeTrue()
                }
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
                mockStatic(LocalDateTime::class) {
                    val origoDateTime = 5.august.kl(12, 0, 0, 0)
                    val inCacheExpired =
                        mapOf(
                            "key-1" to 1,
                            "key-2" to 2,
                        )

                    // Insert element som skal utgå
                    every { LocalDateTime.now() } returns origoDateTime
                    cache.getOrPut(inCacheExpired.keys) { inCacheExpired }

                    // Sjekk at elementer har utgått og må erstattes
                    every { LocalDateTime.now() } returns origoDateTime.plus(2.hours)

                    var keysNotInCache = emptySet<String>()
                    val replacementInCacheExpired = inCacheExpired.mapValues { it.value * 111 }

                    cache
                        .getOrPut(inCacheExpired.keys) { keysNotFound ->
                            keysNotInCache = keysNotFound
                            replacementInCacheExpired
                        }.shouldContainExactly(replacementInCacheExpired)

                    keysNotInCache shouldContainExactly inCacheExpired.keys
                }
            }

            test("ved overtredelse av maks antall elementer hvor ingen utgåtte så kastes eldste elementer ut") {
                mockStatic(LocalDateTime::class) {
                    val origoDateTime = 11.august.kl(12, 0, 0, 0)
                    val inCacheExcess =
                        mapOf(
                            "key-1" to 1,
                        )
                    val inCache =
                        mapOf(
                            "key-2" to 2,
                            "key-3" to 3,
                        )
                    val newToCache =
                        mapOf(
                            "key-4" to 4,
                            "key-5" to 5,
                        )

                    // Insert tre første element (cachen blir nesten full)
                    every { LocalDateTime.now() } returnsMany List(inCacheExcess.size + inCache.size) { origoDateTime.plus(it * 10.minutes) }

                    cache.getOrPut(inCacheExcess.keys + inCache.keys) { inCacheExcess + inCache }

                    // Sjekk at første/eldste element er cachet
                    cache
                        .getOrPut(inCacheExcess.keys) { throwError() }
                        .shouldContainExactly(inCacheExcess)

                    // Insert to nye elementer, som betyr at første/eldste element blir kastet ut av cachen
                    every { LocalDateTime.now() } returns origoDateTime.plus(30.minutes)

                    cache.getOrPut(newToCache.keys) { newToCache }

                    // Sjekk at andre og tredje element fremdeles er cachet og ikke kastet ut
                    cache
                        .getOrPut(inCache.keys) { throwError() }
                        .shouldContainExactly(inCache)

                    // Sjekk at nye elementer er cachet
                    cache
                        .getOrPut(newToCache.keys) { throwError() }
                        .shouldContainExactly(newToCache)

                    // Sjekk at første/eldste element har blitt kastet ut og dermed må insertes på ny
                    var keysNotInCache = emptySet<String>()
                    val replacementInCacheExcess = inCacheExcess.mapValues { it.value * 111 }

                    cache
                        .getOrPut(inCacheExcess.keys) { keysNotFound ->
                            keysNotInCache = keysNotFound
                            replacementInCacheExcess
                        }.shouldContainExactly(replacementInCacheExcess)

                    keysNotInCache shouldContainExactly inCacheExcess.keys
                }
            }

            test("ved overtredelse av maks antall elementer hvor mange utgåtte så kastes utgåtte elementer ut") {
                mockStatic(LocalDateTime::class) {
                    cache = LocalCache(LocalCache.Config(1.hours, 5))

                    val origoDateTime = 25.august.kl(12, 0, 0, 0)
                    val inCacheExpired =
                        mapOf(
                            "key-1" to 1,
                            "key-2" to 2,
                            "key-3" to 3,
                        )
                    val inCache =
                        mapOf(
                            "key-4" to 4,
                        )
                    val newToCache =
                        mapOf(
                            "key-5" to 5,
                            "key-6" to 6,
                        )

                    // Insert fire elementer (cachen blir nesten full)
                    every { LocalDateTime.now() } returnsMany List(inCacheExpired.size + inCache.size) { origoDateTime.plus(it * 10.minutes) }

                    cache.getOrPut(inCacheExpired.keys + inCache.keys) { inCacheExpired + inCache }

                    // Sjekk at elementer er cachet
                    cache
                        .getOrPut(inCacheExpired.keys + inCache.keys) { throwError() }
                        .shouldContainExactly(inCacheExpired + inCache)

                    // Insert to nye elementer, som betyr at tre utgåtte elementer blir kastet ut av cachen
                    every { LocalDateTime.now() } returns origoDateTime.plus(85.minutes)

                    cache.getOrPut(newToCache.keys) { newToCache }

                    // Sjekk at ikke-utgåtte elementer fremdeles er cachet og ikke kastet ut
                    cache
                        .getOrPut(inCache.keys) { throwError() }
                        .shouldContainExactly(inCache)

                    // Sjekk at nye elementer er cachet
                    cache
                        .getOrPut(newToCache.keys) { throwError() }
                        .shouldContainExactly(newToCache)

                    // Sjekk at utgåtte element har blitt kastet ut og dermed må insertes på ny
                    var keysNotInCache = emptySet<String>()
                    val replacementInCacheExpired = inCacheExpired.mapValues { it.value * 111 }

                    cache
                        .getOrPut(inCacheExpired.keys) { keysNotFound ->
                            keysNotInCache = keysNotFound
                            replacementInCacheExpired
                        }.shouldContainExactly(replacementInCacheExpired)

                    keysNotInCache shouldContainExactly inCacheExpired.keys
                }
            }

            test("ved overtredelse av maks antall elementer hvor få utgåtte så kastes utgåtte og deretter eldste elementer ut") {
                mockStatic(LocalDateTime::class) {
                    cache = LocalCache(LocalCache.Config(1.hours, 5))

                    val origoDateTime = 30.august.kl(12, 0, 0, 0)
                    val inCacheExpired =
                        mapOf(
                            "key-1" to 1,
                            "key-2" to 2,
                        )
                    val inCacheExcess =
                        mapOf(
                            "key-3" to 3,
                            "key-4" to 4,
                        )
                    val inCache =
                        mapOf(
                            "key-5" to 5,
                        )
                    val newToCache =
                        mapOf(
                            "key-6" to 6,
                            "key-7" to 7,
                            "key-8" to 8,
                            "key-9" to 9,
                        )

                    // Insert fem elementer (cachen blir full)
                    every { LocalDateTime.now() } returnsMany
                        List(inCacheExpired.size + inCacheExcess.size + inCache.size) {
                            origoDateTime.plus(it * 10.minutes)
                        }

                    cache.getOrPut(inCacheExpired.keys + inCacheExcess.keys + inCache.keys) { inCacheExpired + inCacheExcess + inCache }

                    // Sjekk at elementer er cachet
                    cache
                        .getOrPut(inCacheExpired.keys + inCacheExcess.keys + inCache.keys) { throwError() }
                        .shouldContainExactly(inCacheExpired + inCacheExcess + inCache)

                    // Insert fire nye elementer, som betyr at to utgåtte og to ikke-utgåtte elementer blir kastet ut av cachen
                    every { LocalDateTime.now() } returns origoDateTime.plus(75.minutes)

                    cache.getOrPut(newToCache.keys) { newToCache }

                    // Sjekk at ikke-utgåtte, yngste elementer fremdeles er cachet og ikke kastet ut
                    cache
                        .getOrPut(inCache.keys) { throwError() }
                        .shouldContainExactly(inCache)

                    // Sjekk at nye elementer er cachet
                    cache
                        .getOrPut(newToCache.keys) { throwError() }
                        .shouldContainExactly(newToCache)

                    // Sjekk at utgåtte og eldste elementer har blitt kastet ut og dermed må insertes på ny
                    var keysNotInCache = emptySet<String>()
                    val replacementInCacheExpiredAndExcess = (inCacheExpired + inCacheExcess).mapValues { it.value * 111 }

                    cache
                        .getOrPut(inCacheExpired.keys + inCacheExcess.keys) { keysNotFound ->
                            keysNotInCache = keysNotFound
                            replacementInCacheExpiredAndExcess
                        }.shouldContainExactly(replacementInCacheExpiredAndExcess)

                    keysNotInCache shouldContainExactly (inCacheExpired.keys + inCacheExcess.keys)
                }
            }
        }
    })

private fun throwError(): Nothing = throw AssertionError("Denne funksjonen skal ikke kalles.")

private fun LocalDateTime.plus(duration: Duration): LocalDateTime = plus(duration.toJavaDuration())

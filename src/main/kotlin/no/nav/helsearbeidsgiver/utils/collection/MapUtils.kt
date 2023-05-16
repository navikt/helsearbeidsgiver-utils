package no.nav.helsearbeidsgiver.utils.collection

fun <K, V, R : Any> Map<K, V>.mapKeysNotNull(transform: (K) -> R?): Map<R, V> =
    mapNotNull { (key, value) ->
        transform(key)
            ?.to(value)
    }
        .toMap()

fun <K, V, R : Any> Map<K, V>.mapValuesNotNull(transform: (V) -> R?): Map<K, R> =
    mapNotNull { (key, value) ->
        transform(value)
            ?.let { key to it }
    }
        .toMap()

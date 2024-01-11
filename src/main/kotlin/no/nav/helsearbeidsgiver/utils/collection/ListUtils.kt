package no.nav.helsearbeidsgiver.utils.collection

/** Elementer før [index] kommer i første liste, og elementer lik eller etter [index] kommer i andre liste. */
fun <T : Any> List<T>.splitOnIndex(index: Int): Pair<List<T>, List<T>> =
    withIndex()
        .partition { it.index < index }
        .let { (yieldedTrue, yieldedFalse) ->
            Pair(
                yieldedTrue.map { it.value },
                yieldedFalse.map { it.value }
            )
        }

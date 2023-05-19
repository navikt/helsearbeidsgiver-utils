package no.nav.helsearbeidsgiver.utils.pipe

fun <T : Any> T?.orDefault(default: T): T =
    this ?: default

fun Boolean.ifTrue(block: () -> Unit): Boolean =
    also { if (this) block() }

fun Boolean.ifFalse(block: () -> Unit): Boolean =
    also { if (!this) block() }

fun <A, B, R> Pair<A, B>.mapFirst(transform: (A) -> R): Pair<R, B> =
    Pair(
        first = transform(first),
        second = second
    )

fun <A, B, R> Pair<A, B>.mapSecond(transform: (B) -> R): Pair<A, R> =
    Pair(
        first = first,
        second = transform(second)
    )

package no.nav.helsearbeidsgiver.utils.json

import kotlinx.serialization.Serializable

@Serializable
data class Hobbit(
    val name: Name,
    val age: Int,
)

@Serializable
data class Name(
    val first: String,
    val last: String,
)

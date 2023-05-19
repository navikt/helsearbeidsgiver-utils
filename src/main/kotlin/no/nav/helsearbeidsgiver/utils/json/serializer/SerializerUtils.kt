package no.nav.helsearbeidsgiver.utils.json.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.SetSerializer

fun <T> KSerializer<T>.list(): KSerializer<List<T>> =
    ListSerializer(this)

fun <T> KSerializer<T>.set(): KSerializer<Set<T>> =
    SetSerializer(this)

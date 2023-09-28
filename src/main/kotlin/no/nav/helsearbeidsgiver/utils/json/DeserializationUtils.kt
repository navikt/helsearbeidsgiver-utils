package no.nav.helsearbeidsgiver.utils.json

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import no.nav.helsearbeidsgiver.utils.collection.mapKeysNotNull
import no.nav.helsearbeidsgiver.utils.json.serializer.GenericObjectSerializer

@OptIn(ExperimentalSerializationApi::class)
val jsonConfig = Json {
    ignoreUnknownKeys = true
    decodeEnumsCaseInsensitive = true
}

fun String.parseJson(): JsonElement =
    Json.parseToJsonElement(this)

fun <T> JsonElement.fromJson(serializer: KSerializer<T>): T =
    jsonConfig.decodeFromJsonElement(serializer, this)

fun <T> String.fromJson(serializer: KSerializer<T>): T =
    parseJson().fromJson(serializer)

fun <T : Any> JsonElement.fromJsonMap(keySerializer: KSerializer<T>): Map<T, JsonElement> =
    fromJson(
        MapSerializer(
            keySerializer,
            JsonElement.serializer()
        )
    )

fun <T : Any> JsonElement.fromJsonMapFiltered(keySerializer: KSerializer<T>): Map<T, JsonElement> =
    fromJson(GenericObjectSerializer)
        .mapKeysNotNull {
            tryOrNull {
                "\"$it\"".fromJson(keySerializer)
            }
        }

internal fun <T : Any> tryOrNull(block: () -> T): T? =
    runCatching(block).getOrNull()

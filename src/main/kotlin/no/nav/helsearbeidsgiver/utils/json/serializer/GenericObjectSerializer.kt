package no.nav.helsearbeidsgiver.utils.json.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement

object GenericObjectSerializer : KSerializer<Map<String, JsonElement>> {
    private val delegateSerializer = MapSerializer(
        String.serializer(),
        JsonElement.serializer()
    )

    override val descriptor: SerialDescriptor =
        SerialDescriptor("helsearbeidsgiver.kotlinx.GenericObject", delegateSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Map<String, JsonElement>) {
        encoder.encodeSerializableValue(delegateSerializer, value)
    }

    override fun deserialize(decoder: Decoder): Map<String, JsonElement> =
        decoder.decodeSerializableValue(delegateSerializer)
}

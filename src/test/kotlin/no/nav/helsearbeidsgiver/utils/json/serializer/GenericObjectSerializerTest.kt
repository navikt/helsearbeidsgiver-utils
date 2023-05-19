package no.nav.helsearbeidsgiver.utils.json.serializer

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import no.nav.helsearbeidsgiver.utils.json.fromJson
import no.nav.helsearbeidsgiver.utils.json.removeJsonWhitespace
import no.nav.helsearbeidsgiver.utils.json.toJson
import no.nav.helsearbeidsgiver.utils.json.toJsonStr

class GenericObjectSerializerTest : FunSpec({
    test("serialiserer korrekt") {
        val json = Mock.map.toJsonStr(GenericObjectSerializer)

        json shouldBe Mock.mapJson
    }

    test("deserialiserer korrekt") {
        val uuid = Mock.mapJson.fromJson(GenericObjectSerializer)

        uuid shouldBe Mock.map
    }

    test("gir SerializationException ved deserialiseringsfeil") {
        shouldThrow<SerializationException> {
            "ikke et map".fromJson(GenericObjectSerializer)
        }
    }
})

private object Mock {
    val map = mapOf(
        "en streng" to "her er det mye rart".toJson(),
        "et tall" to 42.toJson(Int.serializer()),
        "en bool" to true.toJson(Boolean.serializer()),
        "en liste" to listOf(3.14, null).toJson(Double.serializer().nullable),
        "et objekt" to mapOf(
            true to 1,
            false to 0
        ).toJson(
            MapSerializer(
                Boolean.serializer(),
                Int.serializer()
            )
        )
    )

    val mapJson = """
        {
            "en streng": "her er det mye rart",
            "et tall": 42,
            "en bool": true,
            "en liste": [3.14, null],
            "et objekt": {
                "true": 1,
                "false": 0
            }
        }
    """.removeJsonWhitespace()
}

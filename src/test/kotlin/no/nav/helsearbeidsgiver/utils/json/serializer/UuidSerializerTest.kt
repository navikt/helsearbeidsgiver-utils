package no.nav.helsearbeidsgiver.utils.json.serializer

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import no.nav.helsearbeidsgiver.utils.json.fromJson
import no.nav.helsearbeidsgiver.utils.json.toJsonStr
import java.util.UUID

private const val mockUuid = "01234567-abcd-0123-abcd-012345678901"

class UuidSerializerTest : FunSpec({
    test("serialiserer korrekt") {
        val uuid = UUID.fromString(mockUuid)

        val json = uuid.toJsonStr(UuidSerializer)

        json shouldBe "\"$mockUuid\""
    }

    test("deserialiserer korrekt") {
        val json = "\"$mockUuid\""

        val uuid = json.fromJson(UuidSerializer)

        uuid shouldBe UUID.fromString(mockUuid)
    }

    test("gir SerializationException ved deserialiseringsfeil") {
        shouldThrow<SerializationException> {
            "ikke en uuid".fromJson(UuidSerializer)
        }
    }
})

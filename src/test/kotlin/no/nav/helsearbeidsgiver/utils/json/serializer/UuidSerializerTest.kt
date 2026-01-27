package no.nav.helsearbeidsgiver.utils.json.serializer

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import no.nav.helsearbeidsgiver.utils.json.fromJson
import no.nav.helsearbeidsgiver.utils.json.toJson
import java.util.UUID

private const val MOCK_UUID = "01234567-abcd-0123-abcd-012345678901"

class UuidSerializerTest :
    FunSpec({
        test("serialiserer korrekt") {
            val uuid = UUID.fromString(MOCK_UUID)

            val json = uuid.toJson(UuidSerializer).toString()

            json shouldBe "\"$MOCK_UUID\""
        }

        test("deserialiserer korrekt") {
            val json = "\"$MOCK_UUID\""

            val uuid = json.fromJson(UuidSerializer)

            uuid shouldBe UUID.fromString(MOCK_UUID)
        }

        test("gir SerializationException ved deserialiseringsfeil") {
            shouldThrow<SerializationException> {
                "ikke en uuid".fromJson(UuidSerializer)
            }
        }
    })

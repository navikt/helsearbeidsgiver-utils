package no.nav.helsearbeidsgiver.utils.json.serializer

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import no.nav.helsearbeidsgiver.utils.json.fromJson
import no.nav.helsearbeidsgiver.utils.json.toJsonStr

private object TestSerializer : AsStringSerializer<ChaosGod>(
    serialName = "helsearbeidsgiver.kotlinx.AsStringSerializer.test",
    parse = {
        val (name, domain) = it.split(" rules ")
        ChaosGod(name, domain)
    },
)

class AsStringSerializerTest :
    FunSpec({
        test("serialiserer korrekt") {
            val nurgle =
                ChaosGod(
                    name = "Nurgle",
                    domain = "disease and decay",
                )

            val json = nurgle.toJsonStr(TestSerializer)

            json shouldBe "\"Nurgle rules disease and decay\""
        }

        test("deserialiserer korrekt") {
            val json = "\"Khorne rules blood and rage\""

            val khorne = json.fromJson(TestSerializer)

            khorne shouldBe
                ChaosGod(
                    name = "Khorne",
                    domain = "blood and rage",
                )
        }

        test("gir SerializationException ved deserialiseringsfeil") {
            shouldThrow<SerializationException> {
                "ikke en chaos god".fromJson(TestSerializer)
            }
        }
    })

private data class ChaosGod(
    val name: String,
    val domain: String,
) {
    override fun toString(): String = "$name rules $domain"
}

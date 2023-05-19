package no.nav.helsearbeidsgiver.utils.json.serializer

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import no.nav.helsearbeidsgiver.utils.json.fromJson
import no.nav.helsearbeidsgiver.utils.json.toJsonStr

class SerializerUtilsKtTest : FunSpec({

    context("list") {
        val nullableInts = listOf(1, 2, null)
        val nullableIntsJson = "[1,2,null]"

        val testSerializer = Int.serializer().nullable.list()

        test("serialiserer korrekt") {
            val json = nullableInts.toJsonStr(testSerializer)

            json shouldBe nullableIntsJson
        }

        test("deserialiserer korrekt") {
            val numbers = nullableIntsJson.fromJson(testSerializer)

            numbers shouldBe nullableInts
        }
    }

    context("set") {
        val nullableDoubleSet = setOf(1.0, 2.0, 2.0, null)
        val nullableDoubleSetJson = "[1.0,2.0,null]"

        val testSerializer = Double.serializer().nullable.set()

        test("serialiserer korrekt") {
            val json = nullableDoubleSet.toJsonStr(testSerializer)

            json shouldBe nullableDoubleSetJson
        }

        test("deserialiserer korrekt") {
            val numberSet = nullableDoubleSetJson.fromJson(testSerializer)

            numberSet shouldBe nullableDoubleSet
        }
    }
})

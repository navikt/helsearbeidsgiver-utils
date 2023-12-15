package no.nav.helsearbeidsgiver.utils.pipe

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PipeUtilsKtTest : FunSpec({
    context("orDefault") {
        test("'orDefault(default)' returns receiver if non-null or default otherwise") {
            val nullableStringThatIsNull: String? = null

            @Suppress("RedundantNullableReturnType")
            val nullableStringThatIsNotNull: String? = "I may be null. Who knows?"

            @Suppress("RedundantExplicitType")
            val nonNullableString: String = "I am definitely not null!"

            nullableStringThatIsNull.orDefault(nonNullableString) shouldBe nonNullableString
            nullableStringThatIsNotNull.orDefault(nonNullableString) shouldBe nullableStringThatIsNotNull
        }

        test("'orDefault(block)' returns receiver if non-null or block() otherwise") {
            val nullableIntThatIsNull: Int? = null

            @Suppress("RedundantNullableReturnType")
            val nullableIntThatIsNotNull: Int? = -42

            @Suppress("RedundantExplicitType")
            val nonNullableInt: Int = 1337

            nullableIntThatIsNull.orDefault { nonNullableInt } shouldBe nonNullableInt
            nullableIntThatIsNotNull.orDefault { nonNullableInt } shouldBe nullableIntThatIsNotNull
        }
    }

    test("ifTrue executes block if receiver is 'true'") {
        var counter = 0

        false.ifTrue {
            counter += 1
        }
        counter shouldBe 0

        true.ifTrue {
            counter += 1
        }
        counter shouldBe 1
    }

    test("ifFalse executes block if receiver is 'false'") {
        var counter = 0

        true.ifFalse {
            counter += 1
        }
        counter shouldBe 0

        false.ifFalse {
            counter += 1
        }
        counter shouldBe 1
    }

    test("mapFirst maps receiver Pair's 'first'-field to new value") {
        val original = Pair(
            first = "What is the 'second'-field?",
            second = 9001
        )

        val mapped = original.mapFirst {
            "IT'S OVER 9000!!!!"
        }

        mapped shouldBe Pair(
            first = "IT'S OVER 9000!!!!",
            second = original.second
        )
    }

    test("mapSecond maps receiver Pair's 'second'-field to new value") {
        val original = Pair(
            first = "The 'second'-field better be something tasty.",
            second = "surströmming"
        )

        val mapped = original.mapSecond {
            3.14
        }

        mapped shouldBe Pair(
            first = original.first,
            second = 3.14
        )
    }
})

package no.nav.helsearbeidsgiver.utils.json.serializer

import java.util.UUID

object UuidSerializer : AsStringSerializer<UUID>(
    serialName = "helsearbeidsgiver.kotlinx.UuidSerializer",
    parse = UUID::fromString,
)

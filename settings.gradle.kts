rootProject.name = "utils"

pluginManagement {
    val kotlinVersion: String by settings
    val ktlintVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("org.jmailen.kotlinter") version ktlintVersion
    }
}

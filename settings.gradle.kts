rootProject.name = "utils"

pluginManagement {
    val kotestVersion: String by settings
    val kotlinVersion: String by settings
    val ktlintVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("org.jmailen.kotlinter") version ktlintVersion
        id("io.kotest") version kotestVersion
    }
}

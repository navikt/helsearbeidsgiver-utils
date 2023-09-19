import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "no.nav.helsearbeidsgiver"
version = "0.6.0"

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jlleitschuh.gradle.ktlint")
    id("maven-publish")
    id("java-test-fixtures")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}

java {
    withSourcesJar()
}

repositories {
    mavenCentral()
    mavenNav("*")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        mavenNav("helsearbeidsgiver-${rootProject.name}")
    }
}

dependencies {
    val kotestVersion: String by project
    val kotlinCoroutinesVersion: String by project
    val kotlinxSerializationVersion: String by project
    val logbackVersion: String by project
    val mockkVersion: String by project
    val slf4jVersion: String by project

    api("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    api("org.slf4j:slf4j-api:$slf4jVersion")

    testFixturesImplementation("io.mockk:mockk:$mockkVersion")
    testFixturesImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

    testRuntimeOnly("ch.qos.logback:logback-classic:$logbackVersion")
}

fun RepositoryHandler.mavenNav(repo: String): MavenArtifactRepository {
    val githubPassword: String by project

    return maven {
        setUrl("https://maven.pkg.github.com/navikt/$repo")
        credentials {
            username = "x-access-token"
            password = githubPassword
        }
    }
}

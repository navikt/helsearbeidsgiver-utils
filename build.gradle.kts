import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "no.nav.helsearbeidsgiver"
version = "0.4.1"

plugins {
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
    id("maven-publish")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

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
    val coroutinesVersion: String by project
    val kotestVersion: String by project
    val logbackVersion: String by project
    val slf4jVersion: String by project

    api("org.slf4j:slf4j-api:$slf4jVersion")

    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

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

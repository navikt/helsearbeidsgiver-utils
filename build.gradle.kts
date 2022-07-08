import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "no.nav.helsearbeidsgiver"
version = "0.1.1"

plugins {
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
    id("maven-publish")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

// Kreves for å få korrekt JVM-versjon. Fikses muligens i Kotlin 1.7.20. For mer info, se lenke:
// https://youtrack.jetbrains.com/issue/KT-52474/An-attribute-orggradlejvmversion-isnt-set-correctly-while-updating-KGP-to-170#focus=Comments-27-6102307.0-0
tasks.withType<JavaCompile> {
    targetCompatibility = "11"
    sourceCompatibility = "11"
}

tasks {
    test {
        useJUnitPlatform()
    }
}

repositories {
    val githubPassword: String by project

    mavenCentral()
    maven {
        credentials {
            username = System.getenv("GITHUB_ACTOR") ?: "x-access-token"
            password = System.getenv("GITHUB_TOKEN") ?: githubPassword
        }
        setUrl("https://maven.pkg.github.com/navikt/*")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/navikt/helsearbeidsgiver-${rootProject.name}")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

dependencies {
    val slf4jVersion: String by project

    api("org.slf4j:slf4j-api:$slf4jVersion")
    testImplementation(kotlin("test"))
}

// Copyright (c) 2022 Erwin Kok. BSD-3-Clause license. See LICENSE file for more details.
@file:Suppress("UnstableApiUsage")

import com.adarshr.gradle.testlogger.theme.ThemeType
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    kotlin("jvm") version "2.3.0"
    `java-library`
    `java-test-fixtures`
    signing
    `maven-publish`

    id("com.google.protobuf") version "0.9.6"

    alias(libs.plugins.build.kover)
    alias(libs.plugins.build.ktlint)
    alias(libs.plugins.build.nexus)
    alias(libs.plugins.build.versions)
    alias(libs.plugins.build.testlogger)
}

repositories {
    mavenCentral()
}

group = "org.erwinkok.libp2p"
version = "1.1.0"

java {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib"))

    implementation(libs.kotlin.logging)
    implementation(libs.kerby.asn1)
    implementation(libs.result.monad)
    implementation(libs.protobuf.java)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)

    testImplementation(testFixtures(libs.result.monad))
    testImplementation(libs.klaxon)

    testRuntimeOnly(libs.junit.jupiter.engine)

    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

testlogger {
    theme = ThemeType.MOCHA
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    withType<Javadoc> {
        exclude("org/erwinkok/libp2p/crypto/pb/**")
    }

    named<Jar>("sourcesJar") {
        exclude("org/erwinkok/libp2p/crypto/pb/**")
    }
}

sourceSets {
    main {
        java {
            srcDirs("src/main/kotlin", "build/generated/source/proto/main/java")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.33.4"
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set(project.name)
                description.set("Cryptographic utilities used by libp2p. Supported key types: ecdsa, ed25519, secp256k1 and rsa")
                inceptionYear.set("2022")
                url.set("https://github.com/erwin-kok/libp2p-crypto")
                licenses {
                    license {
                        name.set("BSD-3-Clause")
                        url.set("https://opensource.org/licenses/BSD-3-Clause")
                    }
                }
                developers {
                    developer {
                        name.set("Erwin Kok")
                        url.set("https://github.com/erwin-kok/")
                    }
                }
                scm {
                    url.set("https://github.com/erwin-kok/libp2p-crypto")
                    connection.set("scm:git:https://github.com/erwin-kok/libp2p-crypto")
                    developerConnection.set("scm:git:ssh://git@github.com:erwin-kok/libp2p-crypto.git")
                }
                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/erwin-kok/libp2p-crypto/issues")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

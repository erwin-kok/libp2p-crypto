// Copyright (c) 2022 Erwin Kok. BSD-3-Clause license. See LICENSE file for more details.
@file:Suppress("UnstableApiUsage")

import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestStackTraceFilter

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm") version "1.7.20"
    `java-library`
    `java-test-fixtures`
    signing
    `maven-publish`

    id("com.google.protobuf") version "0.8.19"

    alias(libs.plugins.build.kover)
    alias(libs.plugins.build.ktlint)
    alias(libs.plugins.build.nexus)
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

group = "org.erwinkok.libp2p"
version = "0.3.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(libs.kotlin.logging)
    implementation(libs.kerby.asn1)
    implementation(libs.result.monad)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)

    testImplementation(testFixtures(libs.result.monad))
    testImplementation(libs.klaxon)

    testRuntimeOnly(libs.junit.jupiter.engine)

    api(libs.protobuf.java)
}

tasks {
    compileKotlin {
        println("Configuring KotlinCompile $name in project ${project.name}...")
        kotlinOptions {
            @Suppress("SpellCheckingInspection")
            freeCompilerArgs = listOf("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
            allWarningsAsErrors = true
            jvmTarget = "11"
            languageVersion = "1.7"
            apiVersion = "1.7"
        }
    }

    compileTestKotlin {
        println("Configuring KotlinTestCompile $name in project ${project.name}...")
        kotlinOptions {
            @Suppress("SpellCheckingInspection")
            freeCompilerArgs = listOf("-Xjsr305=strict")
            allWarningsAsErrors = true
            jvmTarget = "11"
            languageVersion = "1.7"
            apiVersion = "1.7"
        }
    }

    compileJava {
        println("Configuring compileJava $name in project ${project.name}...")
        @Suppress("SpellCheckingInspection")
        options.compilerArgs.addAll(
            listOf(
                "-Xlint:all,-overloads,-rawtypes,-unchecked,-cast"
                // "-Werror"
            )
        )
        sourceCompatibility = "11"
        targetCompatibility = "11"
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
        testLogging {
            events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED)
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            maxGranularity = 3
            stackTraceFilters = setOf(TestStackTraceFilter.ENTRY_POINT)
        }
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
        artifact = "com.google.protobuf:protoc:3.21.2"
    }
}

kover {
    htmlReport {
        onCheck.set(true)
    }
}

koverMerged {
    enable()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set(project.name)
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

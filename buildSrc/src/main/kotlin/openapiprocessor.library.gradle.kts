@file:Suppress("UnstableApiUsage")

import org.checkerframework.gradle.plugin.CheckerFrameworkExtension
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    `java-library`
    jacoco
    kotlin("jvm")

    id("org.checkerframework")
    id("com.github.ben-manes.versions")
}

// see buildSrc/build.gradle.kts
val libs = the<LibrariesForLibs>()

// gradle.properties
val projectVersion: String by project

group = "io.openapiprocessor"
version = projectVersion
println("version: $projectVersion")


repositories {
    mavenCentral()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        mavenContent {
            snapshotsOnly()
        }
    }
}

dependencies {
    checkerFramework(libs.checker)
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.named<JacocoReport>("jacocoTestReport") {
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(false)
        //html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

tasks.withType<Test>().configureEach {
    finalizedBy(tasks.named("jacocoTestReport"))
}

configure<CheckerFrameworkExtension> {
    skipCheckerFramework = true
    excludeTests = true
    extraJavacArgs = listOf("-Awarns")

    checkers = listOf(
        "org.checkerframework.checker.nullness.NullnessChecker",
//        "org.checkerframework.checker.interning.InterningChecker",
//        "org.checkerframework.checker.resourceleak.ResourceLeakChecker",
//        "org.checkerframework.checker.index.IndexChecker"
    )
}

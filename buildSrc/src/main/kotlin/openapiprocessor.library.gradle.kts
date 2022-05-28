@file:Suppress("UnstableApiUsage")

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

group = "io.openapiprocessor"
version = libs.versions.openapiprocessor
println("version: $version")

repositories {
    mavenCentral()
}

/*
jacoco {
    toolVersion = "0.8.7"
}

tasks.jacocoTestReport {
    reports {
//        xml.required.set(false)
//        csv.required.set(false)
//        html.required.set(false)
//        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

configure<org.checkerframework.gradle.plugin.CheckerFrameworkExtension> {
//    skipCheckerFramework = true
//    excludeTests = true
    extraJavacArgs = listOf("-Awarns")

    checkers = listOf(
        "org.checkerframework.checker.nullness.NullnessChecker",
//        "org.checkerframework.checker.interning.InterningChecker",
//        "org.checkerframework.checker.resourceleak.ResourceLeakChecker",
//        "org.checkerframework.checker.index.IndexChecker"
    )
}
*/

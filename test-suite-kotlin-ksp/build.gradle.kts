import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.google.devtools.ksp")
    id("io.micronaut.build.internal.crac-test-suite")
}

dependencies {
    ksp(mn.micronaut.inject.kotlin)

    testImplementation(projects.micronautCrac)
    testImplementation(mnTest.micronaut.test.junit5)

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

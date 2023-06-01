plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    id("org.jetbrains.kotlin.kapt") version "1.8.21"
    id("io.micronaut.build.internal.crac-test-suite")
}

dependencies {
    testImplementation(projects.micronautCrac)
    kaptTest(mn.micronaut.inject.java)
    testImplementation(mnTest.micronaut.test.junit5)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

plugins {
    `java-library`
    id("io.micronaut.build.internal.crac-test-suite")
}

dependencies {
    testImplementation(projects.micronautCrac)
    testImplementation(mnTest.micronaut.test.junit5)

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

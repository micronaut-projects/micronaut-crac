plugins {
    id("io.micronaut.build.internal.kotlin-ksp")
    id("io.micronaut.build.internal.crac-test-suite")
}

dependencies {
    ksp(mn.micronaut.inject.kotlin)

    testImplementation(projects.micronautCrac)
    testImplementation(mnTest.micronaut.test.junit5)

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

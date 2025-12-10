plugins {
    id("io.micronaut.build.internal.kotlin-kapt")
    id("io.micronaut.build.internal.crac-test-suite")
}

dependencies {
    testImplementation(projects.micronautCrac)
    kaptTest(mn.micronaut.inject.java)
    testImplementation(mnTest.micronaut.test.junit5)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

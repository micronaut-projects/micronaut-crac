plugins {
    id("groovy")
    id("io.micronaut.build.internal.crac-test-suite")
}

dependencies {
    testImplementation(projects.micronautCrac)
    testImplementation(mnTest.micronaut.test.spock)
    testImplementation(mn.micronaut.inject.groovy)
}

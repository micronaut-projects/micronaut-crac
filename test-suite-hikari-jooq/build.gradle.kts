plugins {
    id("groovy")
    id("io.micronaut.build.internal.crac-test-suite")
}

dependencies {
    testImplementation(mn.micronaut.test.spock)
    testImplementation(mn.micronaut.inject.groovy)
}

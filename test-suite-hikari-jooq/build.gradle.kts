plugins {
    id("groovy")
    id("io.micronaut.library") version "3.7.3"
}

dependencies {
    testImplementation(project(":crac"))
    testImplementation(mn.micronaut.test.spock)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.inject.groovy)
    testImplementation(mn.micronaut.jooq)
    testImplementation(mn.micronaut.jdbc.hikari)
    testRuntimeOnly(mn.logback)
    testRuntimeOnly(mn.h2)
}

tasks.test {
    useJUnitPlatform()
}

plugins {
    id("groovy")
    id("io.micronaut.library") version "4.0.0-SNAPSHOT"
}

// Required as there's no M1 platform yet
micronaut {
    this.version("4.0.0-SNAPSHOT")
}

dependencies {
    testImplementation(project(":crac"))
    testImplementation(mnTest.micronaut.test.spock)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.inject.groovy)
    testImplementation(mnSql.micronaut.jooq)
    testImplementation(mnSql.micronaut.jdbc.hikari)
    testImplementation(mn.logback.classic)
    testRuntimeOnly(mnSql.h2)
}

tasks.test {
    useJUnitPlatform()
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jooq") {
            useVersion("3.14.16")
            because("JOOQ 3.15.x+ requires Java 11")
        }
    }
}

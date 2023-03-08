plugins {
    id("groovy")
    id("io.micronaut.library") version "3.7.3"
    id("io.micronaut.test-resources") version "3.7.3"
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation(project(":crac"))
    testImplementation(mn.micronaut.test.spock)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.inject.groovy)
    testImplementation(mn.micronaut.redis.lettuce)
    testImplementation(mn.logback)

    testRuntimeOnly(mn.h2)
}

tasks.test {
    useJUnitPlatform()
}

micronaut {
    version.set(libs.versions.micronaut.asProvider())
    testResources {
        additionalModules.set(listOf("redis"))
    }
}

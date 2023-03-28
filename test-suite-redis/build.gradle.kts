plugins {
    id("groovy")
    id("io.micronaut.library") version "4.0.0-SNAPSHOT"
    id("io.micronaut.test-resources") version "4.0.0-SNAPSHOT"
}

dependencies {
    testImplementation(projects.micronautCrac)
    testImplementation(mnTest.micronaut.test.spock)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.inject.groovy)
    testImplementation(mnCache.micronaut.cache.core)
    testImplementation(mnRedis.micronaut.redis.lettuce)
    testImplementation(mn.logback.classic)

    testRuntimeOnly(mnSql.h2)
    testRuntimeOnly(mn.micronaut.jackson.databind)
}

tasks.test {
    useJUnitPlatform()
}

micronaut {
//    version.set(libs.versions.micronaut.asProvider())
    // Required as there's no M1 platform yet
    version.set("4.0.0-SNAPSHOT")
    testResources {
        additionalModules.set(listOf("redis"))
    }
}
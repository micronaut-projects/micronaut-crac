plugins {
    id("groovy")
    id("io.micronaut.build.internal.crac-library-test-suite")
}

dependencies {
    testImplementation(mn.micronaut.http.client)
    testImplementation(mnCache.micronaut.cache.core)
    testImplementation(mnRedis.micronaut.redis.lettuce)
    testImplementation(platform(mnTest.boms.testcontainers))
    testImplementation(libs.testcontainers)
    testImplementation(libs.managed.testcontainers.redis)
    testImplementation("org.testcontainers:testcontainers-spock")
    testRuntimeOnly(mn.micronaut.jackson.databind)
}

micronaut {
    importMicronautPlatform.set(false)
}

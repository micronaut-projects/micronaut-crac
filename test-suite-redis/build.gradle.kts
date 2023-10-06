plugins {
    id("groovy")
    id("io.micronaut.build.internal.crac-library-test-suite")
    id("io.micronaut.test-resources")
}

dependencies {
    testImplementation(mn.micronaut.http.client)
    testImplementation(mnCache.micronaut.cache.core)
    testImplementation(mnRedis.micronaut.redis.lettuce)

    testRuntimeOnly(mn.micronaut.jackson.databind)
}

micronaut {
    importMicronautPlatform.set(false)
    testResources {
        additionalModules.set(listOf("redis"))
    }
}

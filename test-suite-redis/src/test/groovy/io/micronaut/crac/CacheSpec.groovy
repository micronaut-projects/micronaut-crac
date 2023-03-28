package io.micronaut.crac

import io.micronaut.cache.annotation.Cacheable
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.util.StringUtils
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import jakarta.inject.Inject
import jakarta.inject.Singleton
import spock.lang.PendingFeature

@Property(name = "spec.name", value = "CacheSpec")
@Property(name = "redis.caches.test.enabled", value = StringUtils.TRUE)
class CacheSpec extends BaseCacheSpecification {

    @Inject
    @Client("/")
    HttpClient client

    @PendingFeature(reason = "Currently this doesn't work, as the CacheManager keeps hold of it's cache connections")
    def "cache annotations work"() {
        when:
        def response = client.toBlocking().retrieve("/foo")

        then:
        response.startsWith('foo')

        and:
        client.toBlocking().retrieve("/foo") == response

        when:
        simulator.runBeforeCheckpoint()

        and:
        simulator.runAfterRestore()

        then:
        client.toBlocking().retrieve("/foo") == response
    }

    @Singleton
    @Requires(property = "spec.name", value = "CacheSpec")
    static class CacheService {

        @Cacheable("test")
        String get(String key) {
            "$key:${UUID.randomUUID().toString()}"
        }
    }

    @Controller
    @Requires(property = "spec.name", value = "CacheSpec")
    static class TestController {

        @Inject
        CacheService cacheService

        @Get("/{key}")
        String get(String key) {
            cacheService.get(key)
        }
    }
}

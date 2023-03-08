package io.micronaut.crac

import ch.qos.logback.classic.Logger
import io.micronaut.configuration.lettuce.cache.RedisCache
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.util.StringUtils
import io.micronaut.runtime.context.scope.Refreshable
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Property(name = "spec.name", value = "ExplicitCacheSpec")
@Property(name = "redis.caches.test.enabled", value = StringUtils.TRUE)
class ExplicitCacheSpec extends BaseCacheSpecification {

    @Inject
    CacheService cacheService

    def "redis cache can be checkpointed"() {
        given:
        Logger l = (Logger) LoggerFactory.getLogger("io.micronaut.crac.resources")
        l.addAppender(appender)
        appender.start()

        when:
        cacheService.put("foo", "bar")

        and:
        simulator.runBeforeCheckpoint()

        then:
        appender.events.formattedMessage.any { it.contains("Destroying Redis cache") }

        when: "we trigger a restore"
        simulator.runAfterRestore()

        then: "the data is still there"
        cacheService.get("foo", String) == "bar"
    }

    @Singleton
    @Refreshable
    @Requires(property = "spec.name", value = "ExplicitCacheSpec")
    static class CacheService {

        @Inject
        @Named("test")
        RedisCache redisCache

        void put(Object s1, Object s2) {
            redisCache.put(s1, s2)
        }

        <T> T get(Object s1, Class<T> aClass) {
            redisCache.get(s1, aClass).get()
        }
    }
}

package io.micronaut.crac

import io.micronaut.context.ApplicationContext
import io.micronaut.core.util.StringUtils
import io.micronaut.crac.resources.redis.CracRedisConfiguration
import io.micronaut.crac.resources.redis.RedisCacheResource
import io.micronaut.crac.resources.redis.RedisClientResource
import io.micronaut.crac.resources.redis.RedisNamedConfigResource
import io.micronaut.crac.resources.redis.StatefulRedisConnectionResource
import io.micronaut.crac.resources.redis.StatefulRedisPubSubConnectionResource
import spock.lang.Specification

class CracRedisConfigurationSpec extends Specification {

    void "Redis CRaC enabled by default"() {
        given:
        def ctx = ApplicationContext.run(
                "redis.enabled": StringUtils.TRUE,
                "redis.caches.test.enabled": StringUtils.TRUE,
        )

        when:
        def cfg = ctx.getBean(CracConfiguration)

        then:
        cfg.enabled

        when:
        def redisCfg = ctx.getBean(CracRedisConfiguration)

        then:
        redisCfg.enabled
        redisCfg.cacheEnabled
        redisCfg.clientEnabled
        redisCfg.connectionEnabled

        and:
        ctx.containsBean(CracRedisConfiguration)
        ctx.containsBean(RedisCacheResource)
        ctx.containsBean(RedisNamedConfigResource)
        // These are deprecated and should be removed in 2.0.0
        !ctx.containsBean(RedisClientResource)
        !ctx.containsBean(StatefulRedisConnectionResource)
        !ctx.containsBean(StatefulRedisPubSubConnectionResource)

        cleanup:
        ctx.close()
    }

    void "CRaC can be disabled and that disables Redis also"() {
        given:
        ApplicationContext ctx = ApplicationContext.run(
                'redis.enabled': StringUtils.TRUE,
                "redis.caches.test.enabled": StringUtils.TRUE,
                'crac.enabled': StringUtils.FALSE
        )

        expect:
        !ctx.containsBean(CracConfiguration)
        !ctx.containsBean(CracContextProvider)
        !ctx.containsBean(OrderedResource)
        !ctx.containsBean(StartupCracRegistration)
        !ctx.containsBean(CracResourceRegistrar)
        !ctx.containsBean(CracRedisConfiguration)
        !ctx.containsBean(RedisCacheResource)
        !ctx.containsBean(RedisNamedConfigResource)
        // These are deprecated and should be removed in 2.0.0
        !ctx.containsBean(RedisClientResource)
        !ctx.containsBean(StatefulRedisConnectionResource)
        !ctx.containsBean(StatefulRedisPubSubConnectionResource)

        cleanup:
        ctx.close()
    }

    void "CRaC Redis can be disabled"() {
        given:
        ApplicationContext ctx = ApplicationContext.run('redis.enabled': StringUtils.TRUE, 'crac.redis.enabled': StringUtils.FALSE)

        expect:
        ctx.containsBean(CracConfiguration)
        ctx.containsBean(CracContextProvider)
        ctx.containsBean(OrderedResource)
        ctx.containsBean(StartupCracRegistration)
        ctx.containsBean(CracResourceRegistrar)
        !ctx.containsBean(CracRedisConfiguration)
        !ctx.containsBean(RedisCacheResource)
        !ctx.containsBean(RedisNamedConfigResource)
        // These are deprecated and should be removed in 2.0.0
        !ctx.containsBean(RedisClientResource)
        !ctx.containsBean(StatefulRedisConnectionResource)
        !ctx.containsBean(StatefulRedisPubSubConnectionResource)

        cleanup:
        ctx.close()
    }

    void "CRaC Redis component can be disabled separately"() {
        given:
        String cfg = "crac.redis.$suffix-enabled"

        ApplicationContext ctx = ApplicationContext.run(
                'redis.enabled': StringUtils.TRUE,
                "redis.caches.test.enabled": StringUtils.TRUE,
                (cfg): StringUtils.FALSE,
        )

        expect:
        ctx.containsBean(RedisCacheResource) == (suffix != 'cache')
        ctx.containsBean(RedisNamedConfigResource) == (suffix != 'servers')
        // These are deprecated and should be removed in 2.0.0
        !ctx.containsBean(RedisClientResource)
        !ctx.containsBean(StatefulRedisConnectionResource)
        !ctx.containsBean(StatefulRedisPubSubConnectionResource)

        where:
        suffix << ['cache', 'servers']
    }
}

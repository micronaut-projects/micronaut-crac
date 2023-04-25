package io.micronaut.crac

import ch.qos.logback.classic.Logger
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import jakarta.inject.Inject
import org.slf4j.LoggerFactory

@Property(name = "spec.name", value = "RedisStatefulPubSubConnectionSpec")
@Property(name = "redis.cache.enabled", value = StringUtils.TRUE)
class RedisStatefulPubSubConnectionSpec extends BaseCacheSpecification {

    @Inject
    StatefulRedisPubSubConnection<String, String> connection;

    void "test redis pubsub connection"() {
        given:
        Logger l = (Logger) LoggerFactory.getLogger("io.micronaut.crac.resources")
        l.addAppender(appender)
        appender.start()

        when:
        connection.sync().set("foo", "bar")

        then:
        connection.sync().get("foo") == "bar"

        when:
        simulator.runBeforeCheckpoint()

        then:
        appender.events.formattedMessage.any { it.contains("Destroying") }

        when: "we trigger a restore"
        simulator.runAfterRestore()

        and: "we get a new connection"
        def newConnection = ctx.getBean(StatefulRedisPubSubConnection.class)

        then: "the data is still there"
        newConnection.sync().get("foo") == "bar"
    }
}

package io.micronaut.crac

import ch.qos.logback.classic.Logger
import io.lettuce.core.api.StatefulRedisConnection
import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import jakarta.inject.Inject
import org.slf4j.LoggerFactory

@Property(name = "spec.name", value = "RedisCracSpec")
@Property(name = "redis.cache.enabled", value = StringUtils.TRUE)
class RedisStatefulConnectionSpec extends BaseCacheSpecification {

    @Inject
    StatefulRedisConnection<String, String> connection;

    void "test redis connection"() {
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
        appender.events.formattedMessage.any { it.contains("Destroying Redis stateful connection") }

        when: "we trigger a restore"
        simulator.runAfterRestore()

        and: "we get a new connection"
        def newConnection = ctx.getBean(StatefulRedisConnection.class)

        then: "the data is still there"
        newConnection.sync().get("foo") == "bar"
    }
}

package io.micronaut.crac

import ch.qos.logback.classic.Logger
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import jakarta.inject.Inject
import org.slf4j.LoggerFactory

@Property(name = "spec.name", value = "BothRedisConnectionSpec")
@Property(name = "redis.cache.enabled", value = StringUtils.TRUE)
class BothRedisConnectionSpec extends BaseCacheSpecification {

    @Inject
    StatefulRedisPubSubConnection<String, String> pubsub;

    @Inject
    StatefulRedisConnection<String, String> connection;

    void "test both redis connections being present"() {
        given:
        Logger l = (Logger) LoggerFactory.getLogger("io.micronaut.crac.resources")
        l.addAppender(appender)
        appender.start()

        when:
        connection.sync().set("foo", "bar")

        then:
        connection.sync().get("foo") == "bar"

        when:
        pubsub.sync().set("pub", "sub")

        then:
        pubsub.sync().get("pub") == "sub"

        when:
        simulator.runBeforeCheckpoint()

        then:
        appender.events.formattedMessage.any { it.contains("Destroying Redis stateful connection") }
        appender.events.formattedMessage.any { it.contains("Destroying Redis stateful pubsub connection") }

        when: "we trigger a restore"
        simulator.runAfterRestore()

        and: "we get new connections"
        def newConnection = ctx.getBean(StatefulRedisConnection.class)
        def newPubSub = ctx.getBean(StatefulRedisPubSubConnection.class)

        then: "the data is still there"
        newConnection.sync().get("foo") == "bar"
        newPubSub.sync().get("pub") == "sub"
    }
}

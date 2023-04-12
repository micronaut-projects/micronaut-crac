package io.micronaut.crac

import ch.qos.logback.classic.Logger
import io.lettuce.core.api.StatefulRedisConnection
import io.micronaut.context.annotation.Property
import io.micronaut.inject.qualifiers.Qualifiers
import jakarta.inject.Inject
import jakarta.inject.Named
import org.slf4j.LoggerFactory

@Property(name = "spec.name", value = "RedisStatefulConnectionSpec")
@Property(name = "redis.servers.pub.uri", value = '${redis.uri}')
@Property(name = "redis.servers.sub.uri", value = '${redis.uri}')
class TwoServerRedisSpec extends BaseCacheSpecification {

    @Inject
    @Named("pub")
    StatefulRedisConnection<String, String> pub;

    @Inject
    @Named("sub")
    StatefulRedisConnection<String, String> sub;

    void "test redis connection"() {
        given:
        Logger l = (Logger) LoggerFactory.getLogger("io.micronaut.crac.resources")
        l.addAppender(appender)
        appender.start()

        when:
        pub.sync().set("foo", "bar")

        then:
        pub.sync().get("foo") == "bar"

        and:
        sub.sync().get("foo") == "bar"

        when:
        simulator.runBeforeCheckpoint()

        then:
        appender.events.formattedMessage.any { it.contains("Destroying Redis stateful connection") }

        when: "we trigger a restore"
        simulator.runAfterRestore()

        and: "we get a new connection"
        def newPub = ctx.getBean(StatefulRedisConnection.class, Qualifiers.byName("pub"))
        def newSub = ctx.getBean(StatefulRedisConnection.class, Qualifiers.byName("sub"))

        then: "the data is still there"
        newPub != newSub
        newPub.sync().get("foo") == "bar"
        newSub.sync().get("foo") == "bar"
    }
}

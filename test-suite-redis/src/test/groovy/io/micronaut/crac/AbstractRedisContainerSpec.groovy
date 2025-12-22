package io.micronaut.crac

import com.redis.testcontainers.RedisContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import io.micronaut.test.support.TestPropertyProvider
import spock.lang.Shared
import spock.lang.Specification

@Testcontainers
abstract class AbstractRedisContainerSpec extends Specification implements TestPropertyProvider {

    @Shared
    static RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:7.2")).withReuse(true)

    def setupSpec() {
        if (!redis.isRunning()) {
            redis.start()
        }
    }

    @Override
    Map<String, String> getProperties() {
        if (!redis.isRunning()) {
            redis.start()
        }
        String uri = redis.getRedisURI()
        return [
            'redis.enabled': 'true',
            'redis.uri'   : uri
        ]
    }

    def cleanupSpec() {
        System.clearProperty("redis.uri")
        if (redis != null && redis.isRunning()) {
            redis.stop()
        }
    }
}

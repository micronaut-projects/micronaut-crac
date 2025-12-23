package io.micronaut.crac

import com.redis.testcontainers.RedisContainer
import org.testcontainers.utility.DockerImageName
import spock.lang.Specification

abstract class AbstractRedisContainerSpec extends Specification {

    private static final RedisContainer CONTAINER = createContainer()

    private static RedisContainer createContainer() {
        def container = new RedisContainer(DockerImageName.parse("redis:7-alpine"))
        container.start()
        return container
    }

    static Map<String, String> getProperties() {
        [
                'redis.enabled': 'true',
                'redis.uri': CONTAINER.getRedisURI()
        ]
    }
}

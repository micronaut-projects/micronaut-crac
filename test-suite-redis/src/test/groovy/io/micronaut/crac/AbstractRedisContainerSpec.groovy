package io.micronaut.crac

import com.redis.testcontainers.RedisContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import io.micronaut.test.support.TestPropertyProvider
import spock.lang.Shared
import spock.lang.Specification

abstract class AbstractRedisContainerSpec {

    private static final String IMAGE_NAME = "redis"
    private static RedisContainer container

     static  Map<String, String> getProperties() {
        if (container == null) {
            container = new RedisContainer(DockerImageName.parse(IMAGE_NAME))
            do {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } while(!container.isRunning());
            return getProperties(container);
        } else {
            return getProperties(container);
        }
    }

    static def Map<String, String> getProperties(RedisContainer container) {
        String uri = container.getRedisURI()
        return [
                'redis.enabled': 'true',
                'redis.uri'   : uri
        ]
    }
}

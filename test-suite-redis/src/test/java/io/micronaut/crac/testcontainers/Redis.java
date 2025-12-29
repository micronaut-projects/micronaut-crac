package io.micronaut.crac.testcontainers;

import com.redis.testcontainers.RedisContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

public class Redis {
    private static final String IMAGE_NAME = "redis:7-alpine";
    private static RedisContainer container;

    public static Map<String, String> getProperties() {
        if (container == null) {
            container = new RedisContainer(DockerImageName.parse(IMAGE_NAME));
            container.start();
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

    private static Map<String, String> getProperties(RedisContainer container) {
        return Map.of(
                "redis.enabled", "true",
                "redis.uri", container.getRedisURI()
        );
    }
}

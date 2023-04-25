/*
 * Copyright 2017-2023 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.crac.resources.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.micronaut.configuration.lettuce.AbstractRedisConfiguration;
import io.micronaut.configuration.lettuce.DefaultRedisConfiguration;
import io.micronaut.context.BeanContext;
import io.micronaut.context.Qualifier;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.util.StringUtils;
import io.micronaut.crac.CracEventPublisher;
import io.micronaut.crac.CracResourceRegistrar;
import io.micronaut.crac.resources.NettyEmbeddedServerResource;
import io.micronaut.inject.qualifiers.Qualifiers;
import org.crac.Context;
import org.crac.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Destroys Redis connection beans for a named configuration.
 * This also picks up the Default configuration, as that is a named bean with the name "application".
 *
 * @author Tim Yates
 * @since 1.2.3
 */
@Experimental
@EachBean(AbstractRedisConfiguration.class)
@Requires(classes = {AbstractRedisConfiguration.class})
@Requires(bean = CracResourceRegistrar.class)
@Requires(property = RedisNamedConfigResource.ENABLED_PROPERTY, defaultValue = StringUtils.TRUE, value = StringUtils.TRUE)
public class RedisNamedConfigResource extends AbstractRedisResource<RedisClient> {

    static final String ENABLED_PROPERTY = CracRedisConfigurationProperties.PREFIX + ".connection-enabled";

    private static final Logger LOG = LoggerFactory.getLogger(RedisNamedConfigResource.class);

    private final CracEventPublisher eventPublisher;
    private final AbstractRedisConfiguration configuration;
    private final String name;

    public RedisNamedConfigResource(
        BeanContext beanContext,
        CracEventPublisher eventPublisher,
        AbstractRedisConfiguration configuration
    ) {
        super(beanContext);
        this.eventPublisher = eventPublisher;
        this.configuration = configuration;
        this.name = configuration.getName();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating RedisNamedConfigResource for configuration {} named '{}'", configuration.getClass().getSimpleName(), name);
        }
    }

    private <T> Optional<T> findBean(Class<T> beanType) {
        // If this is a default configuration, we don't want to use the name qualifier
        Qualifier<T> qualifier = configuration instanceof DefaultRedisConfiguration ? Qualifiers.none() : Qualifiers.byName(name);

        return beanContext.findBean(beanType, qualifier);
    }

    @Override
    public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
        eventPublisher.fireBeforeCheckpointEvents(this, () -> {
            long beforeStart = System.nanoTime();
            findBean(RedisClient.class).ifPresent(redisClient -> {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Destroying Redis client bean named '{}' {}", name, redisClient);
                }
                beanContext.destroyBean(redisClient);
            });
            findBean(StatefulRedisConnection.class).ifPresent(statefulRedisConnection -> {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Destroying Redis stateful connection named '{}' {}", name, statefulRedisConnection);
                }
                beanContext.destroyBean(statefulRedisConnection);
            });
            findBean(StatefulRedisPubSubConnection.class).ifPresent(statefulRedisPubSubConnection -> {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Destroying Redis stateful pubsub connection named '{}' {}", name, statefulRedisPubSubConnection);
                }
                beanContext.destroyBean(statefulRedisPubSubConnection);
            });
            return System.nanoTime() - beforeStart;
        });
    }

    @Override
    public int getOrder() {
        return NettyEmbeddedServerResource.ORDER - 1;
    }
}

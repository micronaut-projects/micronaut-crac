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

import io.micronaut.configuration.lettuce.cache.RedisCache;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Experimental;
import io.micronaut.crac.CracEventPublisher;
import io.micronaut.crac.CracResourceRegistrar;
import io.micronaut.crac.resources.NettyEmbeddedServerResource;
import org.crac.Context;
import org.crac.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Destroys any RedisCache beans before checkpointing.
 *
 * @author Tim Yates
 * @since 1.2.1
 */
@Experimental
@EachBean(RedisCache.class)
@Requires(classes = {RedisCache.class})
@Requires(bean = CracResourceRegistrar.class)
public class RedisCacheResource extends AbstractRedisResource {

    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheResource.class);

    private final BeanContext beanContext;
    private final CracEventPublisher eventPublisher;
    private final RedisCache redisCache;

    public RedisCacheResource(
        BeanContext beanContext,
        CracEventPublisher eventPublisher,
        RedisCache redisCache
    ) {
        this.beanContext = beanContext;
        this.eventPublisher = eventPublisher;
        this.redisCache = redisCache;
    }

    @Override
    public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
        eventPublisher.fireBeforeCheckpointEvents(this, () -> {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Destroying Redis cache {}", redisCache);
            }
            long beforeStart = System.nanoTime();
            beanContext.destroyBean(redisCache);
            return System.nanoTime() - beforeStart;
        });
    }

    @Override
    public int getOrder() {
        return NettyEmbeddedServerResource.ORDER - 2;
    }
}

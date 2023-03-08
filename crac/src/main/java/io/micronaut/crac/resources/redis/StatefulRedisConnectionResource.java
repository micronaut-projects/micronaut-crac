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

import io.lettuce.core.api.StatefulRedisConnection;
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
 * Destroys any StatefulRedisConnection beans before checkpointing.
 *
 * @author Tim Yates
 * @since 1.2.1
 */
@Experimental
@EachBean(StatefulRedisConnection.class)
@Requires(classes = {StatefulRedisConnection.class})
@Requires(bean = CracResourceRegistrar.class)
public class StatefulRedisConnectionResource extends AbstractRedisResource {

    private static final Logger LOG = LoggerFactory.getLogger(StatefulRedisConnectionResource.class);

    private final BeanContext beanContext;
    private final CracEventPublisher eventPublisher;
    private final StatefulRedisConnection<?, ?> connection;

    public StatefulRedisConnectionResource(
        BeanContext beanContext,
        CracEventPublisher eventPublisher,
        StatefulRedisConnection<?, ?> connection
    ) {
        this.beanContext = beanContext;
        this.eventPublisher = eventPublisher;
        this.connection = connection;
    }

    @Override
    public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
        eventPublisher.fireBeforeCheckpointEvents(this, () -> {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Destroying Redis stateful connection {}", connection);
            }
            long beforeStart = System.nanoTime();
            beanContext.destroyBean(connection);
            return System.nanoTime() - beforeStart;
        });
    }

    @Override
    public int getOrder() {
        return NettyEmbeddedServerResource.ORDER - 1;
    }
}

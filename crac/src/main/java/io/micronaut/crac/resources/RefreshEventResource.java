/*
 * Copyright 2017-2022 original authors
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
package io.micronaut.crac.resources;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.util.StringUtils;
import io.micronaut.crac.CracConfigurationProperties;
import io.micronaut.crac.CracEventPublisher;
import io.micronaut.crac.OrderedResource;
import io.micronaut.runtime.context.scope.refresh.RefreshEvent;
import jakarta.inject.Singleton;
import org.crac.Context;
import org.crac.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An OrderedResource that emits a RefreshEvent when a CRaC checkpoint is taken.
 *
 * @author Tim Yates
 * @since 1.0.0
 */
@Singleton
@Requires(property = CracConfigurationProperties.PREFIX + ".refresh-beans", defaultValue = StringUtils.TRUE, value = StringUtils.TRUE)
public class RefreshEventResource implements OrderedResource {

    private static final Logger LOG = LoggerFactory.getLogger(RefreshEventResource.class);

    private final CracEventPublisher cracEventPublisher;
    private final ApplicationEventPublisher<RefreshEvent> refreshEventPublisher;

    /**
     * @param cracEventPublisher    The CRaC event publisher
     * @param refreshEventPublisher The publisher to use to emit the RefreshEvent
     */
    public RefreshEventResource(
        CracEventPublisher cracEventPublisher,
        ApplicationEventPublisher<RefreshEvent> refreshEventPublisher
    ) {
        this.cracEventPublisher = cracEventPublisher;
        this.refreshEventPublisher = refreshEventPublisher;
    }

    @Override
    public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Before checkpoint, firing events");
        }
        refreshEventPublisher.publishEvent(new RefreshEvent());
        cracEventPublisher.fireBeforeCheckpointEvents(this);
    }

    @Override
    public void afterRestore(Context<? extends Resource> context) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("After restore, firing events");
        }
        cracEventPublisher.fireAfterRestoreEvents(this);
    }

    @Override
    public int getOrder() {
        // CRaC calls the resources in reverse order of registration when checkpointing, and this needs to come BEFORE netty shuts the ApplicationContext
        return NettyEmbeddedServerResource.ORDER + 1;
    }
}

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

import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Experimental;
import io.micronaut.crac.CracEventPublisher;
import io.micronaut.crac.CracResourceRegistrar;
import io.micronaut.crac.OrderedResource;
import io.micronaut.http.server.netty.NettyEmbeddedServer;
import org.crac.Context;
import org.crac.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Register the NettyEmbedded server as a CRaC resource on startup if CRaC is enabled.
 *
 * @author Tim Yates
 * @since 1.0.0
 */
@Experimental
@EachBean(NettyEmbeddedServer.class)
@Requires(classes = {NettyEmbeddedServer.class})
@Requires(bean = CracResourceRegistrar.class)
public class NettyEmbeddedServerResource implements OrderedResource {

    /**
     * The default order for this resource.
     */
    public static final int ORDER = 0;

    private static final Logger LOG = LoggerFactory.getLogger(NettyEmbeddedServerResource.class);

    private final CracEventPublisher eventPublisher;
    private final NettyEmbeddedServer server;

    public NettyEmbeddedServerResource(
        CracEventPublisher eventPublisher,
        NettyEmbeddedServer server
    ) {
        this.eventPublisher = eventPublisher;
        this.server = server;
    }

    @Override
    public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
        eventPublisher.fireBeforeCheckpointEvents(this, () -> {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Stopping netty server {}", server);
            }
            long beforeStart = System.nanoTime();
            server.stop();
            return System.nanoTime() - beforeStart;
        });
    }

    @Override
    public void afterRestore(Context<? extends Resource> context) throws Exception {
        eventPublisher.fireAfterRestoreEvents(this, () -> {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Starting netty server {}", server);
            }
            long beforeStart = System.nanoTime();
            server.start();
            return System.nanoTime() - beforeStart;
        });
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}

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
package io.micronaut.crac.netty;

import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.crac.support.CracContext;
import io.micronaut.crac.support.CracResourceRegistrar;
import io.micronaut.crac.support.OrderedCracResource;
import io.micronaut.http.server.netty.NettyEmbeddedServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Register the NettyEmbedded server as a CRaC resource on startup if CRaC is enabled.
 *
 * @author Tim Yates
 * @since 3.7.0
 */
@Experimental
@EachBean(NettyEmbeddedServer.class)
@Requires(bean = CracResourceRegistrar.class)
public class NettyEmbeddedServerCracHander implements OrderedCracResource {

    private static final Logger LOG = LoggerFactory.getLogger(NettyEmbeddedServerCracHander.class);

    private final NettyEmbeddedServer server;

    public NettyEmbeddedServerCracHander(NettyEmbeddedServer server) {
        this.server = server;
    }

    @Override
    public void beforeCheckpoint(@NonNull CracContext context) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Stopping netty server {}", server);
        }
        server.stop();
    }

    @Override
    public void afterRestore(@NonNull CracContext context) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Starting netty server {}", server);
        }
        server.start();
    }
}

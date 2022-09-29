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
package io.micronaut.crac.resources.datasources;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.crac.Context;
import org.crac.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Handler for Hikari DataSources.
 *
 * @author Tim Yates
 * @since 1.1.0
 */
public class HikariDataSourceResource implements Resource {

    private static final Logger LOG = LoggerFactory.getLogger(HikariDataSourceResource.class);

    private final HikariPoolMXBean poolBean;
    private final HikariDataSource dataSource;

    public HikariDataSourceResource(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        if (!dataSource.isAllowPoolSuspension()) {
            LOG.error("{} is not configured to allow pool suspension. This will cause problems when the application is checkpointed. Please set configuration datasources.*.allow-pool-suspension to fix this", dataSource);
        }
        this.poolBean = dataSource.getHikariPoolMXBean();
    }

    @Override
    public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suspending Hikari pool for {}", dataSource);
        }
        poolBean.suspendPool();
        poolBean.softEvictConnections();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Waiting 30s for connections to be closed");
        }
        CompletableFuture<Void> awaitClosure = CompletableFuture.runAsync(this::waitForConnectionClosure);
        awaitClosure.get(30, TimeUnit.SECONDS);
    }

    @Override
    public void afterRestore(Context<? extends Resource> context) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Resuming hikari datasource {}", dataSource);
        }
        poolBean.resumePool();
    }

    private void waitForConnectionClosure() {
        while (poolBean.getActiveConnections() > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Interrupted while waiting for connections to be closed", e);
                }
                Thread.currentThread().interrupt();
            }
        }
    }
}

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

import com.zaxxer.hikari.HikariDataSource;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Experimental;
import io.micronaut.crac.CracConfiguration;
import io.micronaut.crac.CracEventPublisher;
import io.micronaut.crac.CracResourceRegistrar;
import io.micronaut.crac.OrderedResource;
import io.micronaut.crac.resources.datasources.HikariDataSourceResource;
import io.micronaut.crac.resources.datasources.UnknownDataSourceResource;
import io.micronaut.crac.resources.datasources.resolver.DataSourceResolver;
import jakarta.inject.Inject;
import org.crac.Context;
import org.crac.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * Register DataSources as CRaC resources on startup if CRaC is enabled.
 *
 * @author Tim Yates
 * @since 1.1.0
 */
@Experimental
@EachBean(DataSource.class)
@Requires(classes = {DataSource.class})
@Requires(bean = CracResourceRegistrar.class)
public class DataSourceResource implements OrderedResource {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceResource.class);

    private final CracEventPublisher eventPublisher;
    private final Resource handler;
    private final DataSourceResolver dataSourceResolver;

    /**
     * @deprecated Use {@link #DataSourceResource(CracConfiguration, CracEventPublisher, DataSource, DataSourceResolver)} instead
     *
     * @param configuration
     * @param eventPublisher
     * @param dataSource
     */
    @Deprecated
    public DataSourceResource(
        CracConfiguration configuration,
        CracEventPublisher eventPublisher,
        DataSource dataSource
    ) {
        this(configuration, eventPublisher, dataSource, dataSource1 -> Optional.empty());
    }

    @Inject
    public DataSourceResource(
        CracConfiguration configuration,
        CracEventPublisher eventPublisher,
        DataSource dataSource,
        DataSourceResolver dataSourceResolver
    ) {
        this.eventPublisher = eventPublisher;
        this.dataSourceResolver = dataSourceResolver;
        this.handler = getHandler(dataSource, configuration);
    }

    private Resource getHandler(DataSource dataSource, CracConfiguration configuration) {
        Optional<DataSource> resolvedDataSourceOptional = dataSourceResolver.resolve(dataSource);
        if (resolvedDataSourceOptional.isPresent()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("HikariDataSource detected, using HikariDataSourceResource");
            }
            return new HikariDataSourceResource((HikariDataSource) resolvedDataSourceOptional.get(), configuration);
        }

        if (LOG.isWarnEnabled()) {
            LOG.warn("DataSource {} is not currently supported by CRaC", dataSource.getClass().getName());
        }
        return new UnknownDataSourceResource(dataSource);
    }

    @Override
    public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
        eventPublisher.fireBeforeCheckpointEvents(this, () -> {
            long beforeStart = System.nanoTime();
            try {
                handler.beforeCheckpoint(context);
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Error stopping datasource {}", handler, e);
                }
            }
            return System.nanoTime() - beforeStart;
        });
    }

    @Override
    public void afterRestore(Context<? extends Resource> context) throws Exception {
        eventPublisher.fireAfterRestoreEvents(this, () -> {
            long beforeStart = System.nanoTime();
            try {
                handler.afterRestore(context);
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Error restoring datasource {}", handler, e);
                }
            }
            return System.nanoTime() - beforeStart;
        });
    }
}

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
package io.micronaut.crac.resources.datasources.resolver;

import com.zaxxer.hikari.HikariDataSource;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.crac.CracConfiguration;
import io.micronaut.crac.resources.datasources.HikariDataSourceResource;
import io.micronaut.data.connection.jdbc.advice.DelegatingDataSource;
import jakarta.inject.Singleton;
import org.crac.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * If the data Source is of type {@link DelegatingDataSource}, extract the underlying data source.
 *
 * @author Sergio del Amo
 * @since 1.2.0
 */
@Singleton
@Experimental
@Requires(classes = HikariDataSource.class, missing = { DelegatingDataSource.class })
public class HikariDataSourceResolver implements DataSourceResourceResolver {

    static final int ORDER = 1;

    private static final Logger LOG = LoggerFactory.getLogger(HikariDataSourceResolver.class);

    @Override
    @NonNull
    public Optional<Resource> resolve(@NonNull DataSource dataSource, @NonNull CracConfiguration configuration) {
        return resourceForNonDelegatingDataSource(dataSource, configuration);
    }

    static Optional<Resource> resourceForNonDelegatingDataSource(DataSource dataSource, CracConfiguration configuration) {
        return dataSource instanceof HikariDataSource ?
            Optional.of(dataSource).map(ds -> {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("HikariDataSource detected, using HikariDataSourceResource");
                }
                return new HikariDataSourceResource((HikariDataSource) ds, configuration);
            }) : Optional.empty();
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}

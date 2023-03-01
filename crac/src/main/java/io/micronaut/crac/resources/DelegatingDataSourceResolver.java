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
package io.micronaut.crac.resources;


import io.micronaut.context.annotation.Requires;
import io.micronaut.transaction.jdbc.DelegatingDataSource;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * if the data Source is of type {@link io.micronaut.transaction.jdbc.DelegatingDataSource}
 * @author Sergio del Amo
 * @since 1.2.0
 */
@Requires(classes = DelegatingDataSource.class)
@Singleton
public class DelegatingDataSourceResolver implements DataSourceResolver {
    private static final Logger LOG = LoggerFactory.getLogger(DelegatingDataSourceResolver.class);
    @Override
    public Optional<DataSource> resolve(DataSource dataSource) {
        if (dataSource instanceof DelegatingDataSource) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("DelegatingDataSource detected, unwrapping");
            }
            DataSource result  = DelegatingDataSource.unwrapDataSource(dataSource);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unwrapped DataSource is {}", result.getClass().getName());
            }
            return Optional.of(result);
        }
        return Optional.empty();
    }

    //TODO override order
}

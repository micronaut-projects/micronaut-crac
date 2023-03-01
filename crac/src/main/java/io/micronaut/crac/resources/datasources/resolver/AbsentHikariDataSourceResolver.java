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
import jakarta.inject.Singleton;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * If Hikari is missing, we cannot handle this DataSource, so return empty.
 *
 * @author Tim Yates
 * @since 1.2.0
 */
@Singleton
@Experimental
@Requires(missing = HikariDataSource.class)
public class AbsentHikariDataSourceResolver implements DataSourceResolver {

    @Override
    @NonNull
    public Optional<DataSource> resolve(@NonNull DataSource dataSource) {
        return Optional.empty();
    }

    @Override
    public int getOrder() {
        return DelegatingDataSourceResolver.ORDER + 1;
    }
}

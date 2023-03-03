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

import io.micronaut.context.annotation.Primary;
import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.crac.CracConfiguration;
import jakarta.inject.Singleton;
import org.crac.Resource;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

/**
 * Composite DataSourceResolver that delegates to a list of resolvers.
 *
 * @author Tim Yates
 * @since 1.2.0
 */
@Primary
@Singleton
@Experimental
public class CompositeDataSourceResolver implements DataSourceResourceResolver {

    private final List<DataSourceResourceResolver> resolvers;

    public CompositeDataSourceResolver(List<DataSourceResourceResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    @NonNull
    public Optional<Resource> resolve(@NonNull DataSource dataSource, @NonNull CracConfiguration configuration) {
        return resolvers.stream()
            .map(resolver -> resolver.resolve(dataSource, configuration))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
    }
}

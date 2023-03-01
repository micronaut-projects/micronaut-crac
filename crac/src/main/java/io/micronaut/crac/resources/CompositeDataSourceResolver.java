package io.micronaut.crac.resources;

import io.micronaut.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Primary
public class CompositeDataSourceResolver implements DataSourceResolver {

    private final List<DataSourceResolver> resolvers;

    public CompositeDataSourceResolver(List<DataSourceResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public Optional<DataSource> resolve(DataSource dataSource) {
//TODO do with the stream api;
        for (DataSourceResolver resolver : resolvers) {
            Optional<DataSource> dataSourceOptional = resolver.resolve(dataSource);
            if (dataSourceOptional.isPresent()) {
                return dataSourceOptional;
            }
        }
        return Optional.of(dataSource);
    }
}

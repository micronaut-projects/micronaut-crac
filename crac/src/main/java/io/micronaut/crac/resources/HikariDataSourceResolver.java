package io.micronaut.crac.resources;

import jakarta.inject.Singleton;

import javax.sql.DataSource;
import java.util.Optional;

@Singleton
public class HikariDataSourceResolver implements DataSourceResolver {
    @Override
    public Optional<DataSource> resolve(DataSource dataSource) {
        return Optional.empty();
    }

    //TODO override order this should go after {@link DelgatingDataSourceResolver}
}

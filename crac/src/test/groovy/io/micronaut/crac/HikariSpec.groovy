package io.micronaut.crac

import com.zaxxer.hikari.HikariDataSource
import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.crac.test.CheckpointSimulator
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

import javax.sql.DataSource

@MicronautTest
@Property(name = "datasources.default.url", value = "jdbc:h2:mem:testdb")
@Property(name = "datasources.default.username", value = "sa")
@Property(name = "datasources.default.password", value = "")
@Property(name = "datasources.default.driver-class-name", value = "org.h2.Driver")
@Property(name = "datasources.default.allow-pool-suspension", value = "true")
class HikariSpec extends Specification {

    @Inject
    BeanContext ctx

    void "hikari is suspended and restarted"() {
        when:
        DataSource dataSource = ctx.getBean(DataSource)

        then:
        dataSource instanceof HikariDataSource

        when:
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource

        then: "Pool is configured as suspendable"
        hikariDataSource.allowPoolSuspension
        hikariDataSource.running

        when:
        CheckpointSimulator simulator = ctx.getBean(CheckpointSimulator)
        simulator.runBeforeCheckpoint()

        then:
        !hikariDataSource.running

        when:
        simulator.runAfterRestore()

        then:
        hikariDataSource.running
    }
}

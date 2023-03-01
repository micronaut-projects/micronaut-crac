package io.micronaut.crac

import com.zaxxer.hikari.HikariDataSource
import io.micronaut.context.annotation.Property
import io.micronaut.crac.test.CheckpointSimulator
import io.micronaut.runtime.server.EmbeddedServer
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
class HikariAndJooqSpec extends Specification {

    @Inject
    EmbeddedServer server

    void "application starts"() {
        expect:
        server.running

        when:
        DataSource dataSource = server.applicationContext.getBean(DataSource)
        CheckpointSimulator simulator = server.applicationContext.getBean(CheckpointSimulator)
        simulator.runBeforeCheckpoint()

        then:
        !dataSource.running

        when: "we trigger a restore"
        simulator.runAfterRestore()

        then:
        dataSource.running
    }
}


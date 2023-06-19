package io.micronaut.crac

import ch.qos.logback.classic.Logger
import com.zaxxer.hikari.HikariDataSource
import groovy.sql.Sql
import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.crac.test.CheckpointSimulator
import io.micronaut.data.connection.jdbc.advice.DelegatingDataSource
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import spock.lang.AutoCleanup
import spock.lang.Specification

import javax.sql.DataSource

@MicronautTest
@Property(name = "datasources.default.url", value = "jdbc:h2:mem:testdb")
@Property(name = "datasources.default.username", value = "sa")
@Property(name = "datasources.default.password", value = "")
@Property(name = "datasources.default.driver-class-name", value = "org.h2.Driver")
@Property(name = "datasources.default.allow-pool-suspension", value = "true")
@Property(name = "crac.datasource-pause-timeout", value = "PT10S")
class HikariSpec extends Specification {

    @Inject
    BeanContext ctx

    @AutoCleanup("stop")
    MemoryAppender appender = new MemoryAppender()

    void "hikari is suspended and restarted"() {
        given:
        Logger l = (Logger) LoggerFactory.getLogger("io.micronaut.crac.resources")
        l.addAppender(appender)
        appender.start()

        when:
        DataSource dataSource = ctx.getBean(DataSource)

        then:
        dataSource instanceof DelegatingDataSource

        when:
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource.targetDataSource

        then: "Pool is configured as suspendable"
        hikariDataSource.allowPoolSuspension
        hikariDataSource.running

        when: "we make sure we've got a connection warmed up"
        def rows = new Sql(hikariDataSource).rows("select 1")

        then:
        rows.size() == 1
        rows[0].values()[0] == 1

        when: "we trigger a checkpoint"
        CheckpointSimulator simulator = ctx.getBean(CheckpointSimulator)
        simulator.runBeforeCheckpoint()

        then:
        !hikariDataSource.running

        and:
        appender.events.formattedMessage.any { it.contains("Waiting PT10S for connections to be closed") }

        when: "we trigger a restore"
        simulator.runAfterRestore()

        then:
        hikariDataSource.running
    }
}

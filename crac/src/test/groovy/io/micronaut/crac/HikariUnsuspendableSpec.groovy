package io.micronaut.crac

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.zaxxer.hikari.HikariDataSource
import groovy.sql.Sql
import io.micronaut.context.ApplicationContext
import io.micronaut.context.BeanContext
import io.micronaut.crac.test.CheckpointSimulator
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import spock.lang.Specification

import javax.sql.DataSource

class HikariUnsuspendableSpec extends Specification {

    @Inject
    BeanContext ctx

    void "hikari is suspended and restarted"() {
        given:
        ApplicationContext ctx = ApplicationContext.builder()
                .properties(
                        "datasources.default.url": "jdbc:h2:mem:testdb",
                        "datasources.default.username": "sa",
                        "datasources.default.password": "",
                        "datasources.default.driver-class-name": "org.h2.Driver",
                )
                .build()
        def appender = new MemoryAppender()
        Logger l = (Logger) LoggerFactory.getLogger("io.micronaut.crac.resources")
        l.addAppender(appender)
        appender.start()

        when:
        ctx.start()
        DataSource dataSource = ctx.getBean(DataSource)

        then:
        dataSource instanceof HikariDataSource

        when:
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource

        then: "Pool is unsuspendable"
        !hikariDataSource.allowPoolSuspension
        hikariDataSource.running

        when: "we make sure we've got a connection warmed up"
        def rows = new Sql(dataSource).rows("select 1")

        then:
        rows.size() == 1
        rows[0].values()[0] == 1

        when: "we trigger a checkpoint"
        CheckpointSimulator simulator = ctx.getBean(CheckpointSimulator)
        simulator.runBeforeCheckpoint()

        then: 'we get a useful error message'
        appender.events.find {
            it.message.contains('This will cause problems when the application is checkpointed. Please set configuration datasources.*.allow-pool-suspension to fix this') &&
                    it.level == Level.ERROR
        }

        and: 'the failure is logged'
        appender.events.find {
            it.message.contains('Error stopping datasource') &&
                    it.level == Level.ERROR
        }

        and: 'the pool is still running'
        hikariDataSource.running

        cleanup:
        appender.stop()
        ctx.close()
    }
}

package io.micronaut.crac

import ch.qos.logback.classic.Logger
import io.micronaut.context.annotation.Property
import io.micronaut.crac.test.CheckpointSimulator
import io.micronaut.runtime.server.EmbeddedServer
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
class HikariAndJooqSpec extends Specification {

    @Inject
    EmbeddedServer server

    @AutoCleanup("stop")
    MemoryAppender appender = new MemoryAppender()

    void "application starts"() {
        given:
        Logger l = (Logger) LoggerFactory.getLogger("io.micronaut.crac.resources")
        l.addAppender(appender)
        appender.start()

        expect:
        server.running

        when:
        DataSource dataSource = server.applicationContext.getBean(DataSource)
        CheckpointSimulator simulator = server.applicationContext.getBean(CheckpointSimulator)
        simulator.runBeforeCheckpoint()

        then:
        !dataSource.running

        and:
        appender.events.formattedMessage.any { it == "Suspending Hikari pool for HikariDataSource (HikariPool-1)" }

        when: "we trigger a restore"
        simulator.runAfterRestore()

        then:
        dataSource.running
    }
}


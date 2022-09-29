package io.micronaut.crac

import ch.qos.logback.classic.Logger
import io.micronaut.context.ApplicationContext
import io.micronaut.context.BeanContext
import io.micronaut.context.exceptions.BeanInstantiationException
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import spock.lang.Specification

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

        then:
        def exception = thrown(BeanInstantiationException)
        exception.message.contains("This will cause problems when the application is checkpointed. Please set configuration datasources.*.allow-pool-suspension to fix this")

        cleanup:
        appender.stop()
        ctx.close()
    }
}

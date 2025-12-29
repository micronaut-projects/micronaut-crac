package io.micronaut.crac

import io.micronaut.context.ApplicationContext
import io.micronaut.crac.test.CheckpointSimulator
import io.micronaut.crac.testcontainers.Redis
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import spock.lang.AutoCleanup
import spock.lang.Specification

@MicronautTest
class BaseCacheSpecification extends Specification implements TestPropertyProvider {

    @Override
    Map<String, String> getProperties() {
        return Redis.getProperties()
    }

    @Inject
    ApplicationContext ctx

    @Inject
    CheckpointSimulator simulator

    @AutoCleanup("stop")
    MemoryAppender appender = new MemoryAppender()
}

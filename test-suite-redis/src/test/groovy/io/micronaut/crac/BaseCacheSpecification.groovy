package io.micronaut.crac

import io.micronaut.context.ApplicationContext
import io.micronaut.crac.test.CheckpointSimulator
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.AutoCleanup
import spock.lang.Specification

@MicronautTest
class BaseCacheSpecification extends Specification {

    @Inject
    ApplicationContext ctx

    @Inject
    CheckpointSimulator simulator

    @AutoCleanup("stop")
    MemoryAppender appender = new MemoryAppender()
}

package io.micronaut.crac

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.micronaut.crac.test.CheckpointSimulator
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Specification

@MicronautTest
class CheckpointSimulatorTest extends Specification {

    //tag::test[]
    void 'emulate checkpoint'() {
        given:
        EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Environment.TEST)
        CheckpointSimulator checkpointSimulator = server.getApplicationContext().getBean(CheckpointSimulator.class) // <1>
        expect:
        server.isRunning()

        when:
        checkpointSimulator.runBeforeCheckpoint() // <2>
        then:
        !server.isRunning()

        when:
        checkpointSimulator.runAfterRestore() // <3>
        then:
        server.isRunning()
    }
    //end::test[]
}

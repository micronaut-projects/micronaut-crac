package io.micronaut.crac

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.micronaut.crac.test.CheckpointSimulator
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


@MicronautTest
class CheckpointSimulatorTest {

    //tag::test[]
    @Test
    fun emulateCheckpoint() {
        ApplicationContext.run(EmbeddedServer::class.java, Environment.TEST).use { server ->
            val checkpointSimulator = server.applicationContext.getBean( // <1>
                CheckpointSimulator::class.java
            )
            Assertions.assertTrue(server.isRunning)

            checkpointSimulator.runBeforeCheckpoint() // <2>
            Assertions.assertFalse(server.isRunning)

            checkpointSimulator.runAfterRestore() // <3>
            Assertions.assertTrue(server.isRunning)
        }
    }
    //end::test[]
}


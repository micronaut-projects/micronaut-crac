package io.micronaut.crac;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import io.micronaut.crac.test.CheckpointSimulator;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class CheckpointSimulatorTest {

    //tag::test[]
    @Test
    void emulateCheckpoint() {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Environment.TEST)) {
            CheckpointSimulator checkpointSimulator = server.getApplicationContext().getBean(CheckpointSimulator.class);  // <1>
            assertTrue(server.isRunning());

            checkpointSimulator.runBeforeCheckpoint(); // <2>
            assertFalse(server.isRunning());

            checkpointSimulator.runAfterRestore(); // <3>
            assertTrue(server.isRunning());
        }
    }
    //end::test[]
}

package io.micronaut.crac;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Property;
import io.micronaut.crac.test.CheckpointSimulator;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
@Property(name = "spec.name", value = OrderedResourceCheckpointSimulatorTest.SPEC_NAME)
public class OrderedResourceCheckpointSimulatorTest {

    public static final String SPEC_NAME = "OrderedResourceCheckpointSimulatorTest";

    //tag::test[]
    @Inject
    BeanContext ctx;

    @Test
    void testCustomOrderedResourceUsingCheckpointSimulator() {
        ResourceBean myBean = ctx.getBean(ResourceBean.class);
        CheckpointSimulator checkpointSimulator = ctx.getBean(CheckpointSimulator.class); // <1>
        assertTrue(myBean.isRunning());

        checkpointSimulator.runBeforeCheckpoint();  // <2>
        assertFalse(myBean.isRunning());

        checkpointSimulator.runAfterRestore(); // <3>
        assertTrue(myBean.isRunning());
    }
    //end::test[]
}

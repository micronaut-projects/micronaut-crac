package io.micronaut.crac;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.crac.test.CheckpointSimulator;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.crac.Context;
import org.crac.Resource;
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
        assertTrue(myBean.running);

        checkpointSimulator.runBeforeCheckpoint();  // <2>
        assertFalse(myBean.running);

        checkpointSimulator.runAfterRestore(); // <3>
        assertTrue(myBean.running);
    }

    @Requires(property = "spec.name", value = SPEC_NAME)
    //tag::bean[]
    @Singleton
    static class ResourceBean {

        private boolean running = true; // <1>

        boolean isRunning() {
            return running;
        }

        void stop() {
            this.running = false;
        }

        void start() {
            this.running = true;
        }
    }
    //end::bean[]

    @Requires(property = "spec.name", value = SPEC_NAME)
    //tag::resource[]
    @Singleton
    static class ResourceBeanResource implements OrderedResource { // <1>

        private final ResourceBean resourceBean;

        ResourceBeanResource(ResourceBean resourceBean) {
            this.resourceBean = resourceBean;
        }

        @Override
        public void beforeCheckpoint(Context<? extends Resource> context) throws Exception { // <2>
            resourceBean.stop();
        }

        @Override
        public void afterRestore(Context<? extends Resource> context) throws Exception { // <3>
            resourceBean.start();
        }
    }
    //end::resource[]
}

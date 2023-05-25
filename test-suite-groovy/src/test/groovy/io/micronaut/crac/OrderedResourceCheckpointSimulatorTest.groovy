package io.micronaut.crac

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.crac.test.CheckpointSimulator
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.crac.Context
import org.crac.Resource
import spock.lang.Specification

@MicronautTest
@Property(name = "spec.name", value = SPEC_NAME)
class OrderedResourceCheckpointSimulatorTest extends Specification {

    public static final String SPEC_NAME = "OrderedResourceCheckpointSimulatorTest"

    //tag::test[]
    @Inject
    BeanContext ctx

    void "test custom OrderedResource implementation using the CheckpointSimulator"() {
        given:
        ResourceBean myBean = ctx.getBean(ResourceBean)
        CheckpointSimulator checkpointSimulator = ctx.getBean(CheckpointSimulator) // <1>
        expect:
        myBean.running

        when:
        checkpointSimulator.runBeforeCheckpoint()  // <2>
        then:
        !myBean.running

        when:
        checkpointSimulator.runAfterRestore() // <3>

        then:
        myBean.running
    }
    //end::test[]

    @Requires(property = "spec.name", value = SPEC_NAME)
    //tag::bean[]
    @Singleton
    static class ResourceBean {

        private boolean running = true // <1>

        boolean isRunning() {
            return running
        }

        void stop() {
            this.running = false
        }

        void start() {
            this.running = true
        }
    }
    //end::bean[]

    @Requires(property = "spec.name", value = SPEC_NAME)
    //tag::resource[]
    @Singleton
    static class ResourceBeanResource implements OrderedResource { // <1>

        private final ResourceBean resourceBean

        ResourceBeanResource(ResourceBean resourceBean) {
            this.resourceBean = resourceBean
        }

        @Override
        void beforeCheckpoint(Context<? extends Resource> context) throws Exception { // <2>
            resourceBean.stop()
        }

        @Override
        void afterRestore(Context<? extends Resource> context) throws Exception { // <3>
            resourceBean.start()
        }
    }
    //end::resource[]
}

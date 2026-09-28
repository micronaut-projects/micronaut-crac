package io.micronaut.crac

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.crac.test.CheckpointSimulator
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
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
}

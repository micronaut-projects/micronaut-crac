package io.micronaut.crac

import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton

@Requires(property = "spec.name", value = OrderedResourceCheckpointSimulatorTest.SPEC_NAME)
//tag::bean[]
@Singleton
 class ResourceBean {

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

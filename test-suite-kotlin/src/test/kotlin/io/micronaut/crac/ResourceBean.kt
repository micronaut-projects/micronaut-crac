package io.micronaut.crac

import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton

@Requires(property = "spec.name", value = OrderedResourceCheckpointSimulatorTest.SPEC_NAME)
//tag::bean[]
@Singleton
class ResourceBean {

    var isRunning = true // <1>
        private set

    fun stop() {
        isRunning = false
    }

    fun start() {
        isRunning = true
    }
}
//end::bean[]

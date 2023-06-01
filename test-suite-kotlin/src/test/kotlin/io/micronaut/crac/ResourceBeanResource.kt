package io.micronaut.crac

import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import org.crac.Context
import org.crac.Resource

@Requires(property = "spec.name", value = OrderedResourceCheckpointSimulatorTest.SPEC_NAME)
//tag::resource[]
@Singleton
class ResourceBeanResource(private val resourceBean: ResourceBean) : OrderedResource { // <1>

    @Throws(Exception::class)
    override fun beforeCheckpoint(context: Context<out Resource?>?) { // <2>
        resourceBean.stop()
    }

    @Throws(Exception::class)
    override fun afterRestore(context: Context<out Resource?>?) { // <3>
        resourceBean.start()
    }
}
//end::resource[]

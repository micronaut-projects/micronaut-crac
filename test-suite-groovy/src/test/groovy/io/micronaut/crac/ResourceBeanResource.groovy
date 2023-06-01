package io.micronaut.crac

import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import org.crac.Context
import org.crac.Resource

@Requires(property = "spec.name", value = OrderedResourceCheckpointSimulatorTest.SPEC_NAME)
//tag::resource[]
@Singleton
 class ResourceBeanResource implements OrderedResource { // <1>

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

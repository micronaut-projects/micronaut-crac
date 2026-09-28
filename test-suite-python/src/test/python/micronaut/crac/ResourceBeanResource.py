from jakarta.inject import Singleton
from micronaut.context.annotation import Requires
from micronaut.crac import OrderedResource
from org.crac import Context, Resource

from .ResourceBean import ResourceBean


@Requires(property="spec.name", value="OrderedResourceCheckpointSimulatorTest")
# tag::resource[]
@Singleton
class ResourceBeanResource(OrderedResource):  # <1>

    def __init__(self, resource_bean: ResourceBean):
        self.resource_bean = resource_bean

    def beforeCheckpoint(self, context: Context[Resource]) -> None:  # <2>
        self.resource_bean.stop()

    def afterRestore(self, context: Context[Resource]) -> None:  # <3>
        self.resource_bean.start()
# end::resource[]

from typing import Annotated

import java
from jakarta.inject import Inject
from micronaut.context import BeanContext
from micronaut.context.annotation import Property
from micronaut.test.extensions.junit5.annotation import MicronautTest
from org.junit.jupiter.api import Test

from .ResourceBean import ResourceBean

# TODO(python): the imported micronaut.crac.test.CheckpointSimulator shim cannot be used as a runtime type argument of
# BeanContext.getBean ("TypeError: invalid instantiation of foreign object"), only a java.type(...) alias can
CheckpointSimulator = java.type("io.micronaut.crac.test.CheckpointSimulator")


@MicronautTest
@Property(name="spec.name", value="OrderedResourceCheckpointSimulatorTest")
class OrderedResourceCheckpointSimulatorTest:

    # tag::test[]
    ctx: Annotated[BeanContext, Inject]

    @Test
    def test_custom_ordered_resource_using_checkpoint_simulator(self):
        my_bean = self.ctx.getBean(ResourceBean).asPolyglotValue()
        checkpoint_simulator = self.ctx.getBean(CheckpointSimulator)  # <1>
        assert my_bean.is_running()

        checkpoint_simulator.runBeforeCheckpoint()  # <2>
        assert not my_bean.is_running()

        checkpoint_simulator.runAfterRestore()  # <3>
        assert my_bean.is_running()
    # end::test[]

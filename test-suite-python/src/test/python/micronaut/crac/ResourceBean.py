from jakarta.inject import Singleton
from micronaut.context.annotation import Requires


@Requires(property="spec.name", value="OrderedResourceCheckpointSimulatorTest")
# tag::bean[]
@Singleton
class ResourceBean:

    def __init__(self):
        self.running = True  # <1>

    def is_running(self) -> bool:
        return self.running

    def stop(self) -> None:
        self.running = False

    def start(self) -> None:
        self.running = True
# end::bean[]

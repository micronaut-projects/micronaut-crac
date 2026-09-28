from functools import cached_property
from typing import Annotated

from jakarta.inject import Inject
from micronaut.context.annotation import Property, Requires
from micronaut.http.annotation import Controller, Get
from micronaut.http.client import HttpClient
from micronaut.runtime.server import EmbeddedServer
from micronaut.test.extensions.junit5.annotation import MicronautTest
from org.junit.jupiter.api import Test


@MicronautTest
@Property(name="spec.name", value="EagerHttpClientCreationTest")
class EagerHttpClientCreationTest:

    # tag::test[]
    server: Annotated[EmbeddedServer, Inject]  # <1>

    @cached_property
    def client(self) -> HttpClient:  # <2>
        return self.server.getApplicationContext().createBean(HttpClient, self.server.getURL())

    @Test
    def test_client(self):
        assert self.client.toBlocking().retrieve("/eager") == "ok"  # <3>
    # end::test[]


@Requires(property="spec.name", value="EagerHttpClientCreationTest")
@Controller("/eager")
class EagerController:

    @Get
    def test(self) -> str:
        return "ok"

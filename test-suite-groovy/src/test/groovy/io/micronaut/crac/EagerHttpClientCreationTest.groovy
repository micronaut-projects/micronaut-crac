package io.micronaut.crac

import groovy.transform.Memoized
import io.micronaut.context.DefaultApplicationContextBuilder
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest(contextBuilder = EagerSingletons)
@Property(name = "spec.name", value = "EagerHttpClientCreationTest")
class EagerHttpClientCreationTest extends Specification {

    //tag::test[]
    @Inject
    EmbeddedServer server // <1>

    @Memoized // <2>
    HttpClient clientSupplier() {
        server.applicationContext.createBean(HttpClient, server.URL)
    }

    void 'test client'() {
        expect:
        clientSupplier().toBlocking().retrieve("/eager") == "ok" // <3>
    }
    //end::test[]

    @Requires(property = "spec.name", value = "EagerHttpClientCreationTest")
    @Controller("/eager")
    static class EagerController {

        @Get
        String test() {
            return "ok"
        }
    }

    @Introspected
    static class EagerSingletons extends DefaultApplicationContextBuilder {

        EagerSingletons() {
            eagerInitSingletons(true)
        }
    }
}


package io.micronaut.crac

import groovy.transform.Memoized
import io.micronaut.context.DefaultApplicationContextBuilder
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest(contextBuilder = EagerContextBuilder)
@Property(name = "spec.name", value = "EagerInitializationSpec")
class EagerInitialisationSpec extends Specification {

    // tag::eager[]
    @Inject
    EmbeddedServer server; // <1>

    @Memoized
    HttpClient client() {
        server.applicationContext.createBean(HttpClient.class, server.getURL()) // <2>
    }

    void 'test eager singleton'() {
        expect:
        client().toBlocking().retrieve("/eager") == 'eager' // <3>
    }
    // end::eager[]

    @Requires(property = "spec.name", value = "EagerInitializationSpec")
    @Controller("/eager")
    public static class EagerController {
        @Get
        String test() {
            "eager";
        }
    }

    static class EagerContextBuilder extends DefaultApplicationContextBuilder {

        EagerContextBuilder() {
            eagerInitSingletons(true)
        }
    }
}

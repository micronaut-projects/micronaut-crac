package io.micronaut.crac;

import io.micronaut.context.DefaultApplicationContextBuilder;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.util.SupplierUtil;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(contextBuilder = EagerHttpClientCreationTest.EagerSingletons.class)
@Property(name = "spec.name", value = "EagerHttpClientCreationTest")
class EagerHttpClientCreationTest {

    //tag::test[]
    @Inject
    EmbeddedServer server; // <1>

    Supplier<HttpClient> clientSupplier = SupplierUtil.memoizedNonEmpty(() -> // <2>
        server.getApplicationContext().createBean(HttpClient.class, server.getURL())
    );

    @Test
    void testClient() {
        assertEquals("ok", clientSupplier.get().toBlocking().retrieve("/eager")); // <3>
    }
    //end::test[]

    @Requires(property = "spec.name", value = "EagerHttpClientCreationTest")
    @Controller("/eager")
    static class EagerController {

        @Get
        String test() {
            return "ok";
        }
    }

    @Introspected
    static class EagerSingletons extends DefaultApplicationContextBuilder {

        EagerSingletons() {
            eagerInitSingletons(true);
        }
    }
}


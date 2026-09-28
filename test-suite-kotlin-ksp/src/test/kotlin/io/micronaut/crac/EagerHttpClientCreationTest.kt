package io.micronaut.crac

import io.micronaut.context.DefaultApplicationContextBuilder
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@MicronautTest(contextBuilder = [EagerHttpClientCreationTest.EagerSingletons::class])
@Property(name = "spec.name", value = "EagerHttpClientCreationTest")
class EagerHttpClientCreationTest {

    //tag::test[]
    @field:Inject
    lateinit var server: EmbeddedServer // <1>

    val client by lazy {
        server.applicationContext.createBean(HttpClient::class.java, server.url) // <2>
    }

    @Test
    fun testClient() {
        assertEquals("ok", client.toBlocking().retrieve("/eager")) // <3>
    }
    //end::test[]

    @Requires(property = "spec.name", value = "EagerHttpClientCreationTest")
    @Controller("/eager")
    internal class EagerController {
        @Get
        fun test(): String {
            return "ok"
        }
    }

    @Introspected
    internal class EagerSingletons : DefaultApplicationContextBuilder() {
        init {
            eagerInitSingletons(true)
        }
    }
}

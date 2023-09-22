package io.micronaut.crac

import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
@Property(name = "endpoints.info.sensitive", value = "false")
@Property(name = "info.demo.number", value = "123")
class InfoSpec extends Specification {

    @Inject
    @Client("/")
    HttpClient client

    void "hikari is suspended and restarted"() {
        given:
        Map map = client.toBlocking().retrieve(HttpRequest.GET("/info"), Map)

        expect:
        map.containsKey("demo")
        map.demo.number == '123'

        map.containsKey("crac")
        map.crac.containsKey("restore-time")
        map.crac.containsKey("uptime-since-restore")
    }
}

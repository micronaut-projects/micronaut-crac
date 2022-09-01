package io.micronaut.crac

import io.micronaut.context.BeanContext
import io.micronaut.http.server.netty.NettyEmbeddedServer
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class NettySpec extends Specification {

    @Inject
    BeanContext ctx

    void "checkpoint stops netty"() {
        given:
        NettyEmbeddedServer server = ctx.getBean(NettyEmbeddedServer)
        List<OrderedResource> handler = ctx.getBeansOfType(OrderedResource)

        when:
        handler*.beforeCheckpoint(null)

        then:
        !server.running
    }
}

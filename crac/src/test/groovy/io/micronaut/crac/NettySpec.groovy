package io.micronaut.crac

import io.micronaut.context.BeanContext
import io.micronaut.crac.test.CheckpointSimulator
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
        CheckpointSimulator simulator = ctx.getBean(CheckpointSimulator)

        when: "we call the resources in the reverse order, as per how CRaC does it"
        simulator.runBeforeCheckpoint()

        then:
        !server.running
    }
}

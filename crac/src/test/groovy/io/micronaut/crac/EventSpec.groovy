package io.micronaut.crac

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Requires
import io.micronaut.crac.events.AfterRestoreEvent
import io.micronaut.crac.events.BeforeCheckpointEvent
import io.micronaut.crac.netty.NettyEmbeddedServerCracHander
import io.micronaut.http.server.netty.NettyEmbeddedServer
import io.micronaut.runtime.event.annotation.EventListener
import spock.lang.Specification
import jakarta.inject.Singleton

class EventSpec extends Specification {

    def "netty handler fires events"() {
        given:
        NettyEmbeddedServer server = ApplicationContext.run(NettyEmbeddedServer, ['spec.name': 'EventSpec'])
        ApplicationContext ctx = server.getApplicationContext()
        NettyEmbeddedServerCracHander handler = ctx.getBean(NettyEmbeddedServerCracHander)

        when:
        EventRecorder.clearEvents()
        handler.beforeCheckpoint(null)
        handler.afterRestore(null)

        then:
        EventRecorder.events == ["onCheckpoint", "onRestore"]

        cleanup:
        server.close()
        ctx.close()
    }

    @Singleton
    @Requires(property = "spec.name", value = "EventSpec")
    @SuppressWarnings(['unused', 'GrMethodMayBeStatic'])
    static class EventRecorder {

        static List<String> events = []

        @EventListener
        void checkpointEvent(BeforeCheckpointEvent event) {
            events << 'onCheckpoint'
        }

        @EventListener
        void restoreEvent(AfterRestoreEvent event) {
            events << 'onRestore'
        }

        static void clearEvents() {
            events.clear()
        }
    }
}

package io.micronaut.crac

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Requires
import io.micronaut.crac.events.AfterRestoreEvent
import io.micronaut.crac.events.BeforeCheckpointEvent
import io.micronaut.crac.netty.NettyEmbeddedServerCracHander
import io.micronaut.http.server.netty.NettyEmbeddedServer
import io.micronaut.runtime.context.scope.refresh.RefreshEvent
import io.micronaut.runtime.event.annotation.EventListener
import spock.lang.Specification
import jakarta.inject.Singleton

class EventSpec extends Specification {

    void "netty handler fires events when refreshing is #enabled"() {
        given:
        NettyEmbeddedServer server = ApplicationContext.run(NettyEmbeddedServer, ['spec.name': 'EventSpec', 'crac.refresh-beans': config])
        ApplicationContext ctx = server.getApplicationContext()
        NettyEmbeddedServerCracHander handler = ctx.getBean(NettyEmbeddedServerCracHander)

        when:
        EventRecorder.clearEvents()
        handler.beforeCheckpoint(null)
        handler.afterRestore(null)

        then:
        EventRecorder.events == refreshEvent + ["onCheckpoint", "onRestore"]

        cleanup:
        server.close()
        ctx.close()

        where:
        enabled    | config  | refreshEvent
        'enabled'  | 'true'  | ['onRefresh']
        'disabled' | 'false' | []
    }

    @Singleton
    @Requires(property = "spec.name", value = "EventSpec")
    @SuppressWarnings(['unused', 'GrMethodMayBeStatic'])
    static class EventRecorder {

        static List<String> events = []

        @EventListener
        void refreshEvent(RefreshEvent event) {
            events << "onRefresh"
        }

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

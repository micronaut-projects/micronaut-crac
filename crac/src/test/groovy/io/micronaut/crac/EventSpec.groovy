package io.micronaut.crac

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Requires
import io.micronaut.crac.events.AfterRestoreEvent
import io.micronaut.crac.events.BeforeCheckpointEvent
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
        List<OrderedResource> handler = ctx.getBeansOfType(OrderedResource)

        when:
        EventRecorder.clearEvents()
        handler*.beforeCheckpoint(null)
        handler*.afterRestore(null)

        then:
        EventRecorder.events == expected

        cleanup:
        server.close()
        ctx.close()

        where:
        enabled    | config  | expected
        'enabled'  | 'true'  | ['onRefresh[SingletonMap]', "onCheckpoint[RefreshEventResource]", "onCheckpoint[NettyEmbeddedServerResource]", "onRestore[RefreshEventResource]", "onRestore[NettyEmbeddedServerResource]"]
        'disabled' | 'false' | ["onCheckpoint[NettyEmbeddedServerResource]", "onRestore[NettyEmbeddedServerResource]"]
    }

    @Singleton
    @Requires(property = "spec.name", value = "EventSpec")
    @SuppressWarnings(['unused', 'GrMethodMayBeStatic'])
    static class EventRecorder {

        static List<String> events = []

        @EventListener
        void refreshEvent(RefreshEvent event) {
            events << "onRefresh[${event.source.getClass().simpleName}]"
        }

        @EventListener
        void checkpointEvent(BeforeCheckpointEvent event) {
            events << "onCheckpoint[${event.source.getClass().simpleName}]"
        }

        @EventListener
        void restoreEvent(AfterRestoreEvent event) {
            events << "onRestore[${event.source.getClass().simpleName}]"
        }

        static void clearEvents() {
            events.clear()
        }
    }
}

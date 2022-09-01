package io.micronaut.crac

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requires
import io.micronaut.crac.events.AfterRestoreEvent
import io.micronaut.crac.events.BeforeCheckpointEvent
import io.micronaut.crac.resources.NettyEmbeddedServerResource
import io.micronaut.http.server.netty.NettyEmbeddedServer
import io.micronaut.runtime.context.scope.refresh.RefreshEvent
import io.micronaut.runtime.event.annotation.EventListener
import org.crac.Context
import org.crac.Resource
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
        'enabled'  | 'true'  | ["onCheckpoint[MockNettyServerResource]", 'onRefresh[SingletonMap]', "onCheckpoint[RefreshEventResource]", "onRestore[MockNettyServerResource]", "onRestore[RefreshEventResource]"]
        'disabled' | 'false' | ["onCheckpoint[MockNettyServerResource]", "onRestore[MockNettyServerResource]"]
    }

    /**
     * We don't actually want to stop netty here, we just want to test the event order
     */
    @Singleton
    @Replaces(NettyEmbeddedServerResource)
    @Requires(property = "spec.name", value = "EventSpec")
    static class MockNettyServerResource implements OrderedResource {

        private final CracEventPublisher eventPublisher;

        MockNettyServerResource(CracEventPublisher eventPublisher) {
            this.eventPublisher = eventPublisher
        }

        @Override
        void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
            eventPublisher.fireBeforeCheckpointEvents(this)
        }

        @Override
        void afterRestore(Context<? extends Resource> context) throws Exception {
            eventPublisher.fireAfterRestoreEvents(this)
        }
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

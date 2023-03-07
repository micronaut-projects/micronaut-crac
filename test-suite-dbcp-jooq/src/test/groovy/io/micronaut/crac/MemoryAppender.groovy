package io.micronaut.crac

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase

class MemoryAppender extends AppenderBase<ILoggingEvent> {

    private final List<ILoggingEvent> events = []

    @Override
    protected void append(ILoggingEvent e) {
        synchronized (events) {
            events.add(e)
        }
    }

    List<ILoggingEvent> getEvents() {
        events
    }
}

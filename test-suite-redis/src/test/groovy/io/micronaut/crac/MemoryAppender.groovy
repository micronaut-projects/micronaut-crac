package io.micronaut.crac

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase

class MemoryAppender extends AppenderBase<ILoggingEvent> {

    final List<ILoggingEvent> events = []

    @Override
    protected void append(ILoggingEvent e) {
        synchronized (events) {
            events.add(e)
        }
    }
}

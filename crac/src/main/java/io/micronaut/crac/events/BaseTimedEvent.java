/*
 * Copyright 2017-2022 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.crac.events;

import io.micronaut.core.annotation.Experimental;
import io.micronaut.crac.OrderedResource;

import java.time.Instant;
import java.util.EventObject;

/**
 * The base class for timed CRaC events.
 *
 * @author Tim Yates
 * @since 1.0.0
 */
@Experimental
abstract class BaseTimedEvent extends EventObject {

    private final Instant now;
    private final long timeTakenNanos;

    /**
     * A base class for CRaC events.
     * @param resource The resource that triggered the event.
     * @param timeTakenNanos The time token for the action to be processed in nanoseconds.
     */
    BaseTimedEvent(OrderedResource resource, long timeTakenNanos) {
        super(resource);
        this.now = Instant.now();
        this.timeTakenNanos = timeTakenNanos;
    }

    /**
     * @return The {@link Instant} the event was fired.
     */
    public Instant getNow() {
        return now;
    }

    /**
     * @return The time token for the action to be processed in nanoseconds.
     */
    public long getTimeTakenNanos() {
        return timeTakenNanos;
    }
}

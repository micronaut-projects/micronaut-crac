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

/**
 * An event fired after a CRaC checkpoint is restored.
 *
 * @author Tim Yates
 * @since 1.0.0
 */
@Experimental
public class AfterRestoreEvent extends BaseTimedEvent {

    /**
     * @param resource The resource that triggered the event.
     * @param timeTakenNanos The time token for the action to be processed in nanoseconds.
     */
    public AfterRestoreEvent(OrderedResource resource, long timeTakenNanos) {
        super(resource, timeTakenNanos);
    }
}

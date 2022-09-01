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
package io.micronaut.crac;

import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.crac.events.AfterRestoreEvent;
import io.micronaut.crac.events.BeforeCheckpointEvent;
import jakarta.inject.Singleton;

import java.util.function.LongSupplier;

/**
 * A helper bean to assist in publishing the correct CRaC events when a checkpoint is taken or restored.
 *
 * @author Tim Yates
 * @since 1.0.0
 */
@Singleton
public class CracEventPublisher {

    private final ApplicationEventPublisher<BeforeCheckpointEvent> beforeCheckpointEventPublisher;
    private final ApplicationEventPublisher<AfterRestoreEvent> afterRestoreEventPublisher;

    /**
     * @param cracConfiguration The CRaC configuration.
     * @param beforeCheckpointEventPublisher The publisher for {@link BeforeCheckpointEvent} events.
     * @param afterRestoreEventPublisher The publisher for {@link AfterRestoreEvent} events.
     */
    public CracEventPublisher(
        CracConfiguration cracConfiguration,
        ApplicationEventPublisher<BeforeCheckpointEvent> beforeCheckpointEventPublisher,
        ApplicationEventPublisher<AfterRestoreEvent> afterRestoreEventPublisher
    ) {
        this.beforeCheckpointEventPublisher = beforeCheckpointEventPublisher;
        this.afterRestoreEventPublisher = afterRestoreEventPublisher;
    }

    /**
     * Fires a {@link BeforeCheckpointEvent} event.
     *
     * @param resource The @{link OrderedResource} that is being checkpointed.
     * @param action The action to perform that returns the time taken in nanoseconds.
     */
    public void fireBeforeCheckpointEvents(OrderedResource resource, LongSupplier action) {
        beforeCheckpointEventPublisher.publishEvent(new BeforeCheckpointEvent(resource, action.getAsLong()));
    }

    /**
     * Fires a {@link BeforeCheckpointEvent} event with no action to be performed.
     *
     * @param resource The @{link OrderedResource} that is being checkpointed.
     */
    public void fireBeforeCheckpointEvents(OrderedResource resource) {
        beforeCheckpointEventPublisher.publishEvent(new BeforeCheckpointEvent(resource, 0));
    }

    /**
     * Fires an {@link AfterRestoreEvent} event.
     *
     * @param resource The @{link OrderedResource} that is being restored.
     * @param action The action to perform that returns the time taken in nanoseconds.
     */
    public void fireAfterRestoreEvents(OrderedResource resource, LongSupplier action) {
        afterRestoreEventPublisher.publishEvent(new AfterRestoreEvent(resource, action.getAsLong()));
    }

    /**
     * Fires an {@link AfterRestoreEvent} event with no action to be performed.
     *
     * @param resource The @{link OrderedResource} that is being restored.
     */
    public void fireAfterRestoreEvents(OrderedResource resource) {
        afterRestoreEventPublisher.publishEvent(new AfterRestoreEvent(resource, 0));
    }
}

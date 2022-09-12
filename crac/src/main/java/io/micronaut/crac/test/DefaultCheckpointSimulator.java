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
package io.micronaut.crac.test;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.annotation.Experimental;
import io.micronaut.crac.OrderedResource;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Provides a mechanism to simulate a checkpoint/restore for testing.
 */
@Singleton
@Experimental
@Requires(env = Environment.TEST)
public class DefaultCheckpointSimulator implements CheckpointSimulator {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultCheckpointSimulator.class);

    private final List<OrderedResource> resources;

    public DefaultCheckpointSimulator(List<OrderedResource> resources) {
        this.resources = resources;
    }

    @Override
    public void runBeforeCheckpoint() {
        for (int i = resources.size() - 1; i >= 0; i--) {
            OrderedResource resource = resources.get(i);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Running before checkpoint for resource: {} ({})", resource, resource.getOrder());
            }
            try {
                resource.beforeCheckpoint(null);
            } catch (Exception e) {
                throw new SimulatorException(e);
            }
        }
    }

    @Override
    public void runAfterRestore() {
        resources.forEach(DefaultCheckpointSimulator::internalAfterRestore);
    }

    private static void internalAfterRestore(OrderedResource resource) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Running after restore for resource: {} ({})", resource, resource.getOrder());
            }
            resource.afterRestore(null);
        } catch (Exception e) {
            throw new SimulatorException(e);
        }
    }

    static class SimulatorException extends RuntimeException {

        public SimulatorException(Throwable cause) {
            super(cause);
        }
    }
}

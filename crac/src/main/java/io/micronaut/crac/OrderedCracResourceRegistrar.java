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

import io.micronaut.core.annotation.Experimental;
import jakarta.inject.Singleton;
import org.crac.Context;
import org.crac.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Registers all defined Resources for Coordinated Restore at Checkpoint.
 *
 * @author Tim Yates
 * @since 1.0.0
 */
@Experimental
@Singleton
public class OrderedCracResourceRegistrar implements CracResourceRegistrar {

    private static final Logger LOG = LoggerFactory.getLogger(OrderedCracResourceRegistrar.class);

    private final List<OrderedResource> resources;
    private final Context<Resource> context;

    /**
     * Collects together all available CRaC resources in the order specified.
     *
     * @param resources The ordered registered CRaC resources
     * @param contextProvider CRaC context provider
     */
    public OrderedCracResourceRegistrar(List<OrderedResource> resources,
                                        CracContextProvider contextProvider) {
        this.resources = resources;
        this.context = contextProvider.provideContext();
    }

    /**
     * For each known resource, register it with the CRaC context.
     */
    @Override
    public void registerResources() {
        resources.forEach(resource -> {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Registering CRaC resource: {}", resource.getClass().getName());
            }
            context.register(resource);
        });
    }
}

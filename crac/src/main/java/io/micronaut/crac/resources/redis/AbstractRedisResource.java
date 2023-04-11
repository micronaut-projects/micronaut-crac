/*
 * Copyright 2017-2023 original authors
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
package io.micronaut.crac.resources.redis;

import io.micronaut.context.BeanContext;
import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.annotation.Internal;
import io.micronaut.crac.OrderedResource;
import org.crac.Context;
import org.crac.Resource;
import org.slf4j.Logger;

/**
 * Redis resources are removed from the context, so they are automatically recreated on restore.
 *
 * @param <T> The type of resource
 * @author Tim Yates
 * @since 1.2.1
 */
@Internal
@Experimental
public abstract class AbstractRedisResource<T> implements OrderedResource {

    protected final BeanContext beanContext;

    protected AbstractRedisResource(BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    /**
     * Destroy the bean.
     * @param resource the bean to destroy
     * @param logger the logger to use
     * @param message the log message
     * @return the time taken to destroy the bean
     */
    protected long destroyAction(T resource, Logger logger, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, resource);
        }
        long beforeStart = System.nanoTime();
        beanContext.destroyBean(resource);
        return System.nanoTime() - beforeStart;
    }

    @Override
    public void afterRestore(Context<? extends Resource> context) throws Exception {
        // We destroy beans, so nothing occurs after restore
    }
}

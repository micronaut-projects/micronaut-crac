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

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.Experimental;
import io.micronaut.crac.CracConfigurationProperties;

/**
 * Configuration for Redis CRaC support. Enabled by default.
 *
 * @author Tim Yates
 * @since 1.2.0
 */
@Experimental
@ConfigurationProperties(CracRedisConfigurationProperties.PREFIX)
public class CracRedisConfigurationProperties implements CracRedisConfiguration {

    public static final String PREFIX = CracConfigurationProperties.PREFIX + ".redis";

    public static final boolean DEFAULT_ENABLED = true;
    public static final boolean DEFAULT_CACHE_ENABLED = true;
    public static final boolean DEFAULT_CLIENT_ENABLED = true;
    public static final boolean DEFAULT_CONNECTION_ENABLED = true;

    private boolean enabled = DEFAULT_ENABLED;
    private boolean cacheEnabled = DEFAULT_CACHE_ENABLED;
    private boolean clientEnabled = DEFAULT_CLIENT_ENABLED;
    private boolean connectionEnabled = DEFAULT_CONNECTION_ENABLED;

    /**
     * @return Whether CRaC support for Redis is enabled.
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Whether CRaC (Coordinated Restore at Checkpoint) support for Redis, even if we're on a supporting JDK, is enabled. Default value ({@value #DEFAULT_ENABLED}).
     *
     * @param enabled false if CRaC support for redis should be disabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    /**
     * Whether to destroy RedisCache beans prior to taking a checkpoint. Default value ({@value #DEFAULT_CACHE_ENABLED}).
     *
     * @param cacheEnabled Whether to destroy RedisCache beans prior to taking a checkpoint.
     */
    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    @Override
    public boolean isClientEnabled() {
        return clientEnabled;
    }

    /**
     * Whether to destroy RedisClient beans prior to taking a checkpoint. Default value ({@value #DEFAULT_CLIENT_ENABLED}).
     *
     * @param clientEnabled Whether to destroy RedisClient beans prior to taking a checkpoint.
     */
    public void setClientEnabled(boolean clientEnabled) {
        this.clientEnabled = clientEnabled;
    }

    @Override
    public boolean isConnectionEnabled() {
        return connectionEnabled;
    }

    /**
     * Whether to destroy StatefulRedisConnection beans prior to taking a checkpoint. Default value ({@value #DEFAULT_CONNECTION_ENABLED}).
     *
     * @param connectionEnabled Whether to destroy StatefulRedisConnection beans prior to taking a checkpoint.
     */
    public void setConnectionEnabled(boolean connectionEnabled) {
        this.connectionEnabled = connectionEnabled;
    }
}

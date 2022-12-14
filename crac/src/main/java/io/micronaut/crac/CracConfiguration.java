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

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.Toggleable;

import java.time.Duration;

/**
 * Configuration for CRaC support. Enabled by default.
 *
 * @author Tim Yates
 * @since 1.0.0
 */
public interface CracConfiguration extends Toggleable {

    /**
     * @return Whether to refresh beans prior to taking a checkpoint.
     */
    boolean isRefreshBeans();

    /**
     * @return The timeout to wait for a datasource to pause before taking a checkpoint.
     */
    @NonNull
    Duration getDatasourcePauseTimeout();
}

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
package io.micronaut.crac.info;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.PropertySource;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.core.util.StringUtils;
import io.micronaut.management.endpoint.info.InfoEndpoint;
import io.micronaut.management.endpoint.info.InfoSource;
import jakarta.inject.Singleton;
import org.crac.management.CRaCMXBean;
import org.reactivestreams.Publisher;

import java.util.Map;

@Singleton
@Requires(beans = InfoEndpoint.class, classes = CRaCMXBean.class)
@Requires(property = "endpoints.info.crac.enabled", notEquals = StringUtils.FALSE, defaultValue = StringUtils.TRUE)
public class CracInfoSource implements InfoSource {

    @Override
    public Publisher<PropertySource> getSource() {
        CRaCMXBean mxBean = CRaCMXBean.getCRaCMXBean();
        Map<String, Object> map = Map.of(
            "crac", Map.of(
                "restore-time", mxBean.getRestoreTime(),
                "uptime-since-restore", mxBean.getUptimeSinceRestore()
            )
        );
        return Publishers.just(PropertySource.of("crac-info", map));
    }
}

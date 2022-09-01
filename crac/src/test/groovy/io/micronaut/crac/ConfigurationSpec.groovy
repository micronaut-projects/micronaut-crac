package io.micronaut.crac

import io.micronaut.context.BeanContext
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class ConfigurationSpec extends Specification {

    @Inject
    BeanContext ctx

    void "crac and refreshing beans are enabled by default"() {
        expect:
        with(ctx.getBean(CracConfiguration)) {
            enabled
            refreshBeans
        }
    }
}

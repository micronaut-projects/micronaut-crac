package io.micronaut.crac

import io.micronaut.core.util.StringUtils
import spock.lang.Specification
import io.micronaut.context.ApplicationContext

class CracConfigurationSpec extends Specification {

    void "CRaC enabled by default with no custom compat lookup class"() {
        given:
        def ctx = ApplicationContext.run()

        when:
        def cfg = ctx.getBean(CracConfiguration)

        then:
        cfg.enabled

        when:
        ctx.getBean(CracResourceRegistrar)

        then:
        noExceptionThrown()

        cleanup:
        ctx.close()
    }

    void "CRaC can be disabled"() {
        given:
        ApplicationContext ctx = ApplicationContext.run('crac.enabled': StringUtils.FALSE)

        expect:
        !ctx.containsBean(CracConfiguration)
        !ctx.containsBean(CracContextProvider)
        !ctx.containsBean(OrderedResource)
        !ctx.containsBean(StartupCracRegistration)
        !ctx.containsBean(CracResourceRegistrar)

        cleanup:
        ctx.close()
    }
}

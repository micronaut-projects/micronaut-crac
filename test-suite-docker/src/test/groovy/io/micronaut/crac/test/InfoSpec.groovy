package io.micronaut.crac.test

import spock.lang.Requires

class InfoSpec extends BaseSpec {

    @Requires({ instance.application.running })
    def "info is exposed as expected"() {
        when:
        Map result = client.retrieve("/info", Map)

        then:
        with(result.crac) {
            it.'restore-time' > 0
            it.'uptime-since-restore' > 0
        }
    }
}

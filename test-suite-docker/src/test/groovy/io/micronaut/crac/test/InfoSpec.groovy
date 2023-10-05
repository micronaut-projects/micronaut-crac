package io.micronaut.crac.test

class InfoSpec extends BaseSpec {

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

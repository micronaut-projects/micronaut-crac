package io.micronaut.crac.test

import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

abstract class BaseSpec extends Specification {

    @Shared
    @AutoCleanup
    GenericContainer application = new GenericContainer(DockerImageName.parse("test-suite-docker:latest"))
            .withPrivilegedMode(true)
            .withExposedPorts(8080)
            .withLogConsumer { print "DOCKER: $it.utf8String" }
            .waitingFor(Wait.forHttp("/"))

    @Shared
    @AutoCleanup
    BlockingHttpClient client

    def setupSpec() {
        application.start()
        client = HttpClient.create(URI.create("http://${application.getHost()}:${application.getMappedPort(8080)}").toURL()).toBlocking()
    }
}

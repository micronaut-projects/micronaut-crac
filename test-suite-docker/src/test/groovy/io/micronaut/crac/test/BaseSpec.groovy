package io.micronaut.crac.test

import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.ContainerFetchException
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

abstract class BaseSpec extends Specification {

    private static final Logger LOG = LoggerFactory.getLogger(BaseSpec.class);

    @Shared
    @AutoCleanup
    GenericContainer application

    @Shared
    @AutoCleanup
    BlockingHttpClient client

    def setupSpec() {
        application = new GenericContainer(DockerImageName.parse("test-suite-docker:latest"))
                .withPrivilegedMode(true)
                .withExposedPorts(8080)
                .withLogConsumer { print "DOCKER: $it.utf8String" }
                .waitingFor(Wait.forHttp("/"))
        try {
            application.start()
            client = HttpClient.create(URI.create("http://${application.getHost()}:${application.getMappedPort(8080)}").toURL()).toBlocking()
        } catch (ContainerFetchException e) {
            LOG.error("""Failed to fetch the image for the test.
                        |
                        |This image should have been built by previous tasks in the build, so this is
                        |probably caused by the use of TestContainers Cloud.
                        |${'*' * 80}
                        |Please switch TestContainers desktop to run containers locally, and try again.
                        |${'*' * 80}""".stripMargin(),
                    e
            )
        }
    }
}

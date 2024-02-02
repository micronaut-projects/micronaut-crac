plugins {
    id("groovy")
    id("io.micronaut.build.internal.crac-test-suite")
    id("io.micronaut.minimal.application")
    id("io.micronaut.docker")
    id("io.micronaut.crac")
}

description = "Test features that require a CRaC JDK to be running the application."

dependencies {
    implementation(platform(libs.micronaut.platform))
    implementation(mn.micronaut.runtime)

    runtimeOnly(projects.micronautCrac)
    runtimeOnly(mn.micronaut.management)
    runtimeOnly(mnSerde.micronaut.serde.jackson)
    runtimeOnly(mnLogging.logback.classic)

    implementation(mnTestResources.testcontainers.core)
    testImplementation(mn.micronaut.http.client)
}

tasks.withType(Test::class) {
    dependsOn("dockerBuildCrac")
}

application {
    mainClass.set("io.micronaut.crac.example.Application")
}

micronaut {
    importMicronautPlatform.set(false)
    runtime("netty")
    testRuntime("spock")
    crac {
        this.javaVersion.set(JavaLanguageVersion.of(JavaVersion.current().majorVersion))
    }
}

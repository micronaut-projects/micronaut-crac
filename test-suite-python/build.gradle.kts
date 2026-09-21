plugins {
    `java-library`
    id("io.micronaut.build.internal.crac-test-suite")
    id("io.micronaut.build.internal.python")
}

dependencies {
    testImplementation(projects.micronautCrac)
    // The Python compiler (micronaut-inject-python) takes the (jar-resolved) compile classpath as its
    // annotation processor path, so the processors are testImplementation (not testAnnotationProcessor).
    testImplementation(mn.micronaut.inject.python.test)
    testImplementation(mn.micronaut.context.python)
    testImplementation(mnTest.micronaut.test.junit5)

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.withType<Test>().configureEach {
    systemProperty("micronaut.python.pool.enabled", "false")
}

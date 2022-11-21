plugins {
    id("io.micronaut.build.internal.crac-module")
}

dependencies {
    testImplementation(mn.micronaut.serde.jackson)
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "ch.qos.logback") {
            useVersion("1.4.5")
            because("Currently micronaut-bom is pulling in a version that requires 1.x slf4j, not 2.x")
        }
    }
}

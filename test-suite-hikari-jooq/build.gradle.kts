plugins {
    id("groovy")
    id("io.micronaut.build.internal.crac-library-test-suite")
}

dependencies {
    testImplementation(mnSql.micronaut.jooq)
    testImplementation(mnSql.micronaut.jdbc.hikari)

    testRuntimeOnly(mnSql.h2)
}

micronaut {
    version.set(libs.versions.micronaut.platform.get())
}

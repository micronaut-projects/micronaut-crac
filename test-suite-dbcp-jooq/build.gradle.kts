plugins {
    id("groovy")
    id("io.micronaut.build.internal.crac-library-test-suite")
}

dependencies {
    testImplementation(mnSql.micronaut.jooq)
    testImplementation(mnSql.micronaut.jdbc.dbcp)

    testRuntimeOnly(mnSql.h2)
}

micronaut {
    importMicronautPlatform.set(false)
}

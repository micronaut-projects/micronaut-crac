package io.micronaut.crac

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.NonNull
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.crac.CheckpointException
import org.crac.Context
import org.crac.Resource
import org.crac.RestoreException
import spock.lang.Specification

@Property(name = "spec.name", value = "CracRegistrationSpec")
@MicronautTest
class CracRegistrationSpec extends Specification {

    @Inject
    CracContextProviderReplacement cracContextProviderReplacement

    def "resources are registered in the expected order"() {
        expect:
        cracContextProviderReplacement.replacement.registrations == [
                'TestResource',
                'NettyEmbeddedServerCracHander',
                'TestResource3',
                'TestResource2',
        ]
    }

    @Singleton
    @Requires(property = "spec.name", value = "CracRegistrationSpec")
    @Replaces(CracContextProvider.class)
    static class CracContextProviderReplacement implements CracContextProvider {
        Context<Resource> replacement = new ContextReplacement();
        @Override
        @NonNull
        Context<Resource> provideContext() {
            return replacement;
        }
    }

    static class ContextReplacement extends Context<Resource> {
        static List<String> registrations = []
        @Override
        void beforeCheckpoint(Context<? extends Resource> context) throws CheckpointException {

        }

        @Override
        void afterRestore(Context<? extends Resource> context) throws RestoreException {

        }

        @Override
        void register(Resource resource) {
            registrations.add(resource.class.simpleName)
        }
    }

    @Singleton
    @Requires(property = "spec.name", value = "CracRegistrationSpec")
    static class TestResource implements OrderedResource {

        @Override
        int getOrder() {
            -1
        }

        @Override
        void beforeCheckpoint(Context<? extends Resource> context) throws Exception {

        }

        @Override
        void afterRestore(Context<? extends Resource> context) throws Exception {

        }
    }

    @Singleton
    @Requires(property = "spec.name", value = "CracRegistrationSpec")
    static class TestResource2 implements OrderedResource {

        @Override
        int getOrder() {
            3
        }

        @Override
        void beforeCheckpoint(Context<? extends Resource> context) throws Exception {

        }

        @Override
        void afterRestore(Context<? extends Resource> context) throws Exception {

        }
    }

    @Singleton
    @Requires(property = "spec.name", value = "CracRegistrationSpec")
    static class TestResource3 implements OrderedResource {

        @Override
        int getOrder() {
            2
        }

        @Override
        void beforeCheckpoint(Context<? extends Resource> context) throws Exception {

        }

        @Override
        void afterRestore(Context<? extends Resource> context) throws Exception {

        }
    }
}

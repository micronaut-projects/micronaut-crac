# Python Docs Disabled Test Inventory

This file tracks Python docs examples of Micronaut CRaC that are present but disabled, or that deviate from the
Java example because the direct port currently fails compilation or at runtime (Python compiler gaps). Use it as the
bug-fixing task list for the final migration wave.

## Reconciliation

- Last generated active `@Disabled` count: 0.
- Last generated command: `rg -n "@Disabled\(" test-suite-python/src/test/python`.
- Last full-suite command: `./gradlew :test-suite-python:test -Ppython-ci`.
- Last full-suite result: build successful, 2 tests executed (2 test classes), 0 skipped.

## Migration Rules

- Methods that implement a Java interface keep the Java (camelCase) name (`beforeCheckpoint`, `afterRestore`); other
  methods and constructor parameters are snake_case (`is_running`, `resource_bean`).
- Python source files must not live in a package whose `__init__.py` the Python compiler also generates for an imported
  Java package: `micronaut/crac/*.py` collides with the shims of `io.micronaut.crac` (`OrderedResource`) and
  `io.micronaut.crac.test` (`CheckpointSimulator`) (`Failed to write Python code to
  [.../micronaut/crac/__init__.py]: Output stream or writer has already been opened`). The snippet classes were
  therefore moved to `io.micronaut.crac.docs` in every language.
- Imported shim classes of Java types cannot be used as runtime type arguments (`TypeError: invalid instantiation of
  foreign object`): `BeanContext.getBean(CheckpointSimulator)` and `ApplicationContext.createBean(HttpClient, url)`
  need a `java.type("io.micronaut.crac.test.CheckpointSimulator")` / `java.type("io.micronaut.http.client.HttpClient")`
  alias (marked `# TODO(python)`). The Python snippet class `ResourceBean` works as a runtime type argument of
  `getBean`; the returned bean is unwrapped with `.asPolyglotValue()` before its Python methods are called.
- A Python class cannot extend `io.micronaut.context.DefaultApplicationContextBuilder`, so
  `@MicronautTest(contextBuilder = EagerSingletons.class)` has no Python equivalent. The Python suite enables eager
  singleton initialization with the Java `@ContextConfigurer` `io.micronaut.crac.docs.EagerSingletonsConfigurer`
  (`test-suite-python/src/test/java`), exactly what Micronaut Launch generates for an application; the lazily created
  `HttpClient` of the Java test is a `functools.cached_property`.

## Active `@Disabled` Tests

None.

## Commented Unsupported Snippet Ports

None.

## Intentionally Unsupported Snippet Targets

None.

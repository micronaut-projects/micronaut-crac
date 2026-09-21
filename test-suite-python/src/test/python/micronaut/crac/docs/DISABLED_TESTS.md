# Python Docs Disabled Test Inventory

This file tracks Python docs examples of Micronaut CRaC that are present but disabled, or that deviate from the
Java example because the direct port currently fails compilation or at runtime (Python compiler gaps). Use it as the
bug-fixing task list for the final migration wave.

## Reconciliation

- Last generated active `@Disabled` count: 0.
- Last generated command: `rg -n "@Disabled\(" test-suite-python/src/test/python`.
- Last full-suite command: `./gradlew :test-suite-python:test -Ppython-ci` (micronaut-core 5.2.3).
- Last full-suite result: build successful, 2 tests executed (2 test classes), 0 skipped.

## Migration Rules

- Methods that implement a Java interface keep the Java (camelCase) name (`beforeCheckpoint`, `afterRestore`); other
  methods and constructor parameters are snake_case (`is_running`, `resource_bean`).
- Python source files must not live in a package whose `__init__.py` the Python compiler also generates for an imported
  Java package: `micronaut/crac/*.py` collides with the shims of `io.micronaut.crac` (`OrderedResource`) and
  `io.micronaut.crac.test` (`CheckpointSimulator`) (`Failed to write Python code to
  [.../micronaut/crac/__init__.py]: Output stream or writer has already been opened`). The snippet classes were
  therefore moved to `io.micronaut.crac.docs` in every language.
- Imported Java classes are used as runtime type arguments (`BeanContext.getBean(CheckpointSimulator)`,
  `ApplicationContext.createBean(HttpClient, url)`), no `java.type(...)` aliases; a Python bean returned by
  `getBean(ResourceBean)` is used directly (its Python methods are callable on the returned object).
- `@MicronautTest(contextBuilder = EagerSingletons.class)` has no Python equivalent: Micronaut Test instantiates the
  builder class reflectively before any application context (and with it the GraalPy runtime) exists, so a Python
  subclass of `io.micronaut.context.DefaultApplicationContextBuilder` cannot be created at that point. The Python suite
  enables eager singleton initialization with the Java `@ContextConfigurer` `io.micronaut.crac.docs.EagerSingletonsConfigurer`
  (`test-suite-python/src/test/java`), exactly what Micronaut Launch generates for an application; the lazily created
  `HttpClient` of the Java test is a `functools.cached_property`.

## Active `@Disabled` Tests

None.

## Commented Unsupported Snippet Ports

None.

## Intentionally Unsupported Snippet Targets

None.

# Verification

Passed locally:

- Maven POM parses as XML.
- Executed the actual schema and SQL extracted from SqliteJobRepository against
  Python's SQLite: insert, repeated-ID update, preserved first-discovery time,
  and rollback of a batch with a NOT NULL violation all passed.
- Five Java test classes are included (eight test methods).

Not verified:

- `bash mvnw test` failed before compilation because Maven Central's hostname
  could not be resolved in this environment. No Java test was executed.
- Only Java 17 is installed here; the delivered project requires Java 21.
- HTTP runtime behaviour and SQLite JDBC integration still require the Mac test run.

Run `mvn test` on Java 21, then the curl/restart checks in CP001-README.md.
The SQL check is not a substitute for those Java integration tests.

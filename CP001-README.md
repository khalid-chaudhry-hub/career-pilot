# Career Pilot — CP001

Java 21 / Maven / Spring Boot 4.1.1, using the supplied Initializr project.
Mock discovery → immutable Job record → SQLite → REST JSON.
All companies and opportunities are fictional. No scraping, scoring, AI calls,
application submission or external credentials are involved.

## Install into your existing clone

Extract this ZIP somewhere separate from the repository. Copy its CONTENTS
into `/Users/khalid/IdeaProjects/career-pilot`, not the enclosing folder.
Include `.mvn`, `.gitattributes` and `.gitignore` (Finder: Command+Shift+Period).
Merge `.gitignore` if you have customised it. Do not touch the clone's `.git`.
This archive intentionally contains no README.md, so your existing README is preserved.
Open the repository's pom.xml as a Maven project in IntelliJ and select JDK 21.

## Test and run

```bash
cd /Users/khalid/IdeaProjects/career-pilot
mvn test
mvn spring-boot:run
```

Alternatively use `bash mvnw test` and `bash mvnw spring-boot:run`.
The application listens only on localhost:8080. In another terminal:

```bash
curl -X POST http://localhost:8080/api/jobs/discover
curl http://localhost:8080/api/jobs
```

POST discovers and saves three jobs, returning the complete stored list sorted
by title then ID. Repeating POST updates existing jobs rather than adding duplicates.
The first discovery timestamp is preserved. GET never performs discovery.
Before the first POST, GET returns `[]`.

The database is `career-pilot.db` in the working directory. Stop with Ctrl+C,
restart from the same directory, then GET: the jobs should still be present.
Tests use a separate, single-connection in-memory SQLite database and never your file.
No authentication is implemented; keep the server bound to localhost.

## Code reading order

1. job/Job.java — initial domain fields; optional salary, posting date and structured skills deferred.
2. discovery/JobDiscoveryService.java and MockJobDiscoveryService.java — replaceable source.
3. persistence/JobRepository.java and SqliteJobRepository.java — storage boundary and atomic upsert.
4. job/JobService.java — orchestration via constructor injection.
5. job/JobController.java — two endpoints.
6. Tests — discovery, orchestration, SQLite round trip/update/rollback, HTTP and startup.

Spring JDBC replaces the Initializr JPA starter for this small SQL-based slice.
It avoids an ORM entity copy and SQLite Hibernate dialect. PostgreSQL migration
will require driver/config/schema and repository integration testing, not a claim
of automatic portability. schema.sql is initial bootstrap, not a migration system.
The generated Spring Boot test stack supplies JUnit Jupiter; its version follows
the Boot BOM rather than forcing the earlier planned JUnit 5 version.

## Verification in the generation environment

Only Java 17 was available (the project requires 21). See docs/verification.md
for actual checks and limitations. Run `mvn test` on your Mac before committing.

## Commit after tests pass

```bash
git status
git add pom.xml src .mvn mvnw mvnw.cmd .gitattributes .gitignore CP001-README.md docs
git diff --cached --stat
git commit -m "CP001: mock job discovery with SQLite persistence and REST API"
```

No commit or push has been performed on your behalf. Review staged files; do not
commit IDE files, database files, tokens or credentials.

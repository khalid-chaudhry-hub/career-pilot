# ADR-001: Java with Spring Boot and SQLite

Status: implemented for CP001.

Java 21 reinforces the owner's Java/backend experience and provides records and
mature HTTP/API integration. Python is not required for future LLM API calls.
Spring Boot provides dependency injection, HTTP and transaction management;
Maven supplies reproducible dependency resolution. Retain the supplied Boot 4.1.1.

SQLite provides local persistence without a server. Use Spring JDBC, not the
initially selected JPA starter: a single table does not justify Hibernate's extra
SQLite dialect and entity mapping. SQL is confined to the persistence adapter.
Retain Boot-managed JUnit Jupiter rather than downgrading its testing platform.

Consequences: SQLite is a local, low-concurrency choice. PostgreSQL and schema
migrations should be introduced when required, with integration tests. No Docker,
cloud infrastructure, AI framework or multi-agent implementation is needed yet.

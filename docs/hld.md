# CP001 design

JobController delegates to JobService, which depends on JobDiscoveryService and
JobRepository. MockJobDiscoveryService produces domain records; SqliteJobRepository
stores them using JdbcTemplate and parameterised SQL. Spring wires constructors.

Discovery and saving run within a transaction. Stable source-prefixed IDs make
repeat requests idempotent with respect to row count; current details update while
first discovery time remains unchanged. The complete sorted stored list is returned.
SQLite uses one pooled connection, deliberately limiting concurrency for local use.
Database/bootstrap errors propagate as server errors and roll back the batch.

Real sources will need canonical IDs, deduplication across sources, timeouts,
retry policy, validation of untrusted content and pagination. None are pretended
to exist in this mock-only slice. Dates use UTC ISO-8601 text. No salary information
is invented. Persistence format is isolated behind the repository interface.

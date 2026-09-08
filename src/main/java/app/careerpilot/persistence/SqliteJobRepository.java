package app.careerpilot.persistence;

import app.careerpilot.job.Job;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Repository
public class SqliteJobRepository implements JobRepository {
    private final JdbcTemplate jdbc;

    public SqliteJobRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public void saveAll(List<Job> jobs) {
        for (Job job : jobs) {
            jdbc.update("""
                INSERT INTO jobs (id, title, company, location, work_mode, contract_type,
                                  description, source, source_url, discovered_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    title=excluded.title, company=excluded.company, location=excluded.location,
                    work_mode=excluded.work_mode, contract_type=excluded.contract_type,
                    description=excluded.description, source=excluded.source,
                    source_url=excluded.source_url
                """, job.id(), job.title(), job.company(), job.location(), job.workMode(),
                job.contractType(), job.description(), job.source(), job.sourceUrl(),
                job.discoveredAt().toString());
        }
    }

    @Override
    public List<Job> findAll() {
        return jdbc.query("SELECT * FROM jobs ORDER BY title, id", (rs, rowNum) -> new Job(
            rs.getString("id"), rs.getString("title"), rs.getString("company"),
            rs.getString("location"), rs.getString("work_mode"), rs.getString("contract_type"),
            rs.getString("description"), rs.getString("source"), rs.getString("source_url"),
            Instant.parse(rs.getString("discovered_at"))));
    }
}

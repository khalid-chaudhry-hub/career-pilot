package app.careerpilot;

import app.careerpilot.discovery.MockJobDiscoveryService;
import app.careerpilot.job.Job;
import app.careerpilot.job.JobService;
import app.careerpilot.persistence.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import java.time.Instant;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class JobPersistenceTest {
    @Autowired JobRepository repository;
    @Autowired JobService service;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach
    void clearJobs() {
        jdbc.update("DELETE FROM jobs");
    }

    @Test
    void emptyDatabaseReturnsNoJobs() {
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void repeatedDiscoveryDoesNotDuplicateJobsOrChangeFirstDiscoveryTime() {
        var first = service.discover();
        assertThat(first).hasSize(3);
        assertThat(service.discover()).isEqualTo(first);
    }

    @Test
    void updatesExistingJobAndPreservesOriginalDiscoveryTime() {
        Job original = new MockJobDiscoveryService().discover().getFirst();
        repository.saveAll(List.of(original));
        repository.saveAll(List.of(new Job(original.id(), "Updated title", original.company(),
            original.location(), original.workMode(), original.contractType(), original.description(),
            original.source(), original.sourceUrl(), Instant.parse("2030-01-01T00:00:00Z"))));
        assertThat(repository.findAll()).singleElement().satisfies(job -> {
            assertThat(job.title()).isEqualTo("Updated title");
            assertThat(job.discoveredAt()).isEqualTo(original.discoveredAt());
        });
    }

    @Test
    void rollsBackWholeBatchWhenOneJobIsInvalid() {
        Job valid = new MockJobDiscoveryService().discover().getFirst();
        Job invalid = new Job("invalid", null, "Company", "UK", "REMOTE", "PERMANENT",
            "Description", "MOCK", "https://example.com/invalid", Instant.now());
        assertThatThrownBy(() -> repository.saveAll(List.of(valid, invalid)))
            .isInstanceOf(org.springframework.dao.DataAccessException.class);
        assertThat(repository.findAll()).isEmpty();
    }
}

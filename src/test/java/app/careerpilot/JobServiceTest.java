package app.careerpilot;

import app.careerpilot.discovery.JobDiscoveryService;
import app.careerpilot.discovery.MockJobDiscoveryService;
import app.careerpilot.job.JobService;
import app.careerpilot.persistence.JobRepository;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JobServiceTest {
    @Test
    void savesDiscoveredJobsAndReturnsPersistedResults() {
        var discovery = mock(JobDiscoveryService.class);
        var repository = mock(JobRepository.class);
        var jobs = new MockJobDiscoveryService().discover();
        when(discovery.discover()).thenReturn(jobs);
        when(repository.findAll()).thenReturn(jobs);
        assertThat(new JobService(discovery, repository).discover()).isEqualTo(jobs);
        var order = inOrder(repository);
        order.verify(repository).saveAll(jobs);
        order.verify(repository).findAll();
    }
}

package app.careerpilot;

import app.careerpilot.discovery.MockJobDiscoveryService;
import app.careerpilot.job.Job;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MockJobDiscoveryServiceTest {
    @Test
    void discoversThreeDistinctMockJobsWithStableIds() {
        var discovery = new MockJobDiscoveryService();
        var jobs = discovery.discover();
        assertThat(jobs).hasSize(3);
        assertThat(jobs).extracting(Job::source).containsOnly("MOCK");
        assertThat(jobs).extracting(Job::id).doesNotHaveDuplicates()
            .containsExactlyElementsOf(discovery.discover().stream().map(Job::id).toList());
    }
}

package app.careerpilot.scoring;

import app.careerpilot.job.Job;
import app.careerpilot.persistence.JobRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static app.careerpilot.scoring.ScoreRule.Field.*;
import static app.careerpilot.scoring.ScoreRule.Kind.*;

class RecommendationServiceTest {
    @Test
    void sortsByExclusionThenScoreThenStableIdWithoutWriting() {
        var repository = mock(JobRepository.class);
        when(repository.findAll()).thenReturn(List.of(job("b", "Java"), job("x", "Java inside IR35"),
            job("a", "Java"), job("c", "Go")));
        var profile = new CandidateProfile("Example", "Any", "Any", List.of(), List.of(
            new ScoreRule("Java", DESCRIPTION, PREFERENCE, 10, List.of("Java")),
            new ScoreRule("IR35", DESCRIPTION, EXCLUSION, 0, List.of("inside IR35"))));
        var service = new RecommendationService(repository,
            new ScoringConfiguration.Profiles(Map.of("example", profile)), new JobScorer());
        assertThat(service.recommend("example")).extracting(result -> result.job().id())
            .containsExactly("a", "b", "c", "x");
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
        assertThatThrownBy(() -> service.recommend("unknown"))
            .isInstanceOf(ResponseStatusException.class);
    }

    private Job job(String id, String description) {
        return new Job(id, "Role", "Company", "UK", "REMOTE", "PERMANENT",
            description, "MOCK", "https://example.com", Instant.EPOCH);
    }
}

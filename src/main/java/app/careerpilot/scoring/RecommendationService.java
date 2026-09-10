package app.careerpilot.scoring;

import app.careerpilot.persistence.JobRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Comparator;
import java.util.List;

@Service
public class RecommendationService {
    private final JobRepository repository;
    private final ScoringConfiguration.Profiles configuration;
    private final JobScorer scorer;

    public RecommendationService(JobRepository repository, ScoringConfiguration.Profiles configuration,
                                 JobScorer scorer) {
        this.repository = repository;
        this.configuration = configuration;
        this.scorer = scorer;
    }

    public List<ScoredJob> recommend(String profileId) {
        CandidateProfile profile = configuration.profiles().get(profileId);
        if (profile == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown candidate profile");
        return repository.findAll().stream()
            .map(job -> scorer.score(job, profileId, profile))
            .sorted(Comparator.comparing((ScoredJob job) ->
                    job.eligibility() == ScoredJob.Eligibility.EXCLUDED_BY_PROFILE)
                .thenComparing(Comparator.comparingInt(ScoredJob::score).reversed())
                .thenComparing(job -> job.job().id()))
            .toList();
    }
}

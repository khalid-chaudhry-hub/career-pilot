package app.careerpilot.scoring;

import app.careerpilot.discovery.MockJobDiscoveryService;
import app.careerpilot.job.Job;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProfileScoringTest {
    @Autowired ScoringConfiguration.Profiles configuration;
    @Autowired JobScorer scorer;

    @Test
    void loadsFourProfilesAndScoresSameRoleDifferently() {
        assertThat(configuration.profiles()).containsKeys("senior", "graduate", "nurse", "secretary");
        Job javaJob = new MockJobDiscoveryService().discover().getFirst();
        assertThat(scorer.score(javaJob, "senior", configuration.profiles().get("senior")).score())
            .isEqualTo(75);
        assertThat(scorer.score(javaJob, "graduate", configuration.profiles().get("graduate")).score())
            .isZero();
    }

    @Test
    void graduatePrefersTrainingRoleToSeniorRole() {
        var graduate = configuration.profiles().get("graduate");
        Job entry = job("Graduate Software Engineer", "Java. Training provided.");
        assertThat(scorer.score(entry, "graduate", graduate).score()).isEqualTo(90);
        assertThat(scorer.score(new MockJobDiscoveryService().discover().getFirst(), "graduate",
            graduate).score()).isZero();
    }

    @Test
    void sameEngineSupportsNonSoftwareOccupationsWithoutClaimingRegistration() {
        var nurse = scorer.score(job("Staff Nurse", "Patient care; NMC registration required."),
            "nurse", configuration.profiles().get("nurse"));
        assertThat(nurse.score()).isEqualTo(100);
        assertThat(nurse.eligibility()).isEqualTo(ScoredJob.Eligibility.NOT_ASSESSED);
        var secretary = scorer.score(job("Secretary", "Diary management and Microsoft Office."),
            "secretary", configuration.profiles().get("secretary"));
        assertThat(secretary.score()).isEqualTo(100);
    }

    private Job job(String title, String description) {
        return new Job("example", title, "Example", "UK", "ONSITE", "PERMANENT",
            description, "MOCK", "https://example.com", Instant.EPOCH);
    }
}

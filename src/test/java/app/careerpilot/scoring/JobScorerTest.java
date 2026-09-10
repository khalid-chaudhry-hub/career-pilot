package app.careerpilot.scoring;

import app.careerpilot.job.Job;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import static org.assertj.core.api.Assertions.*;
import static app.careerpilot.scoring.ScoreRule.Field.*;
import static app.careerpilot.scoring.ScoreRule.Kind.*;

class JobScorerTest {
    private final JobScorer scorer = new JobScorer();

    private CandidateProfile profile(ScoreRule... rules) {
        return new CandidateProfile("Example", "Any occupation", "Any level", List.of(), List.of(rules));
    }

    private ScoreRule rule(String label, ScoreRule.Kind kind, int weight, String... terms) {
        return new ScoreRule(label, DESCRIPTION, kind, weight, List.of(terms));
    }

    private Job job(String description) {
        return new Job("test", "Role", "Company", "UK", "REMOTE", "PERMANENT",
            description, "MOCK", "https://example.com", Instant.EPOCH);
    }

    @Test
    void matchesWholeTermsWithoutConfusingJavaAndJavascript() {
        var profile = profile(rule("Java", PREFERENCE, 30, "Java"));
        assertThat(scorer.score(job("JavaScript"), "example", profile).score()).isZero();
        assertThat(scorer.score(job("JAVA, Java; Java"), "example", profile).earnedPoints()).isEqualTo(30);
    }

    @Test
    void countsAliasesOnlyOnceAndExplainsMissingEvidence() {
        var result = scorer.score(job("AWS and Azure"), "example", profile(
            rule("Cloud", PREFERENCE, 10, "AWS", "Azure"),
            rule("Spring", PREFERENCE, 10, "Spring Boot")));
        assertThat(result.score()).isEqualTo(50);
        assertThat(result.reasons().get(0).matchedTerms()).containsExactly("AWS", "Azure");
        assertThat(result.reasons().get(1).outcome()).isEqualTo("NO_TEXT_EVIDENCE");
    }

    @Test
    void penalisesExplicitRequirementButNotLearnableGo() {
        var profile = profile(rule("Java", PREFERENCE, 30, "Java"),
            rule("Go experience", PENALTY, 40, "five years commercial Go experience required"));
        assertThat(scorer.score(job("Java required; Go can be learned"), "example", profile).score())
            .isEqualTo(100);
        var result = scorer.score(job("Java. Five years commercial Go experience required."),
            "example", profile);
        assertThat(result.score()).isZero();
        assertThat(result.earnedPoints()).isEqualTo(-10);
    }

    @Test
    void exclusionIsSeparateFromScoreAndDoesNotClaimEligibility() {
        var profile = profile(rule("Java", PREFERENCE, 20, "Java"),
            rule("IR35", EXCLUSION, 0, "inside IR35"));
        var excluded = scorer.score(job("Java; inside IR35"), "example", profile);
        assertThat(excluded.score()).isEqualTo(100);
        assertThat(excluded.eligibility()).isEqualTo(ScoredJob.Eligibility.EXCLUDED_BY_PROFILE);
        assertThat(scorer.score(job("Java"), "example", profile).eligibility())
            .isEqualTo(ScoredJob.Eligibility.NOT_ASSESSED);
    }

    @Test
    void handlesMissingTextAndUsesLocaleIndependentMatching() {
        var profile = profile(rule("Skills", PREFERENCE, 10, "SPRING BOOT"));
        assertThat(scorer.score(job(null), "example", profile).score()).isZero();
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertThat(scorer.score(job("spring   boot"), "example", profile).score()).isEqualTo(100);
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    void rejectsInvalidProfilesBeforeScoring() {
        assertThatThrownBy(() -> profile(rule("Penalty only", PENALTY, 10, "senior")))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> rule("Empty term", PREFERENCE, 10, " "))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> rule("Negative weight", PREFERENCE, -10, "Java"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}

package app.careerpilot.scoring;

import app.careerpilot.job.Job;
import java.util.List;

public record ScoredJob(Job job, String profileId, int score, int earnedPoints, int possiblePoints,
                        Eligibility eligibility, List<Reason> reasons) {
    // Text matching cannot establish professional eligibility or verify qualifications.
    public enum Eligibility { NOT_ASSESSED, EXCLUDED_BY_PROFILE }
    public record Reason(String criterion, String outcome, int points, List<String> matchedTerms) {
        public Reason { matchedTerms = List.copyOf(matchedTerms); }
    }
    public ScoredJob { reasons = List.copyOf(reasons); }
}

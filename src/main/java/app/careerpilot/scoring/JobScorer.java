package app.careerpilot.scoring;

import app.careerpilot.job.Job;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class JobScorer {
    public ScoredJob score(Job job, String profileId, CandidateProfile profile) {
        int earned = 0;
        int possible = 0;
        boolean excluded = false;
        List<ScoredJob.Reason> reasons = new ArrayList<>();
        for (ScoreRule rule : profile.rules()) {
            String text = normalize(fieldValue(job, rule.field()));
            List<String> matched = rule.terms().stream()
                .filter(term -> containsPhrase(text, normalize(term))).toList();
            boolean hit = !matched.isEmpty();
            int points = 0;
            String outcome;
            switch (rule.kind()) {
                case PREFERENCE -> {
                    possible += rule.weight();
                    points = hit ? rule.weight() : 0;
                    outcome = hit ? "MATCHED" : "NO_TEXT_EVIDENCE";
                }
                case PENALTY -> {
                    points = hit ? -rule.weight() : 0;
                    outcome = hit ? "PENALTY" : "NOT_TRIGGERED";
                }
                case EXCLUSION -> {
                    excluded |= hit;
                    outcome = hit ? "EXCLUDED" : "NOT_TRIGGERED";
                }
                default -> throw new IllegalStateException("Unexpected rule kind");
            }
            earned += points;
            reasons.add(new ScoredJob.Reason(rule.label(), outcome, points, matched));
        }
        int bounded = Math.max(0, Math.min(possible, earned));
        int score = (int) Math.round(100.0 * bounded / possible);
        return new ScoredJob(job, profileId, score, earned, possible,
            excluded ? ScoredJob.Eligibility.EXCLUDED_BY_PROFILE : ScoredJob.Eligibility.NOT_ASSESSED,
            reasons);
    }

    private String fieldValue(Job job, ScoreRule.Field field) {
        return switch (field) {
            case TITLE -> job.title();
            case DESCRIPTION -> job.description();
            case LOCATION -> job.location();
            case WORK_MODE -> job.workMode();
            case CONTRACT_TYPE -> job.contractType();
        };
    }

    private static String normalize(String text) {
        return text == null ? "" : Normalizer.normalize(text, Normalizer.Form.NFKC)
            .toLowerCase(Locale.ROOT).strip().replaceAll("\\s+", " ");
    }

    private static boolean containsPhrase(String text, String term) {
        return Pattern.compile("(?<![\\p{L}\\p{N}_])" + Pattern.quote(term)
            + "(?![\\p{L}\\p{N}_])").matcher(text).find();
    }
}

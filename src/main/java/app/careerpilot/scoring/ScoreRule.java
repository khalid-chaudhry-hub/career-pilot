package app.careerpilot.scoring;

import java.util.List;
import java.util.Objects;

public record ScoreRule(String label, Field field, Kind kind, int weight, List<String> terms) {
    public enum Field { TITLE, DESCRIPTION, LOCATION, WORK_MODE, CONTRACT_TYPE }
    public enum Kind { PREFERENCE, PENALTY, EXCLUSION }

    public ScoreRule {
        if (label == null || label.isBlank()) throw new IllegalArgumentException("Rule label is required");
        Objects.requireNonNull(field, "Rule field is required");
        Objects.requireNonNull(kind, "Rule kind is required");
        if (weight < 0 || weight > 100 || (kind != Kind.EXCLUSION && weight == 0))
            throw new IllegalArgumentException("Preference/penalty weight must be 1..100; exclusion 0..100");
        if (terms == null || terms.isEmpty() || terms.stream().anyMatch(t -> t == null || t.isBlank()))
            throw new IllegalArgumentException("Rule must have nonblank terms");
        terms = List.copyOf(terms);
    }
}

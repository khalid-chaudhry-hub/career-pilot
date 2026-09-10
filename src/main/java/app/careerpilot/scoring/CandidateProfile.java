package app.careerpilot.scoring;

import java.util.List;

public record CandidateProfile(String displayName, String occupation, String experienceLevel,
                               List<String> skills, List<ScoreRule> rules) {
    public CandidateProfile {
        if (displayName == null || displayName.isBlank() || occupation == null || occupation.isBlank()
                || experienceLevel == null || experienceLevel.isBlank())
            throw new IllegalArgumentException("Profile name, occupation and experience level are required");
        if (skills == null || skills.stream().anyMatch(s -> s == null || s.isBlank()))
            throw new IllegalArgumentException("Skills must be a list of nonblank values");
        skills = List.copyOf(skills);
        if (rules == null || rules.isEmpty())
            throw new IllegalArgumentException("Profile needs scoring rules");
        rules = List.copyOf(rules);
        if (rules.stream().noneMatch(r -> r.kind() == ScoreRule.Kind.PREFERENCE))
            throw new IllegalArgumentException("Profile needs at least one positive preference");
        if (rules.stream().map(ScoreRule::label).distinct().count() != rules.size())
            throw new IllegalArgumentException("Rule labels must be unique within a profile");
    }
}

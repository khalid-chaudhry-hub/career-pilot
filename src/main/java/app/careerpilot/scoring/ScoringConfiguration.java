package app.careerpilot.scoring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import java.util.Map;

@Configuration
@PropertySource("classpath:candidate-profiles.properties")
@EnableConfigurationProperties(ScoringConfiguration.Profiles.class)
public class ScoringConfiguration {
    @Bean
    JobScorer jobScorer() {
        return new JobScorer();
    }

    @ConfigurationProperties(prefix = "career-pilot")
    public record Profiles(Map<String, CandidateProfile> profiles) {
        public Profiles {
            if (profiles == null || profiles.isEmpty())
                throw new IllegalArgumentException("At least one candidate profile is required");
            profiles = Map.copyOf(profiles);
        }
    }
}

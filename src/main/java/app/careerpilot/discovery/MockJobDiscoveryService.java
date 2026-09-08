package app.careerpilot.discovery;

import app.careerpilot.job.Job;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.List;

@Component
public class MockJobDiscoveryService implements JobDiscoveryService {
    @Override
    public List<Job> discover() {
        Instant now = Instant.now();
        return List.of(
            new Job("mock-java-001", "Senior Java Engineer", "Example Banking",
                "Edinburgh", "HYBRID", "PERMANENT",
                "Java, Spring Boot, microservices and AWS. Go can be learned.",
                "MOCK", "https://example.com/jobs/java-001", now),
            new Job("mock-go-001", "Go Backend Engineer", "Example Cloud",
                "UK", "REMOTE", "PERMANENT",
                "Five years commercial Go experience required. Kubernetes and distributed systems.",
                "MOCK", "https://example.com/jobs/go-001", now),
            new Job("mock-lead-001", "Java Technical Lead", "Example Engineering",
                "Glasgow", "HYBRID", "CONTRACT",
                "Java, Spring Boot, Azure, Terraform, architecture and technical leadership.",
                "MOCK", "https://example.com/jobs/lead-001", now)
        );
    }
}

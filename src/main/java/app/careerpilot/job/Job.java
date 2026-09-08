package app.careerpilot.job;

import java.time.Instant;

public record Job(String id, String title, String company, String location,
                  String workMode, String contractType, String description,
                  String source, String sourceUrl, Instant discoveredAt) {
}

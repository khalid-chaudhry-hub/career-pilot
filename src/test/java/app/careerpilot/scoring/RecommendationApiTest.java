package app.careerpilot.scoring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecommendationApiTest {
    @LocalServerPort int port;

    @Test
    void endpointScoresExistingJobsAndRejectsUnknownOrMissingProfile() throws Exception {
        try (var client = HttpClient.newHttpClient()) {
            var discovery = client.send(HttpRequest.newBuilder(URI.create(base() + "/discover"))
                .POST(HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.ofString());
            assertThat(discovery.statusCode()).isEqualTo(200);
            var senior = get(client, "?profile=senior");
            assertThat(senior.statusCode()).isEqualTo(200);
            assertThat(senior.body()).contains("\"score\":90", "\"profileId\":\"senior\"",
                "NOT_ASSESSED", "matchedTerms");
            assertThat(senior.body().indexOf("mock-lead-001"))
                .isLessThan(senior.body().indexOf("mock-java-001"));
            assertThat(get(client, "?profile=graduate").statusCode()).isEqualTo(200);
            assertThat(get(client, "?profile=unknown").statusCode()).isEqualTo(404);
            assertThat(get(client, "").statusCode()).isEqualTo(400);
        }
    }

    private String base() { return "http://localhost:" + port + "/api/jobs"; }

    private HttpResponse<String> get(HttpClient client, String query) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create(base() + "/recommendations" + query))
            .GET().build(), HttpResponse.BodyHandlers.ofString());
    }
}

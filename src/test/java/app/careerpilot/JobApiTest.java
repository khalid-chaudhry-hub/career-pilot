package app.careerpilot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JobApiTest {
    @LocalServerPort int port;

    @Test
    void discoversAndListsJobsOverHttp() throws Exception {
        try (var client = HttpClient.newHttpClient()) {
            String base = "http://localhost:" + port + "/api/jobs";
            var discovered = client.send(HttpRequest.newBuilder(URI.create(base + "/discover"))
                .POST(HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.ofString());
            assertThat(discovered.statusCode()).isEqualTo(200);
            var listed = client.send(HttpRequest.newBuilder(URI.create(base)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
            assertThat(listed.statusCode()).isEqualTo(200);
            assertThat(listed.body()).isEqualTo(discovered.body())
                .contains("mock-java-001", "mock-go-001", "mock-lead-001");
        }
    }
}

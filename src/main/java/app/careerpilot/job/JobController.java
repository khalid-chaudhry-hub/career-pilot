package app.careerpilot.job;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService service;

    public JobController(JobService service) {
        this.service = service;
    }

    @PostMapping("/discover")
    public List<Job> discover() {
        return service.discover();
    }

    @GetMapping
    public List<Job> findAll() {
        return service.findAll();
    }
}

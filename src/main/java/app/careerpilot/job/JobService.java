package app.careerpilot.job;

import app.careerpilot.discovery.JobDiscoveryService;
import app.careerpilot.persistence.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class JobService {
    private final JobDiscoveryService discovery;
    private final JobRepository repository;

    public JobService(JobDiscoveryService discovery, JobRepository repository) {
        this.discovery = discovery;
        this.repository = repository;
    }

    @Transactional
    public List<Job> discover() {
        repository.saveAll(discovery.discover());
        return repository.findAll();
    }

    public List<Job> findAll() {
        return repository.findAll();
    }
}

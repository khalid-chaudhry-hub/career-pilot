package app.careerpilot.persistence;

import app.careerpilot.job.Job;
import java.util.List;

public interface JobRepository {
    void saveAll(List<Job> jobs);
    List<Job> findAll();
}

package app.careerpilot.discovery;

import app.careerpilot.job.Job;
import java.util.List;

public interface JobDiscoveryService {
    List<Job> discover();
}

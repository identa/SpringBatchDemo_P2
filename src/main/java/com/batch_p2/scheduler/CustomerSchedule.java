package com.batch_p2.scheduler;

import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CustomerSchedule {
    @Autowired
    public JobOperator jobOperator;

    @Scheduled(cron = "*/5 * * * * *")
    public void runJob() throws Exception{
        this.jobOperator.startNextInstance("job");
    }
}

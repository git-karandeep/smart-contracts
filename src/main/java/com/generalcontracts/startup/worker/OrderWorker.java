/*
package com.generalcontracts.startup.worker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import io.camunda.spring.client.annotation.JobWorker;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderWorker {

    @JobWorker(type = "process-payment")
    public void process(JobClient client, ActivatedJob job) {

        client.newCompleteCommand(job.getKey())
                .variables(Map.of(
                        "paymentStatus", "SUCCESS"
                ))
                .send()
                .join();
    }
}
*/

package com.sequenceiq.environment.credential.v1.converter;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.model.Logging;
import com.sequenceiq.cloudbreak.cloud.model.Telemetry;
import com.sequenceiq.cloudbreak.cloud.model.WorkloadAnalytics;
import com.sequenceiq.environment.api.v1.environment.model.request.LoggingRequest;
import com.sequenceiq.environment.api.v1.environment.model.request.TelemetryRequest;
import com.sequenceiq.environment.api.v1.environment.model.request.WorkloadAnalyticsRequest;

@Component
public class TelemetryRequestToTelemetryConverter {

    public Telemetry convert(TelemetryRequest request) {
        Telemetry telemetry = null;
        if (request != null) {
            Logging logging = null;
            WorkloadAnalytics wa = null;
            if (request.getLogging() != null) {
                LoggingRequest loggingRequest = request.getLogging();
                logging = new Logging(
                        loggingRequest.isEnabled(),
                        loggingRequest.getOutput(),
                        loggingRequest.getAttributes());
            }
            if (request.getWorkloadAnalytics() != null) {
                WorkloadAnalyticsRequest waRequest = request.getWorkloadAnalytics();
                wa = new WorkloadAnalytics(waRequest.isEnabled(), waRequest.getDatabusEndpoint(),
                        null, null, waRequest.getAttributes());
            }
            telemetry = new Telemetry(logging, wa);
        }
        return telemetry;
    }
}

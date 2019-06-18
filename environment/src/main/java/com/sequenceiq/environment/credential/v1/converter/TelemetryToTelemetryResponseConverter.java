package com.sequenceiq.environment.credential.v1.converter;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.model.Logging;
import com.sequenceiq.cloudbreak.cloud.model.Telemetry;
import com.sequenceiq.cloudbreak.cloud.model.WorkloadAnalytics;
import com.sequenceiq.cloudbreak.common.json.Json;
import com.sequenceiq.environment.api.v1.environment.model.response.LoggingResponse;
import com.sequenceiq.environment.api.v1.environment.model.response.TelemetryResponse;
import com.sequenceiq.environment.api.v1.environment.model.response.WorkloadAnalyticsResponse;

@Component
public class TelemetryToTelemetryResponseConverter {

    public TelemetryResponse convertFromJson(Json json) {
        if (json != null) {
            Telemetry telemetry = null;
            try {
                telemetry = json.get(Telemetry.class);
            } catch (IOException e) {
                throw new IllegalArgumentException("Telemetry JSON is not valid", e);
            }
            return convert(telemetry);
        }
        return null;
    }

    public TelemetryResponse convert(Telemetry telemetry) {
        TelemetryResponse response = null;
        if (telemetry != null) {
            LoggingResponse loggingResponse = null;
            WorkloadAnalyticsResponse waResponse = null;
            if (telemetry.getLogging() != null) {
                Logging logging = telemetry.getLogging();
                loggingResponse = new LoggingResponse();
                loggingResponse.setAttributes(logging.getAttributes());
                loggingResponse.setOutput(logging.getOutputType());
                loggingResponse.setEnabled(logging.isEnabled());
            }
            if (telemetry.getWorkloadAnalytics() != null) {
                WorkloadAnalytics wa = telemetry.getWorkloadAnalytics();
                waResponse = new WorkloadAnalyticsResponse();
                waResponse.setEnabled(wa.isEnabled());
                waResponse.setAttributes(wa.getAttributes());
                waResponse.setDatabusEndpoint(wa.getDatabusEndpoint());
            }
            response = new TelemetryResponse();
            response.setLogging(loggingResponse);
            response.setWorkloadAnalytics(waResponse);
        }
        return response;
    }
}

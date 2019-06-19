package com.sequenceiq.distrox.v1.distrox.converter;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.sharedservice.SharedServiceV4Request;
import com.sequenceiq.cloudbreak.exception.BadRequestException;
import com.sequenceiq.distrox.api.v1.distrox.model.sharedservice.SdxV1Request;
import com.sequenceiq.sdx.api.endpoint.SdxEndpoint;
import com.sequenceiq.sdx.api.model.SdxClusterResponse;
import com.sequenceiq.sdx.api.model.SdxClusterStatusResponse;

@Component
public class SdxConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SdxConverter.class);

    @Inject
    private SdxEndpoint sdxEndpoint;

    public SharedServiceV4Request getSharedService(SdxV1Request sdx) {
        return getSharedServiceV4Request(sdx.getName());
    }

    public SharedServiceV4Request getSharedService(SdxV1Request sdx, String environemntName) {
        List<SdxClusterResponse> sdxList = sdxEndpoint.list(environemntName);
        StringBuilder name = new StringBuilder();
        if (sdx == null) {
            if (sdxList.isEmpty()) {
                LOGGER.info("We don't attach the cluster to any SDX, because the environemnt has not SDX. So we continue the creation as simple WORKLOAD.");
                return null;
            } else if (sdxList.size() > 1) {
                throw new BadRequestException("More than one SDX attached to the environment");
            }
            name.append(sdxList.get(0).getName());
        } else {
            name.append(sdx.getName());
        }
        SdxClusterResponse sdxClusterResponse = sdxList.stream()
                .filter(sr -> sr.getName().equals(name.toString()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("The given SDX not attached to the environment"));
        if (sdxClusterResponse.getStatus() != SdxClusterStatusResponse.RUNNING) {
            throw new BadRequestException(String.format("SDX status is invalid. Current state is %s instead of %s", sdxClusterResponse.getStatus().name(),
                    SdxClusterStatusResponse.RUNNING));
        }
        return getSharedServiceV4Request(name.toString());
    }

    public SdxV1Request getSdx(SharedServiceV4Request sharedServiceV4Request) {
        SdxV1Request sdx = new SdxV1Request();
        sdx.setName(sharedServiceV4Request.getDatalakeName());
        return sdx;
    }

    public SharedServiceV4Request getSharedServiceV4Request(String name) {
        SharedServiceV4Request sharedServiceV4Request = new SharedServiceV4Request();
        sharedServiceV4Request.setDatalakeName(name);
        return sharedServiceV4Request;
    }
}

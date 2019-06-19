package com.sequenceiq.distrox.v1.distrox.converter;

import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.sharedservice.SharedServiceV4Request;
import com.sequenceiq.cloudbreak.exception.BadRequestException;
import com.sequenceiq.distrox.api.v1.distrox.model.sharedservice.SdxV1Request;
import com.sequenceiq.sdx.api.endpoint.SdxEndpoint;
import com.sequenceiq.sdx.api.model.SdxClusterResponse;
import com.sequenceiq.sdx.api.model.SdxClusterStatusResponse;

@ExtendWith(MockitoExtension.class)
public class SdxConverterTest {

    @InjectMocks
    private SdxConverter underTest;

    @Mock
    private SdxEndpoint sdxEndpoint;

    @Test
    public void testGetSharedServiceWhenSdxNullAndEnvironmentHasNotSdx() {
        String environmenName = "environmenName";
        when(sdxEndpoint.list(environmenName)).thenReturn(Collections.emptyList());

        SharedServiceV4Request sdxRequest = underTest.getSharedService(null, environmenName);

        Assertions.assertNull(sdxRequest);
    }

    @Test
    public void testGetSharedServiceWhenSdxNullAndEnvironmentHasMoreThanOneSdx() {
        String environmenName = "environmenName";
        when(sdxEndpoint.list(environmenName)).thenReturn(List.of(new SdxClusterResponse(), new SdxClusterResponse()));

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class, () -> underTest.getSharedService(null, environmenName));
        Assertions.assertEquals(exception.getMessage(), "More than one SDX attached to the environment");
    }

    @Test
    public void testGetSharedServiceWhenSdxNullAndSdxInvIsRunning() {
        String environmenName = "environmenName";
        SdxClusterResponse sdxClusterResponse = new SdxClusterResponse();
        sdxClusterResponse.setName("some-sdx");
        sdxClusterResponse.setStatus(SdxClusterStatusResponse.RUNNING);
        when(sdxEndpoint.list(environmenName)).thenReturn(List.of(sdxClusterResponse));

        SharedServiceV4Request sdxRequest = underTest.getSharedService(null, environmenName);

        Assertions.assertEquals("some-sdx", sdxRequest.getDatalakeName());
    }

    @Test
    public void testGetSharedServiceWhenSdxNotAttachedToEnvironment() {
        String environmenName = "environmenName";
        SdxClusterResponse sdxClusterResponse = new SdxClusterResponse();
        sdxClusterResponse.setName("some-sdx");
        when(sdxEndpoint.list(environmenName)).thenReturn(List.of(sdxClusterResponse));

        SdxV1Request sdxRequest = new SdxV1Request();
        sdxRequest.setName("other-sdx-name");

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class, () -> underTest.getSharedService(sdxRequest, environmenName));
        Assertions.assertEquals(exception.getMessage(), "The given SDX not attached to the environment");
    }

    @Test
    public void testGetSharedServiceWhenSdxInvIsNotRunning() {
        String environmenName = "environmenName";
        SdxClusterResponse sdxClusterResponse = new SdxClusterResponse();
        sdxClusterResponse.setName("some-sdx");
        sdxClusterResponse.setStatus(SdxClusterStatusResponse.DELETE_FAILED);
        when(sdxEndpoint.list(environmenName)).thenReturn(List.of(sdxClusterResponse));

        SdxV1Request sdxRequest = new SdxV1Request();
        sdxRequest.setName("some-sdx");

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class, () -> underTest.getSharedService(sdxRequest, environmenName));
        Assertions.assertEquals(exception.getMessage(), "SDX status is invalid. Current state is DELETE_FAILED instead of RUNNING");
    }
}

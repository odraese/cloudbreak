package com.sequenceiq.it.cloudbreak.dto.clustertemplate;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.sequenceiq.cloudbreak.api.endpoint.v4.clustertemplate.responses.ClusterTemplateV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.authentication.StackAuthenticationV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.ClusterV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.instancegroup.InstanceGroupV4Request;
import com.sequenceiq.distrox.api.v1.distrox.model.DistroXV1Request;
import com.sequenceiq.distrox.api.v1.distrox.model.authentication.DistroXAuthenticationV1Request;
import com.sequenceiq.distrox.api.v1.distrox.model.cluster.DistroXClusterV1Request;
import com.sequenceiq.distrox.api.v1.distrox.model.cluster.cm.ClouderaManagerV1Request;
import com.sequenceiq.distrox.api.v1.distrox.model.cluster.cm.product.ClouderaManagerProductV1Request;
import com.sequenceiq.distrox.api.v1.distrox.model.cluster.cm.repository.ClouderaManagerRepositoryV1Request;
import com.sequenceiq.distrox.api.v1.distrox.model.instancegroup.InstanceGroupV1Request;
import com.sequenceiq.distrox.api.v1.distrox.model.instancegroup.template.InstanceTemplateV1Request;
import com.sequenceiq.distrox.api.v1.distrox.model.instancegroup.template.volume.RootVolumeV1Request;
import com.sequenceiq.it.cloudbreak.CloudbreakClient;
import com.sequenceiq.it.cloudbreak.Prototype;
import com.sequenceiq.it.cloudbreak.context.TestContext;
import com.sequenceiq.it.cloudbreak.dto.ClusterTestDto;
import com.sequenceiq.it.cloudbreak.dto.DeletableTestDto;
import com.sequenceiq.it.cloudbreak.dto.InstanceGroupTestDto;
import com.sequenceiq.it.cloudbreak.dto.environment.EnvironmentTestDto;
import com.sequenceiq.it.cloudbreak.dto.stack.StackTestDto;

@Prototype
public class DistroXTestDto extends DeletableTestDto<DistroXV1Request, ClusterTemplateV4Response,
        DistroXTestDto, ClusterTemplateV4Response> {

    public DistroXTestDto(TestContext testContext) {
        super(new DistroXV1Request(), testContext);
    }

    public DistroXTestDto() {
        super(DistroXTestDto.class.getSimpleName().toUpperCase());
    }

    public DistroXTestDto valid() {
        return withName(resourceProperyProvider().getName())
                .withEnvironmentName(getTestContext().get(EnvironmentTestDto.class).getName())
                .withCluster(getTestContext().init(ClusterTestDto.class).getRequest())
                .withInstanceGroups(getTestContext().init(InstanceGroupTestDto.class).getRequest())
                .withAuthentication(getTestContext().init(StackTestDto.class).getRequest().getAuthentication());
    }

    private DistroXTestDto withAuthentication(StackAuthenticationV4Request authenticationRequest) {
        DistroXAuthenticationV1Request authentication = new DistroXAuthenticationV1Request();
        authentication.setLoginUserName(authenticationRequest.getLoginUserName());
        authentication.setPublicKey(authenticationRequest.getPublicKey());
        authentication.setPublicKeyId(authenticationRequest.getPublicKeyId());
        getRequest().setAuthentication(authentication);
        return this;
    }

    private DistroXTestDto withInstanceGroups(InstanceGroupV4Request instanceGroupTestDto) {
        InstanceGroupV1Request instanceGroup = new InstanceGroupV1Request();
        InstanceTemplateV1Request template = new InstanceTemplateV1Request();
        RootVolumeV1Request rootVolume = new RootVolumeV1Request();
        rootVolume.setSize(instanceGroupTestDto.getTemplate().getRootVolume().getSize());
        template.setRootVolume(rootVolume);
        instanceGroup.setTemplate(template);
        instanceGroup.setName(instanceGroupTestDto.getName());
        getRequest().setInstanceGroups(Set.of(instanceGroup));
        return this;
    }

    private DistroXTestDto withCluster(ClusterV4Request clusterV4Request) {
        DistroXClusterV1Request cluster = new DistroXClusterV1Request();
        cluster.setBlueprintName(clusterV4Request.getBlueprintName());
        ClouderaManagerV1Request cm = new ClouderaManagerV1Request();
        ClouderaManagerRepositoryV1Request repository = new ClouderaManagerRepositoryV1Request();
        cm.setRepository(repository);
        ClouderaManagerProductV1Request product = new ClouderaManagerProductV1Request();
        cm.setProducts(List.of(product));
        cluster.setCm(cm);
        getRequest().setCluster(cluster);
        return this;
    }

    private DistroXTestDto withEnvironmentName(String name) {
        getRequest().setEnvironmentName(name);
        return this;
    }

    public DistroXTestDto withName(String name) {
        getRequest().setName(name);
        setName(name);
        return this;
    }

    @Override
    protected String name(ClusterTemplateV4Response entity) {
        return null;
    }

    @Override
    public Collection<ClusterTemplateV4Response> getAll(CloudbreakClient client) {
        return null;
    }

    @Override
    public void delete(TestContext testContext, ClusterTemplateV4Response entity, CloudbreakClient client) {

    }
}

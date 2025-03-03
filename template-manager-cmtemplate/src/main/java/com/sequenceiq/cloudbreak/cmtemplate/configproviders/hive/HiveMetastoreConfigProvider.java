package com.sequenceiq.cloudbreak.cmtemplate.configproviders.hive;

import static com.sequenceiq.cloudbreak.cmtemplate.configproviders.ConfigUtils.config;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.cloudera.api.swagger.model.ApiClusterTemplateConfig;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sequenceiq.cloudbreak.api.endpoint.v4.database.base.DatabaseType;
import com.sequenceiq.cloudbreak.cmtemplate.CmTemplateProcessor;
import com.sequenceiq.cloudbreak.cmtemplate.configproviders.AbstractRoleConfigProvider;
import com.sequenceiq.cloudbreak.cmtemplate.configproviders.ConfigUtils;
import com.sequenceiq.cloudbreak.domain.RDSConfig;
import com.sequenceiq.cloudbreak.dto.KerberosConfig;
import com.sequenceiq.cloudbreak.template.TemplatePreparationObject;
import com.sequenceiq.cloudbreak.template.views.RdsView;

@Component
public class HiveMetastoreConfigProvider extends AbstractRoleConfigProvider {

    @Override
    public List<ApiClusterTemplateConfig> getServiceConfigs(TemplatePreparationObject templatePreparationObject) {
        Optional<RDSConfig> rdsConfigOptional = getFirstRDSConfigOptional(templatePreparationObject);
        Preconditions.checkArgument(rdsConfigOptional.isPresent());
        RdsView hiveView = new RdsView(rdsConfigOptional.get());

        List<ApiClusterTemplateConfig> configs = Lists.newArrayList(
                config("hive_metastore_database_host", hiveView.getHost()),
                config("hive_metastore_database_name", hiveView.getDatabaseName()),
                config("hive_metastore_database_password", hiveView.getConnectionPassword()),
                config("hive_metastore_database_port", hiveView.getPort()),
                config("hive_metastore_database_type", hiveView.getSubprotocol()),
                config("hive_metastore_database_user", hiveView.getConnectionUserName())
        );

        Optional<KerberosConfig> kerberosConfigOpt = templatePreparationObject.getKerberosConfig();
        if (kerberosConfigOpt.isPresent()) {
            String realm = Optional.ofNullable(kerberosConfigOpt.get().getRealm()).orElse("").toUpperCase();
            String safetyValveValue = "<property><name>hive.server2.authentication.kerberos.principal</name><value>hive/_HOST@" + realm
                    + "</value></property><property><name>hive.server2.authentication.kerberos.keytab</name><value>hive.keytab</value></property>";
            configs.add(config("hive_service_config_safety_valve", safetyValveValue));
        }

        return configs;
    }

    @Override
    public String getServiceType() {
        return HiveRoles.HIVE;
    }

    @Override
    public List<String> getRoleTypes() {
        return List.of(HiveRoles.HIVEMETASTORE);
    }

    @Override
    public boolean isConfigurationNeeded(CmTemplateProcessor cmTemplateProcessor, TemplatePreparationObject source) {
        return getFirstRDSConfigOptional(source).isPresent() && cmTemplateProcessor.isRoleTypePresentInService(getServiceType(), getRoleTypes());
    }

    @Override
    protected List<ApiClusterTemplateConfig> getRoleConfigs(String roleType, TemplatePreparationObject source) {
        return List.of(
                config("metastore_canary_health_enabled", Boolean.FALSE.toString())
        );
    }

    private Optional<RDSConfig> getFirstRDSConfigOptional(TemplatePreparationObject source) {
        return ConfigUtils.getFirstRDSConfigOptional(source, DatabaseType.HIVE);
    }

}

package com.sequenceiq.cloudbreak.cmtemplate.configproviders;

import java.util.Optional;

import com.cloudera.api.swagger.model.ApiClusterTemplateConfig;
import com.cloudera.api.swagger.model.ApiClusterTemplateVariable;
import com.sequenceiq.cloudbreak.api.endpoint.v4.database.base.DatabaseType;
import com.sequenceiq.cloudbreak.domain.RDSConfig;
import com.sequenceiq.cloudbreak.template.TemplatePreparationObject;
import com.sequenceiq.cloudbreak.template.filesystem.StorageLocationView;

public class ConfigUtils {

    private static final String CM_SAFETY_VALVE_PROPERTY_FORMAT = "<property><name>%s</name><value>%s</value></property>";

    private ConfigUtils() { }

    /**
     * Convenience method for creating an {@link ApiClusterTemplateConfig} with a value.
     *
     * @param name config name
     * @param value config value
     * @return config object
     */
    public static ApiClusterTemplateConfig config(String name, String value) {
        return new ApiClusterTemplateConfig().name(name).value(value);
    }

    /**
     * Convenience method for creating an {@link ApiClusterTemplateConfig} with a variable.
     *
     * @param name config name
     * @param variable config variable
     * @return config object
     */
    public static ApiClusterTemplateConfig configVar(String name, String variable) {
        return new ApiClusterTemplateConfig().name(name).variable(variable);
    }

    /**
     * Convenience method for creating an {@link ApiClusterTemplateVariable}.
     *
     * @param name variable name
     * @param value variable value
     * @return variable object
     */
    public static ApiClusterTemplateVariable variable(String name, String value) {
        return new ApiClusterTemplateVariable().name(name).value(value);
    }

    public static Optional<RDSConfig> getFirstRDSConfigOptional(TemplatePreparationObject source, DatabaseType databaseType) {
        return source.getRdsConfigs().stream()
                .filter(rds -> databaseType.name().equalsIgnoreCase(rds.getType()))
                .findFirst();
    }

    public static Optional<StorageLocationView> getStorageLocationForServiceProperty(TemplatePreparationObject source, String serviceProperty) {
        return source.getFileSystemConfigurationView().flatMap(configview -> configview.getLocations().stream()
                .filter(s -> s.getProperty().equalsIgnoreCase(serviceProperty))
                .findFirst());
    }

    public static String getSafetyValveProperty(String key, String value) {
        return String.format(CM_SAFETY_VALVE_PROPERTY_FORMAT, key, value);
    }
}
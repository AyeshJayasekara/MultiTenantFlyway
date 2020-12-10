package com.ayeshj.flywaytenant.configuration;

import com.ayeshj.flywaytenant.property.DatasourcePropertyModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Configuration property file for spring context to map with the declared property file
 *
 * @author Ayesh Jayasekara
 * @since Dec 2020
 */

@Data
@ConfigurationProperties(prefix = "app.datasource")
public class PropertyBasedDatasourceConfiguration {

    private List<DatasourcePropertyModel> property;
}

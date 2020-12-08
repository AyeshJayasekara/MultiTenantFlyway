package com.ayeshj.flywaytenant.configuration;

import com.ayeshj.flywaytenant.property.DatasourcePropertyModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "app.datasource")
public class PropertyBasedDatasourceConfiguration {

    private List<DatasourcePropertyModel> property;
}

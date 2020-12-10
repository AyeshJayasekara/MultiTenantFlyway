package com.ayeshj.flywaytenant.configuration;

import com.ayeshj.flywaytenant.property.DatasourcePropertyModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration class for setting up data sources according to given configuration properties
 *
 * @author Ayesh Jayasekara
 * @since Dec 2020
 */

@Configuration
@Slf4j
public class DataSourcesConfiguration {

    private final PropertyBasedDatasourceConfiguration datasourceConfiguration;
    private final ApplicationContext applicationContext;

    /**
     * Spting autowired constructor
     *
     * @param datasourceConfiguration Property model
     * @param appContext              Application context of the running application
     */
    @Autowired
    public DataSourcesConfiguration(PropertyBasedDatasourceConfiguration datasourceConfiguration,
                                    ApplicationContext appContext) {
        this.applicationContext = appContext;
        log.info("DATASOURCE(S) INITIALIZATION STARTED...");
        this.datasourceConfiguration = datasourceConfiguration;
    }


    /**
     * Registers a map of datasource(s) mapped to tenant name as a bean
     *
     * @return Configured datasource map bean
     */
    @Bean("datasourceConfigurationMap")
    public Map<String, DataSource> datasourceConfigurationMap() {

        if (datasourceConfiguration.getProperty() == null || datasourceConfiguration.getProperty().isEmpty()) {
            log.error("NO DATASOURCE(S) COULD BE CONFIGURED! PLEASE MAKE SURE YOU HAVE CORRECT PROPERTIES SET!");
            safelyShutdown();
        }

        return configure();
    }

    /**
     * SQL root directory configuration properties as spring bean
     *
     * @return Properties object with configured SQL directories if provided on application.properties as a bean
     */
    @Bean("sqlDirectoryProperties")
    @DependsOn("datasourceConfigurationMap")
    public Properties sqlDirectoryProperties() {
        Properties properties = new Properties();
        datasourceConfiguration.getProperty().forEach(tenant -> {

            String path = tenant.getSqlRepositoryPath();
            if (path != null && !path.isEmpty()) {
                properties.put(tenant.getDataSourceName(), path);
            }
        });

        return properties;
    }

    /**
     * Placeholder file path configuration properties as a spring bean
     *
     * @return Configuration bean
     */
    @Bean("placeholderProperties")
    @DependsOn("datasourceConfigurationMap")
    public Properties placeholderProperties() {
        Properties properties = new Properties();
        datasourceConfiguration.getProperty().forEach(tenant -> {

            String path = tenant.getPlaceholderPropertiesPath();
            if (path != null && !path.isEmpty()) {
                properties.put(tenant.getDataSourceName(), path);
            }
        });

        return properties;
    }

    /**
     * Method for datasource configuration from properties
     *
     * @return Map of configured datasource(s)
     */
    private Map<String, DataSource> configure() {
        HashMap<String, DataSource> map = new HashMap<>();

        datasourceConfiguration.getProperty()
                .forEach(datasourcePropertyModel ->
                        map.put(datasourcePropertyModel.getDataSourceName(), configure(datasourcePropertyModel)));

        return map;
    }

    /**
     * Method to configure and set up datasource
     *
     * @param datasourcePropertyModel Datasource properties model
     * @return Configured datasource
     */
    private DataSource configure(DatasourcePropertyModel datasourcePropertyModel) {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(datasourcePropertyModel.getDriverClassName());
        dataSourceBuilder.url(datasourcePropertyModel.getUrl());
        dataSourceBuilder.username(datasourcePropertyModel.getUsername());
        dataSourceBuilder.password(datasourcePropertyModel.getPassword());

        log.info("CONFIGURING DATA SOURCE FOR : {}", datasourcePropertyModel.getDataSourceName());

        return dataSourceBuilder.build();
    }

    /**
     * Method to shutdown Spring Application safely
     */
    private void safelyShutdown() {
        log.warn("ATTEMPTING TO SHUTDOWN APPLICATION SAFELY. BYE!");
        System.exit(SpringApplication.exit(applicationContext, () -> 1));
    }

}

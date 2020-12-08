package com.ayeshj.flywaytenant.configuration;

import com.ayeshj.flywaytenant.property.DatasourcePropertyModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class DataSourcesConfiguration {

    private final PropertyBasedDatasourceConfiguration datasourceConfiguration;
    private final ApplicationContext applicationContext;

    @Autowired
    public DataSourcesConfiguration(PropertyBasedDatasourceConfiguration datasourceConfiguration,
                                    ApplicationContext appContext) {
        this.applicationContext = appContext;
        log.info("DATASOURCE(S) INITIALIZATION STARTED...");
        this.datasourceConfiguration = datasourceConfiguration;
    }


    @Bean("datasourceConfigurationMap")
    public Map<String, DataSource> datasourceConfigurationMap(){

        if(datasourceConfiguration.getProperty() == null || datasourceConfiguration.getProperty().isEmpty()){
            log.error("NO DATASOURCE(S) COULD BE CONFIGURED! PLEASE MAKE SURE YOU HAVE CORRECT PROPERTIES SET!");
            safelyShutdown();
        }

        return configure();
    }

    private Map<String, DataSource> configure(){
        HashMap<String, DataSource> map = new HashMap<>();

        datasourceConfiguration.getProperty()
                .forEach(datasourcePropertyModel ->
                        map.put(datasourcePropertyModel.getDataSourceName(), configure(datasourcePropertyModel)));


        return map;
    }

    private DataSource configure(DatasourcePropertyModel datasourcePropertyModel) {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(datasourcePropertyModel.getDriverClassName());
        dataSourceBuilder.url(datasourcePropertyModel.getUrl());
        dataSourceBuilder.username(datasourcePropertyModel.getUsername());
        dataSourceBuilder.password(datasourcePropertyModel.getPassword());

        log.info("CONFIGURING DATA SOURCE FOR : {}", datasourcePropertyModel.getDataSourceName());

        return dataSourceBuilder.build();
    }

    private void safelyShutdown(){
        log.warn("ATTEMPTING TO SHUTDOWN APPLICATION SAFELY. BYE!");
        System.exit(SpringApplication.exit(applicationContext, () -> 1));
    }

}

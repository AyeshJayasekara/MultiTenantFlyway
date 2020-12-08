package com.ayeshj.flywaytenant;

import com.ayeshj.flywaytenant.configuration.PropertyBasedDatasourceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = {FlywayAutoConfiguration.class, DataSourceAutoConfiguration.class})
@EnableConfigurationProperties({PropertyBasedDatasourceConfiguration.class})
public class FlywayTenantApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlywayTenantApplication.class, args);
    }

}

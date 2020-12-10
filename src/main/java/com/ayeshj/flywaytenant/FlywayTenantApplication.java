package com.ayeshj.flywaytenant;

import com.ayeshj.flywaytenant.configuration.PropertyBasedDatasourceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 *
 * Flyway based multi tenant database migration wizard application
 *
 * @author Ayesh Jayasekara (ejkpac@gmail.com)
 * @since Dec 2020
 * @see <a href="https://www.linkedin.com/in/ayesh-jayasekara/">Ayesh Jayasekara on LinkedIn</a>
 *
 */
@SpringBootApplication(exclude = {FlywayAutoConfiguration.class, DataSourceAutoConfiguration.class})
@EnableConfigurationProperties({PropertyBasedDatasourceConfiguration.class})
public class FlywayTenantApplication {

    /**
     * This is where the magic begins!
     * @param args Usual arguments to feed in anything. Ignored like your cat ignores you!
     */
    public static void main(String[] args) {
        SpringApplication.run(FlywayTenantApplication.class, args);
    }

}

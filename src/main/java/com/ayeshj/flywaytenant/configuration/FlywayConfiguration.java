package com.ayeshj.flywaytenant.configuration;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Main configuration file for Flyway.
 * <p>
 * Contains all the logic related to migrations and placeholders
 *
 * @author Ayesh Jayasekara
 * @since Dec 2020
 */

@Configuration
@Slf4j
public class FlywayConfiguration {

    private final Map<String, DataSource> dataSourceMap;
    private final Environment environment;
    private static final String DEFAULT_SQL_ROOT_DIRECTORY = "db/migration/";


    /**
     * Constructor for Spring DI with required dependencies
     *
     * @param dataSourceMap Map of datasource configured and exposed as a bean.
     * @param environment   Spring application environment
     */
    @Autowired
    FlywayConfiguration(@Qualifier("datasourceConfigurationMap") Map<String, DataSource> dataSourceMap,
                        Environment environment) {
        this.dataSourceMap = dataSourceMap;
        this.environment = environment;
        log.info("DATASOURCE(S) CONFIGURED SUCCESSFULLY!");
        log.info("STARTING MIGRATION...");


    }

    /**
     * Method to initiate migrations for configured datasource(s)
     * <p>
     * Will be triggered by Spring context once the configurations are ready and constructed
     */
    @PostConstruct
    public void migrate() {

        String scriptLocation = environment.getProperty("migration.script.rootLocation", DEFAULT_SQL_ROOT_DIRECTORY);

        dataSourceMap.keySet().forEach(tenant -> {

            log.info("STARTING MIGRATION FOR DB : {}", tenant);
            String tenantSQLRootDirectory = scriptLocation.concat(tenant);
            log.info("SQL REPOSITORY LOCATED AT : {}", tenantSQLRootDirectory);


            FluentConfiguration flywayConfiguration = Flyway.configure()
                    .locations(tenantSQLRootDirectory)
                    .baselineOnMigrate(Boolean.TRUE)
                    .dataSource(dataSourceMap.get(tenant))
                    .schemas(tenant);

            flywayConfiguration = configurePlaceholderReplacer(flywayConfiguration, tenantSQLRootDirectory);
            Flyway flyway = flywayConfiguration.load();

            log.warn("APPLYING MIGRATIONS FOR DB : {}", tenant);
            flyway.migrate();

            log.warn("COMPLETED APPLYING MIGRATIONS FOR DB : {}", tenant);

        });

        log.info("*** MIGRATION COMPLETE! HAPPY CODING! ***");
    }


    /**
     * Method to configure placeholder replacements
     *
     * @param flywayConfiguration    Flyway configurer instance
     * @param tenantSQLRootDirectory Directory to look for properties file
     * @return Flyway configurer instance configured with placeholder replacer (if found)
     */
    FluentConfiguration configurePlaceholderReplacer(FluentConfiguration flywayConfiguration,
                                                     String tenantSQLRootDirectory) {


        String absoluteFilePath = tenantSQLRootDirectory.concat("/placeholder.properties");

        try (InputStream inputStream = fetchStream(absoluteFilePath)) {

            Map<String, String> propertyMap = new HashMap<>();
            Properties properties = new Properties();
            properties.load(inputStream);
            properties.keySet().forEach(key -> propertyMap.put((String) key, properties.getProperty((String) key)));

            flywayConfiguration.placeholders(propertyMap);
            flywayConfiguration.placeholderReplacement(true);

            log.info("PLACEHOLDER PROPERTIES SUCCESSFULLY LOADED. TOTAL ENTRIES LOAD : {}", propertyMap.size());

        } catch (FileNotFoundException fileNotFoundException) {
            log.warn("NO PLACEHOLDER FILE FOUND AT LOCATION : {}  <<< IGNORING PLACEHOLDER FILE >>>", absoluteFilePath);

        } catch (IOException exception) {
            log.error("UNABLE TO LOAD PLACEHOLDER PROPERTIES. FILE CAN NOT BE OPENED AT : {}", absoluteFilePath);
        }

        return flywayConfiguration;

    }

    /**
     * Method to handle classpath resources and file system resources differently
     *
     * @param absoluteFilePath Absolute path to properties file
     * @return Input stream for the given file
     * @throws IOException Thrown if the file was not found or unreadable
     */
    private InputStream fetchStream(String absoluteFilePath) throws IOException {
        if (absoluteFilePath.contains("filesystem:")) {

            absoluteFilePath = absoluteFilePath.replace("filesystem:", "");
            log.info("CHECKING ABSOLUTE PATH {} FOR PLACEHOLDER PROPERTIES", absoluteFilePath);

            return new FileInputStream(absoluteFilePath);
        } else {

            log.info("CHECKING CLASS PATH {} FOR PLACEHOLDER PROPERTIES", absoluteFilePath);
            ClassPathResource resource = new ClassPathResource(absoluteFilePath);

            return new FileInputStream(resource.getFile());
        }

    }


}

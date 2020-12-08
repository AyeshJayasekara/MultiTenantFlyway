package com.ayeshj.flywaytenant.configuration;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@Slf4j
public class FlywayConfiguration {

    private final Map<String, DataSource> dataSourceMap;
    private final Environment environment;
    private static final String DEFAULT_SQL_ROOT_DIRECTORY = "db/migration/";


    @Autowired
    FlywayConfiguration(@Qualifier("datasourceConfigurationMap") Map<String, DataSource> dataSourceMap,
                        Environment environment){
        this.dataSourceMap = dataSourceMap;
        this.environment = environment;
        log.info("DATASOURCE(S) CONFIGURED SUCCESSFULLY!");
        log.info("STARTING MIGRATION...");


        migrate();
    }

    public void migrate() {

        String scriptLocation = environment.getProperty("migration.script.rootLocation", DEFAULT_SQL_ROOT_DIRECTORY);

        dataSourceMap.keySet().forEach(tenant -> {

            log.info("STARTING MIGRATION FOR DB : {}", tenant);
            log.info("SQL REPOSITORY LOCATED AT : {}", scriptLocation.concat(tenant));
            Flyway flyway = Flyway.configure()
                    .locations(scriptLocation.concat(tenant))
                    .baselineOnMigrate(Boolean.TRUE)
                    .dataSource(dataSourceMap.get(tenant))
                    .schemas(tenant)
                    .load();

            log.warn("APPLYING MIGRATIONS FOR DB : {}", tenant);
            flyway.migrate();
            log.warn("COMPLETED APPLYING MIGRATIONS FOR DB : {}", tenant);

        });

        log.info("*** MIGRATION COMPLETE! HAPPY CODING! ***");
    }


}

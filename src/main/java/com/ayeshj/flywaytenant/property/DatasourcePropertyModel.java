package com.ayeshj.flywaytenant.property;

import lombok.Data;

/**
 * Property model for reading property values from the application properties file
 *
 * @author Ayesh Jayasekara
 * @since Dec 2020
 */

@Data
public class DatasourcePropertyModel {

    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private String name;
    private String dataSourceName;
}

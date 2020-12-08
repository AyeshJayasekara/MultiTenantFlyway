package com.ayeshj.flywaytenant.property;

import lombok.Data;


@Data
public class DatasourcePropertyModel {

    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private String name;
    private String dataSourceName;
}

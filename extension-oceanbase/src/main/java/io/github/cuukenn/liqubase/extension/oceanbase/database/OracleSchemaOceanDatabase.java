package io.github.cuukenn.liqubase.extension.oceanbase.database;

import liquibase.database.core.MySQLDatabase;

/**
 * OceanBase for oracle schema
 * //TODO 找不到企业版安装包，社区版不支持Oracle租户，先这样
 *
 * @author changgg
 */
public class OracleSchemaOceanDatabase extends MySQLDatabase {
    @Override
    public String getShortName() {
        return "oceanBase";
    }

    @Override
    public String getDefaultDriver(String url) {
        return "com.alipay.oceanbase.jdbc.Driver";
    }
}

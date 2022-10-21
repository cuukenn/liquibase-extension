package io.github.cuukenn.liqubase.extension.oceanbase.database;

import liquibase.Scope;
import liquibase.change.AddColumnConfig;
import liquibase.change.Change;
import liquibase.change.core.CreateIndexChange;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.DatabaseConnection;
import liquibase.database.core.MySQLDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.sql.visitor.SqlVisitor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * OceanBase for mysql schema
 *
 * @author changgg
 */
public class MySqlSchemaOceanDatabase extends MySQLDatabase {

    @Override
    public String getShortName() {
        return "oceanBase";
    }

    @Override
    protected String getDefaultDatabaseProductName() {
        return "OceanBase";
    }

    @Override
    public Integer getDefaultPort() {
        return 2881;
    }

    @Override
    public int getPriority() {
        return super.getPriority() + 1;
    }

    @Override
    public String getDefaultDriver(String url) {
        //先判断是否为mysql连接从而获取对应的驱动
        //否则使用oceanBase官方驱动
        String driverName = super.getDefaultDriver(url);
        if (driverName == null) {
            driverName = "com.1";
        }
        return driverName;
    }

    @Override
    public boolean isCorrectDatabaseImplementation(DatabaseConnection conn) throws DatabaseException {
        boolean mysqlSchema = super.isCorrectDatabaseImplementation(conn);
        //属于mysql或者mysql兼容的类型
        //此时无法判断是否为OceanBase,需要获取版本信息来判断
        if (mysqlSchema) {
            if (conn instanceof JdbcConnection) {
                Connection connection = ((JdbcConnection) conn).getUnderlyingConnection();
                try (ResultSet resultSet = connection.createStatement().executeQuery("SELECT @@VERSION")) {
                    if (resultSet.next()) {
                        String version = resultSet.getString(1);
                        if (version != null && version.toLowerCase().contains(getDefaultDatabaseProductName().toLowerCase())) {
                            return true;
                        }
                    }
                } catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }
        }
        //mysql模式判断失败,意味着可能使用oceanBase官方的url格式
        String url;
        return (url = conn.getURL()) != null && url.toLowerCase().startsWith("jdbc:oceanbase");
    }

    @Override
    public void executeStatements(Change change, DatabaseChangeLog changeLog, List<SqlVisitor> sqlVisitors) throws LiquibaseException {
        if (change instanceof CreateIndexChange) {
            for (AddColumnConfig columnConfig : ((CreateIndexChange) change).getColumns()) {
                if (Boolean.TRUE.equals(columnConfig.getDescending())) {
                    Scope.getCurrentScope().getLog(MySqlSchemaOceanDatabase.class)
                        .warning("OceanBase not support desc order when create index,unset " + ((CreateIndexChange) change).getIndexName() + "'s order for " + columnConfig.getName());
                    columnConfig.setDescending(null);
                }
            }
        }
        super.executeStatements(change, changeLog, sqlVisitors);
    }
}

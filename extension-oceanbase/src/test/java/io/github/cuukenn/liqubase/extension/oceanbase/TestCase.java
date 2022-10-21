package io.github.cuukenn.liqubase.extension.oceanbase;

import liquibase.Liquibase;
import liquibase.Scope;
import liquibase.database.jvm.JdbcConnection;
import liquibase.logging.Logger;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author changgg
 */
public class TestCase {
    private static final Logger log = Scope.getCurrentScope().getLog(TestCase.class);

    @Test
    public void test() {
        log.info("test");
    }

    @Test
    public void test_connect_mysql() throws Exception {
        try (Connection connection = DriverManager.getConnection(TestConstant.MYSQL_URL); Statement statement = connection.createStatement()) {
            log.info("autoCommit:" + connection.getAutoCommit());
            showTables(statement);
        }
    }

    @Test
    public void test_connect_oceanbase_mysql_schema() throws Exception {
        try (Connection connection = DriverManager.getConnection(TestConstant.OCEAN_BASE_MYSQL_SCHEMA_URL); Statement statement = connection.createStatement()) {
            log.info("autoCommit:" + connection.getAutoCommit());
            showTables(statement);
        }
    }

    private void showTables(Statement statement) throws Exception {
        try (ResultSet resultSet = statement.executeQuery("SHOW TABLES")) {
            while (resultSet.next()) {
                log.info("table:" + resultSet.getString(1));
            }
        }
    }

    /**
     * 测试mysql是否正常
     *
     * @throws Exception exception
     */
    @Test
    public void test_run_changelog_mysql() throws Exception {
        try (Connection connection = DriverManager.getConnection(TestConstant.MYSQL_URL)) {
            log.info("autoCommit:" + connection.getAutoCommit());
            Liquibase liquibase = new Liquibase("changelog.yml", new ClassLoaderResourceAccessor(), new JdbcConnection(connection));
            liquibase.update("");
        }
    }

    /**
     * 测试OceanBase的mysql模式下的表现
     *
     * @throws Exception exception
     */
    @Test
    public void test_run_changelog_mysql_schema() throws Exception {
        try (Connection connection = DriverManager.getConnection(TestConstant.OCEAN_BASE_MYSQL_SCHEMA_URL)) {
            log.info("autoCommit:" + connection.getAutoCommit());
            Liquibase liquibase = new Liquibase("changelog.yml", new ClassLoaderResourceAccessor(), new JdbcConnection(connection));
            liquibase.update("");
        }
    }
}

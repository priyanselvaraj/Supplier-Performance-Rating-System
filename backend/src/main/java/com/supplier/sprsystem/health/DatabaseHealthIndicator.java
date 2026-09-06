package com.supplier.sprsystem.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                DatabaseMetaData metaData = connection.getMetaData();
                try (Statement statement = connection.createStatement()) {
                    statement.execute("SELECT 1");
                }
                return Health.up()
                        .withDetail("database", metaData.getDatabaseProductName())
                        .withDetail("version", metaData.getDatabaseProductVersion())
                        .withDetail("status", "CONNECTED")
                        .build();
            } else {
                return Health.down()
                        .withDetail("error", "Database connection validation timeout")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("status", "UNREACHABLE")
                    .build();
        }
    }
}

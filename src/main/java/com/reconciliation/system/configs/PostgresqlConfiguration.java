package com.reconciliation.system.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class PostgresqlConfiguration {
    @Value("${app.datasource.driver-class}")
    private String driverClass;
    @Value("${app.datasource.url}")
    private String url;
    @Value("${app.datasource.username}")
    private String username;
    @Value("${app.datasource.password}")
    private String password;

    @Bean
    public Connection getDataSource() throws SQLException {
        DriverManagerDataSource manager = new DriverManagerDataSource();
        manager.setDriverClassName(driverClass);
        manager.setUrl(url);
        manager.setUsername(username);
        manager.setPassword(password);

        Connection connection = manager.getConnection();

        if (driverClass.contains("h2")) {
            String createReportQuery = """
                    CREATE TABLE reports (
                        id UUID PRIMARY KEY NOT NULL,
                        reportReceivedAt TIMESTAMP NOT NULL
                    )
                    """;
            connection.createStatement().executeUpdate(createReportQuery);

            String createTransactionQuery = """
                    CREATE TABLE sale_transactions (
                        id UUID PRIMARY KEY NOT NULL,
                        saleId VARCHAR(255) NOT NULL,
                        grossAmount DECIMAL(10,2) NOT NULL,
                        currency VARCHAR(255) NOT NULL,
                        date TIMESTAMP NOT NULL,
                        paymentMethod VARCHAR(255) NOT NULL,
                        installments INTEGER NOT NULL
                    )
                    """;
            connection.createStatement().executeUpdate(createTransactionQuery);

            String createBankTransactionQuery = """
                    CREATE TABLE bank_transactions (
                        bank_tx_id VARCHAR(255) PRIMARY KEY NOT NULL,
                        post_date TIMESTAMP NOT NULL,
                        amount DECIMAL(10,2) NOT NULL,
                        currency VARCHAR(255) NOT NULL,
                        description VARCHAR(255) NOT NULL,
                        direction VARCHAR(255) NOT NULL
                    )
                    """;
            connection.createStatement().executeUpdate(createBankTransactionQuery);
        }

        return connection;
    }

}

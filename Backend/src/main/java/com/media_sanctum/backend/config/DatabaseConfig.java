package com.media_sanctum.backend.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.DateClass;
import org.sqlite.SQLiteConfig.JournalMode;
import org.sqlite.SQLiteConfig.SynchronousMode;
import org.sqlite.SQLiteDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class DatabaseConfig {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean
    @Primary
    public DataSource dataSource(
            @Value("${media-sanctum.config-dir}") String dbPath,
            @Value("${media-sanctum.db.busy-timeout-ms}") int busyTimeoutMs,
            @Value("${media-sanctum.db.pool-size}") int poolSize,
            @Value("${media-sanctum.db.connection-timeout-ms}") int connectionTimeoutMs
    ) {
        try {
            Files.createDirectories(Path.of(dbPath));
        } catch (IOException e) {
            LOG.error("Failed to create db path: {}", dbPath, e);
            throw new RuntimeException(e);
        }

        SQLiteConfig sqliteConfig = new SQLiteConfig();
        sqliteConfig.setJournalMode(JournalMode.WAL);
        sqliteConfig.setSynchronous(SynchronousMode.NORMAL);
        sqliteConfig.setBusyTimeout(busyTimeoutMs);
        sqliteConfig.enforceForeignKeys(true);
        sqliteConfig.setDateClass(DateClass.TEXT.getValue());

        SQLiteDataSource sqLiteDataSource = new SQLiteDataSource(sqliteConfig);
        sqLiteDataSource.setUrl("jdbc:sqlite:" + dbPath + "/media-sanctum.db");

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSource(sqLiteDataSource);
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setConnectionTimeout(connectionTimeoutMs);

        return new HikariDataSource(hikariConfig);
    }
}

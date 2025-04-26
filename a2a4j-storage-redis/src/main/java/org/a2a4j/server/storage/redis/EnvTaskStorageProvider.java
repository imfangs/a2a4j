// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.server.storage.redis;

import org.a2a4j.server.storage.TaskStorage;
import org.a2a4j.server.storage.TaskStorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;

/**
 * A TaskStorageProvider implementation that creates a RedisTaskStorage
 * configured from environment variables or system properties.
 */
public class EnvTaskStorageProvider implements TaskStorageProvider {

    private static final Logger LOG = LoggerFactory.getLogger(EnvTaskStorageProvider.class);

    // Configuration property names
    private static final String REDIS_HOST_PROP = "A2A_STORAGE_REDIS_HOST";
    private static final String REDIS_PORT_PROP = "A2A_STORAGE_REDIS_PORT";
    private static final String REDIS_USERNAME_PROP = "A2A_STORAGE_REDIS_USERNAME";
    private static final String REDIS_PASSWORD_PROP = "A2A_STORAGE_REDIS_PASSWORD";
    private static final String REDIS_SSL_PROP = "A2A_STORAGE_REDIS_SSL";
    private static final String REDIS_TLS_PROP = "A2A_STORAGE_REDIS_TLS";

    /**
     * Provides a RedisTaskStorage instance configured from environment variables.
     * If the required environment variables are not set, returns null.
     *
     * @return a configured RedisTaskStorage or null if configuration is not available
     */
    @Override
    public TaskStorage provide() {
        LOG.info("Attempting to create Redis task storage from environment");

        String host = getEnv(REDIS_HOST_PROP);
        String port = getEnv(REDIS_PORT_PROP, "6379");
        String username = getEnv(REDIS_USERNAME_PROP);
        String password = getEnv(REDIS_PASSWORD_PROP);
        boolean ssl = Boolean.parseBoolean(getEnv(REDIS_SSL_PROP, "false"));
        boolean tls = Boolean.parseBoolean(getEnv(REDIS_TLS_PROP, "false"));

        // If no host is specified, we can't create a Redis connection
        if (host == null || host.isEmpty()) {
            LOG.info("No Redis host specified, not creating Redis task storage");
            return null;
        }

        try {
            LOG.info("Creating Redis connection to {}:{}", host, port);

            // Configure Redis connection
            RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
            redisConfig.setHostName(host);
            redisConfig.setPort(Integer.parseInt(port));

            if (username != null && !username.isEmpty()) {
                redisConfig.setUsername(username);
            }

            if (password != null && !password.isEmpty()) {
                redisConfig.setPassword(RedisPassword.of(password));
            }

            // Configure SSL/TLS if needed
            LettuceClientConfiguration.LettuceClientConfigurationBuilder clientConfigBuilder =
                LettuceClientConfiguration.builder();

            if (ssl) {
                clientConfigBuilder.useSsl();
            }

            // Note: Spring Data Redis doesn't support startTls directly through LettuceClientConfigurationBuilder
            // If TLS is needed, useSsl() is typically sufficient for most Redis deployments
            if (tls) {
                LOG.warn("TLS requested but startTls() is not available in Spring Data Redis LettuceClientConfigurationBuilder");
                LOG.warn("Using SSL instead which should cover most TLS use cases");
                clientConfigBuilder.useSsl();
            }

            // Create connection factory
            LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(
                redisConfig, clientConfigBuilder.build());
            connectionFactory.afterPropertiesSet();

            // Create and return the Redis task storage
            return new RedisTaskStorage(connectionFactory);
        } catch (Exception e) {
            LOG.error("Failed to create Redis task storage", e);
            return null;
        }
    }

    /**
     * Gets an environment variable or system property value.
     *
     * @param key the name of the environment variable or system property
     * @return the value or null if not set
     */
    private String getEnv(String key) {
        return getEnv(key, null);
    }

    /**
     * Gets an environment variable or system property value with a default.
     *
     * @param key the name of the environment variable or system property
     * @param defaultValue the default value to return if not set
     * @return the value or the default if not set
     */
    private String getEnv(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null || value.isEmpty()) {
            value = System.getenv(key);
        }
        return value != null && !value.isEmpty() ? value : defaultValue;
    }
}

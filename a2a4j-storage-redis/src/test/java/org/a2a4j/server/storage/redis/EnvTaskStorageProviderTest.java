// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.server.storage.redis;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.a2a4j.server.storage.TaskStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for the EnvTaskStorageProvider.
 */
@ExtendWith(MockitoExtension.class)
class EnvTaskStorageProviderTest {

    private EnvTaskStorageProvider provider;
    private MockedStatic<System> mockedSystem;

    @BeforeEach
    void setUp() {
        provider = new EnvTaskStorageProvider();
        mockedSystem = mockStatic(System.class);
    }

    @AfterEach
    void tearDown() {
        mockedSystem.close();
    }

    @Test
    void testProvideWithRedisConfig() {
        // Mock environment variables for Redis configuration
        mockedSystem.when(() -> System.getenv("A2A_STORAGE_REDIS_HOST")).thenReturn("localhost");
        mockedSystem.when(() -> System.getenv("A2A_STORAGE_REDIS_PORT")).thenReturn("6379");
        mockedSystem.when(() -> System.getenv("A2A_STORAGE_REDIS_PASSWORD")).thenReturn("password");
        mockedSystem.when(() -> System.getenv("A2A_STORAGE_REDIS_SSL")).thenReturn("true");
        
        // Call provide method
        TaskStorage storage = provider.provide();

        // Verify the storage is a RedisTaskStorage
        assertNotNull(storage, "Task storage should not be null");
        assertTrue(storage instanceof RedisTaskStorage, "Task storage should be a RedisTaskStorage");
    }

    @Test
    void testProvideWithoutRedisHost() {
        // Mock environment variables without Redis host configuration
        mockedSystem.when(() -> System.getenv("A2A_STORAGE_REDIS_HOST")).thenReturn(null);

        // Call provide method
        TaskStorage storage = provider.provide();

        // Verify the storage is null
        assertNull(storage, "Task storage should be null when Redis host is not configured");
    }

    @Test
    void testProvideWithEmptyRedisHost() {
        // Mock environment variables with empty Redis host
        mockedSystem.when(() -> System.getenv("A2A_STORAGE_REDIS_HOST")).thenReturn("");

        // Call provide method
        TaskStorage storage = provider.provide();

        // Verify the storage is null
        assertNull(storage, "Task storage should be null when Redis host is empty");
    }

    @Test
    void testProvideWithInvalidPort() {
        // Mock environment variables with invalid port
        mockedSystem.when(() -> System.getenv("A2A_STORAGE_REDIS_HOST")).thenReturn("localhost");
        mockedSystem.when(() -> System.getenv("A2A_STORAGE_REDIS_PORT")).thenReturn("not-a-number");

        // Call provide method
        TaskStorage storage = provider.provide();

        // Verify the storage is null due to exception
        assertNull(storage, "Task storage should be null when port is invalid");
    }
} 
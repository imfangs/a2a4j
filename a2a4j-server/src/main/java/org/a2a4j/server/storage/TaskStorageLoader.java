// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.server.storage;

import org.a2a4j.server.InMemoryTaskStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Utility class for loading TaskStorage implementations.
 */
public class TaskStorageLoader {
    private static final Logger LOG = LoggerFactory.getLogger(TaskStorageLoader.class);

    private TaskStorageLoader() {
        // Private constructor to prevent instantiation
    }

    /**
     * Loads the default TaskStorage implementation.
     * Uses the Java ServiceLoader mechanism to discover available TaskStorageProvider implementations.
     * If no provider is found, returns an InMemoryTaskStorage instance as a fallback.
     *
     * @return A configured TaskStorage implementation
     */
    public static TaskStorage loadTaskStorage() {
        LOG.info("Loading task storage implementation");

        // Use Java's ServiceLoader to find TaskStorageProvider implementations
        ServiceLoader<TaskStorageProvider> loader = ServiceLoader.load(TaskStorageProvider.class);
        Iterator<TaskStorageProvider> providerIterator = loader.iterator();

        // Try each provider
        while (providerIterator.hasNext()) {
            TaskStorageProvider provider = providerIterator.next();
            TaskStorage storage = provider.provide();

            if (storage != null) {
                LOG.info("Using task storage implementation: {}", storage.getClass().getName());
                return storage;
            }
        }

        // If no provider is found, use InMemoryTaskStorage as a fallback
        LOG.info("No task storage provider found, using in-memory storage");
        return new InMemoryTaskStorage();
    }
}

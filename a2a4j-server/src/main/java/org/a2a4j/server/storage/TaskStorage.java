// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.server.storage;

import org.a2a4j.models.Task;
import org.a2a4j.models.notification.PushNotificationConfig;

/**
 * Interface for storing and retrieving tasks and their associated push notification configurations.
 * Implementations of this interface should provide thread-safe operations and handle the persistence
 * of task data according to their specific storage mechanism.
 */
public interface TaskStorage {

    /**
     * Stores a task in the storage.
     * If a task with the same ID already exists, implementations should overwrite it.
     *
     * @param task The task to store
     */
    void store(Task task);

    /**
     * Retrieves a task by its ID from the storage.
     *
     * @param taskId The ID of the task to retrieve
     * @return The task if found, null otherwise
     */
    Task fetch(String taskId);

    /**
     * Stores a push notification configuration for a task.
     * If a configuration for the same task ID already exists, implementations should overwrite it.
     *
     * @param taskId The ID of the task to associate with the configuration
     * @param config The push notification configuration to store
     * @throws IllegalArgumentException if the task ID does not exist in the storage
     */
    void storeNotificationConfig(String taskId, PushNotificationConfig config);

    /**
     * Retrieves a push notification configuration for a task by its ID.
     *
     * @param taskId The ID of the task whose configuration to retrieve
     * @return The push notification configuration if found, null otherwise
     */
    PushNotificationConfig fetchNotificationConfig(String taskId);
}

// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.server;

import org.a2a4j.models.Task;
import org.a2a4j.models.notification.PushNotificationConfig;
import org.a2a4j.server.storage.TaskStorage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the TaskStorage interface.
 * Stores tasks and their associated push notification configurations in memory using concurrent hash maps.
 * This implementation is thread-safe but does not persist data across application restarts.
 */
public class InMemoryTaskStorage implements TaskStorage {

    /** Map of task IDs to their corresponding Task objects */
    private final ConcurrentHashMap<String, Task> tasks = new ConcurrentHashMap<>();

    /** Map of task IDs to their push notification configurations */
    private final ConcurrentHashMap<String, PushNotificationConfig> pushNotificationInfos = new ConcurrentHashMap<>();

    /**
     * Stores a task in the in-memory storage.
     * If a task with the same ID already exists, it will be overwritten.
     *
     * @param task The task to store
     */
    @Override
    public void store(Task task) {
        tasks.put(task.getId(), task);
    }

    /**
     * Retrieves a task by its ID from the in-memory storage.
     *
     * @param taskId The ID of the task to retrieve
     * @return The task if found, null otherwise
     */
    @Override
    public Task fetch(String taskId) {
        return tasks.get(taskId);
    }

    /**
     * Stores a push notification configuration for a task.
     * If a configuration for the same task ID already exists, it will be overwritten.
     *
     * @param taskId The ID of the task to associate with the configuration
     * @param config The push notification configuration to store
     * @throws IllegalArgumentException if the task ID does not exist
     */
    @Override
    public void storeNotificationConfig(String taskId, PushNotificationConfig config) {
        if (fetch(taskId) == null) {
            throw new IllegalArgumentException("Task not found for " + taskId);
        }
        pushNotificationInfos.put(taskId, config);
    }

    /**
     * Retrieves a push notification configuration for a task by its ID.
     *
     * @param taskId The ID of the task whose configuration to retrieve
     * @return The push notification configuration if found, null otherwise
     */
    @Override
    public PushNotificationConfig fetchNotificationConfig(String taskId) {
        return pushNotificationInfos.get(taskId);
    }
}

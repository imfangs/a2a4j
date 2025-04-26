// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.server.storage.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.a2a4j.models.Task;
import org.a2a4j.models.notification.PushNotificationConfig;
import org.a2a4j.server.storage.TaskStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Closeable;
import java.io.IOException;

/**
 * Redis implementation of the TaskStorage interface.
 * Stores tasks and their associated push notification configurations in Redis.
 * This implementation is thread-safe and persists data in Redis.
 */
public class RedisTaskStorage implements TaskStorage, Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(RedisTaskStorage.class);
    private static final String TASK_PREFIX = "task:";
    private static final String NOTIFICATION_PREFIX = "notification:";
    
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    
    /**
     * Creates a new RedisTaskStorage with the specified Redis connection factory.
     *
     * @param connectionFactory the Redis connection factory
     */
    public RedisTaskStorage(RedisConnectionFactory connectionFactory) {
        this(connectionFactory, new ObjectMapper());
    }
    
    /**
     * Creates a new RedisTaskStorage with the specified Redis connection factory and object mapper.
     *
     * @param connectionFactory the Redis connection factory
     * @param objectMapper the object mapper for JSON serialization
     */
    public RedisTaskStorage(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        
        // Configure Redis template
        this.redisTemplate = new RedisTemplate<>();
        this.redisTemplate.setConnectionFactory(connectionFactory);
        this.redisTemplate.setKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setValueSerializer(new StringRedisSerializer());
        this.redisTemplate.afterPropertiesSet();
        
        LOG.info("RedisTaskStorage initialized");
    }

    /**
     * Stores a task in Redis.
     * If a task with the same ID already exists, it will be overwritten.
     *
     * @param task The task to store
     */
    @Override
    public void store(Task task) {
        String key = TASK_PREFIX + task.getId();
        try {
            String taskJson = objectMapper.writeValueAsString(task);
            redisTemplate.opsForValue().set(key, taskJson);
            LOG.debug("Stored task with ID: {}", task.getId());
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize task: {}", task.getId(), e);
            throw new RuntimeException("Failed to serialize task", e);
        }
    }

    /**
     * Retrieves a task by its ID from Redis.
     *
     * @param taskId The ID of the task to retrieve
     * @return The task if found, null otherwise
     */
    @Override
    public Task fetch(String taskId) {
        String key = TASK_PREFIX + taskId;
        String taskJson = redisTemplate.opsForValue().get(key);
        
        if (taskJson == null) {
            LOG.debug("Task not found with ID: {}", taskId);
            return null;
        }
        
        try {
            Task task = objectMapper.readValue(taskJson, Task.class);
            LOG.debug("Retrieved task with ID: {}", taskId);
            return task;
        } catch (JsonProcessingException e) {
            LOG.error("Failed to deserialize task: {}", taskId, e);
            throw new RuntimeException("Failed to deserialize task", e);
        }
    }

    /**
     * Stores a push notification configuration for a task in Redis.
     * If a configuration for the same task ID already exists, it will be overwritten.
     *
     * @param taskId The ID of the task to associate with the configuration
     * @param config The push notification configuration to store
     * @throws IllegalArgumentException if the task ID does not exist in Redis
     */
    @Override
    public void storeNotificationConfig(String taskId, PushNotificationConfig config) {
        // Check if task exists
        Task task = fetch(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found for " + taskId);
        }
        
        String key = NOTIFICATION_PREFIX + taskId;
        try {
            String configJson = objectMapper.writeValueAsString(config);
            redisTemplate.opsForValue().set(key, configJson);
            LOG.debug("Stored notification config for task ID: {}", taskId);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize notification config for task: {}", taskId, e);
            throw new RuntimeException("Failed to serialize notification config", e);
        }
    }

    /**
     * Retrieves a push notification configuration for a task by its ID from Redis.
     *
     * @param taskId The ID of the task whose configuration to retrieve
     * @return The push notification configuration if found, null otherwise
     */
    @Override
    public PushNotificationConfig fetchNotificationConfig(String taskId) {
        String key = NOTIFICATION_PREFIX + taskId;
        String configJson = redisTemplate.opsForValue().get(key);
        
        if (configJson == null) {
            LOG.debug("Notification config not found for task ID: {}", taskId);
            return null;
        }
        
        try {
            PushNotificationConfig config = objectMapper.readValue(configJson, PushNotificationConfig.class);
            LOG.debug("Retrieved notification config for task ID: {}", taskId);
            return config;
        } catch (JsonProcessingException e) {
            LOG.error("Failed to deserialize notification config for task: {}", taskId, e);
            throw new RuntimeException("Failed to deserialize notification config", e);
        }
    }

    /**
     * Closes the Redis connection.
     * This method should be called when the storage is no longer needed.
     */
    @Override
    public void close() throws IOException {
        // RedisTemplate doesn't require explicit closing
        LOG.info("RedisTaskStorage closed");
    }
} 
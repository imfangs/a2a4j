//// SPDX-FileCopyrightText: 2025
////
//// SPDX-License-Identifier: Apache-2.0
//package org.a2a4j.server.storage.redis;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.util.Collections;
//import java.util.List;
//
//import org.a2a4j.models.Message;
//import org.a2a4j.models.Task;
//import org.a2a4j.models.TaskState;
//import org.a2a4j.models.TaskStatus;
//import org.a2a4j.models.notification.PushNotificationConfig;
//import org.a2a4j.models.part.TextPart;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//
///**
// * Integration tests for RedisTaskStorage using TestContainers.
// */
//class RedisTaskStorageTest extends TestBase {
//
//    private RedisTaskStorage storage;
//    private LettuceConnectionFactory connectionFactory;
//
//    @BeforeEach
//    void setUp() {
//        connectionFactory = createRedisConnectionFactory();
//        storage = new RedisTaskStorage(connectionFactory);
//    }
//
//    @AfterEach
//    void tearDown() throws Exception {
//        storage.close();
//        connectionFactory.destroy();
//    }
//
//    @Test
//    void testStoreAndFetchTask() {
//        // Create a test task
//        Task task = new Task(
//            "test-task-1",
//            "test-session",
//            new TaskStatus(TaskState.SUBMITTED),
//            null,
//            null,
//            Collections.emptyMap()
//        );
//
//        // Store the task
//        storage.store(task);
//
//        // Fetch the task
//        Task fetchedTask = storage.fetch("test-task-1");
//
//        // Verify the task was stored and retrieved correctly
//        assertNotNull(fetchedTask, "Fetched task should not be null");
//        assertEquals("test-task-1", fetchedTask.getId(), "Task ID should match");
//        assertEquals("test-session", fetchedTask.getSessionId(), "Session ID should match");
//        assertEquals(TaskState.SUBMITTED, fetchedTask.getStatus().getState(), "Task state should match");
//    }
//
//    @Test
//    void testFetchNonExistentTask() {
//        // Fetch a non-existent task
//        Task task = storage.fetch("non-existent-task");
//
//        // Verify the task is null
//        assertNull(task, "Non-existent task should be null");
//    }
//
//    @Test
//    void testStoreAndFetchTaskWithHistory() {
//        // Create a message for task history
//        TextPart textPart = new TextPart("Hello", Collections.emptyMap());
//        Message message = new Message("user", List.of(textPart), Collections.emptyMap());
//
//        // Create a test task with history
//        Task task = new Task(
//            "test-task-with-history",
//            "test-session",
//            new TaskStatus(TaskState.SUBMITTED),
//            List.of(message),
//            null,
//            Collections.emptyMap()
//        );
//
//        // Store the task
//        storage.store(task);
//
//        // Fetch the task
//        Task fetchedTask = storage.fetch("test-task-with-history");
//
//        // Verify the task was stored and retrieved correctly including history
//        assertNotNull(fetchedTask, "Fetched task should not be null");
//        assertNotNull(fetchedTask.getHistory(), "Task history should not be null");
//        assertFalse(fetchedTask.getHistory().isEmpty(), "Task history should not be empty");
//        assertEquals("user", fetchedTask.getHistory().get(0).getRole(), "Message role should match");
//        assertEquals(1, fetchedTask.getHistory().get(0).getParts().size(), "Message should have one part");
//        assertEquals("Hello", ((TextPart) fetchedTask.getHistory().get(0).getParts().get(0)).getText(),
//                "Message text should match");
//    }
//
//    @Test
//    void testStoreAndFetchNotificationConfig() {
//        // Create a test task
//        Task task = new Task(
//            "test-task-2",
//            "test-session",
//            new TaskStatus(TaskState.SUBMITTED),
//            null,
//            null,
//            Collections.emptyMap()
//        );
//
//        // Store the task
//        storage.store(task);
//
//        // Create a test notification config
//        PushNotificationConfig config = new PushNotificationConfig("https://example.com/webhook", "test-token");
//
//        // Store the notification config
//        storage.storeNotificationConfig("test-task-2", config);
//
//        // Fetch the notification config
//        PushNotificationConfig fetchedConfig = storage.fetchNotificationConfig("test-task-2");
//
//        // Verify the config was stored and retrieved correctly
//        assertNotNull(fetchedConfig, "Fetched config should not be null");
//        assertEquals("https://example.com/webhook", fetchedConfig.getUrl(), "URL should match");
//        assertEquals("test-token", fetchedConfig.getAuthToken(), "Auth token should match");
//    }
//
//    @Test
//    void testFetchNonExistentNotificationConfig() {
//        // Create a test task
//        Task task = new Task(
//            "test-task-3",
//            "test-session",
//            new TaskStatus(TaskState.SUBMITTED),
//            null,
//            null,
//            Collections.emptyMap()
//        );
//
//        // Store the task
//        storage.store(task);
//
//        // Fetch a non-existent notification config
//        PushNotificationConfig config = storage.fetchNotificationConfig("test-task-3");
//
//        // Verify the config is null
//        assertNull(config, "Non-existent notification config should be null");
//    }
//
//    @Test
//    void testStoreNotificationConfigWithNonExistentTask() {
//        // Create a test notification config
//        PushNotificationConfig config = new PushNotificationConfig("https://example.com/webhook", "test-token");
//
//        // Attempt to store the notification config for a non-existent task
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            storage.storeNotificationConfig("non-existent-task", config);
//        });
//
//        // Verify the exception message
//        assertTrue(exception.getMessage().contains("Task not found"),
//                "Exception message should indicate that task was not found");
//    }
//}

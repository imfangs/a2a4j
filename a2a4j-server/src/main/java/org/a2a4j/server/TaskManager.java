// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.server;

import org.a2a4j.models.jsonrpc.*;
import reactor.core.publisher.Flux;

/**
 * Interface for managing tasks in the A2A system.
 *
 * This interface defines methods for creating, retrieving, updating, and subscribing to tasks.
 * It also provides functionality for managing push notifications related to tasks.
 */
public interface TaskManager {

    /**
     * Retrieves a task by its ID.
     *
     * @param request The request containing the task ID and query parameters
     * @return A response containing the requested task or an error if the task is not found
     */
    GetTaskResponse onGetTask(GetTaskRequest request);

    /**
     * Attempts to cancel a task.
     *
     * @param request The request containing the task ID to cancel
     * @return A response indicating success or failure of the cancellation
     */
    CancelTaskResponse onCancelTask(CancelTaskRequest request);

    /**
     * Creates or updates a task.
     *
     * @param request The request containing the task data to send
     * @return A response containing the created or updated task
     */
    SendTaskResponse onSendTask(SendTaskRequest request);

    /**
     * Subscribes to streaming updates for a task.
     *
     * @param request The request containing the task data and subscription parameters
     * @return A flux of streaming responses with task updates
     */
    Flux<SendTaskStreamingResponse> onSendTaskSubscribe(SendTaskStreamingRequest request);

    /**
     * Sets push notification configuration for a task.
     *
     * @param request The request containing the task ID and push notification configuration
     * @return A response indicating success or failure of the operation
     */
    SetTaskPushNotificationResponse onSetTaskPushNotification(SetTaskPushNotificationRequest request);

    /**
     * Handles a request to get the push notification configuration for a task.
     *
     * @param request The request to get the push notification configuration
     * @return The response containing the push notification configuration or an error
     */
    GetTaskPushNotificationResponse onGetTaskPushNotification(GetTaskPushNotificationRequest request);

    /**
     * Handles a request to resubscribe to a task.
     *
     * @param request The request to resubscribe to a task
     * @return A flux of streaming responses with task updates
     */
    Flux<SendTaskStreamingResponse> onResubscribeToTask(TaskResubscriptionRequest request);
}

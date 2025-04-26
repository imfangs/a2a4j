package org.a2a4j.client;

import org.a2a4j.models.AgentCard;
import org.a2a4j.models.Message;
import org.a2a4j.models.notification.PushNotificationConfig;
import org.a2a4j.models.jsonrpc.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Closeable;

/**
 * A2AClient defines the interface for Agent-to-Agent communication client based on the A2A protocol.
 *
 * This client provides methods for interacting with an A2A server, including retrieving
 * the agent's metadata (agent card), sending and retrieving tasks, canceling tasks,
 * and managing push notification configurations.
 */
public interface A2AClient extends Closeable {

    /**
     * Retrieves the agent card from the server.
     *
     * @return A Mono emitting the agent card containing metadata about the agent.
     */
    Mono<AgentCard> getAgentCard();

    /**
     * Retrieves a task by its ID.
     *
     * @param taskId The ID of the task to retrieve.
     * @param historyLength The maximum number of history entries to include in the response.
     * @param requestId A unique identifier for this request.
     * @return A Mono emitting the response containing the requested task or an error if the task is not found.
     */
    Mono<GetTaskResponse> getTask(String taskId, Integer historyLength, String requestId);

    /**
     * Retrieves a task by its ID with default history length and generated request ID.
     *
     * @param taskId The ID of the task to retrieve.
     * @return A Mono emitting the response containing the requested task or an error if the task is not found.
     */
    Mono<GetTaskResponse> getTask(String taskId);

    /**
     * Creates or updates a task.
     *
     * @param message The message to include in the task.
     * @param taskId The ID of the task to create or update.
     * @param sessionId The session ID for this task.
     * @param historyLength The maximum number of history entries to include in the response.
     * @param requestId A unique identifier for this request.
     * @return A Mono emitting the response containing the created or updated task.
     */
    Mono<SendTaskResponse> sendTask(Message message, String taskId, String sessionId, Integer historyLength, String requestId);

    /**
     * Creates or updates a task with generated task ID, session ID, and request ID.
     *
     * @param message The message to include in the task.
     * @return A Mono emitting the response containing the created or updated task.
     */
    Mono<SendTaskResponse> sendTask(Message message);

    /**
     * Subscribes to streaming updates for a task.
     *
     * @param taskId The ID of the task to subscribe to.
     * @param sessionId The session ID for this task.
     * @param message The message to include in the task.
     * @param historyLength The maximum number of history entries to include in the response.
     * @param requestId A unique identifier for this request.
     * @return A Flux of streaming responses with task updates.
     */
    Flux<SendTaskStreamingResponse> sendTaskStreaming(String taskId, String sessionId, Message message, Integer historyLength, String requestId);

    /**
     * Subscribes to streaming updates for a task with generated task ID, session ID, and request ID.
     *
     * @param message The message to include in the task.
     * @return A Flux of streaming responses with task updates.
     */
    Flux<SendTaskStreamingResponse> sendTaskStreaming(Message message);

    /**
     * Attempts to cancel a task.
     *
     * @param taskId The ID of the task to cancel.
     * @param requestId A unique identifier for this request.
     * @return A Mono emitting the response indicating success or failure of the cancellation.
     */
    Mono<CancelTaskResponse> cancelTask(String taskId, String requestId);

    /**
     * Attempts to cancel a task with a generated request ID.
     *
     * @param taskId The ID of the task to cancel.
     * @return A Mono emitting the response indicating success or failure of the cancellation.
     */
    Mono<CancelTaskResponse> cancelTask(String taskId);

    /**
     * Sets push notification configuration for a task.
     *
     * @param taskId The ID of the task to configure.
     * @param config The push notification configuration.
     * @param requestId A unique identifier for this request.
     * @return A Mono emitting the response indicating success or failure of the operation.
     */
    Mono<SetTaskPushNotificationResponse> setTaskPushNotification(String taskId, PushNotificationConfig config, String requestId);

    /**
     * Sets push notification configuration for a task with a generated request ID.
     *
     * @param taskId The ID of the task to configure.
     * @param config The push notification configuration.
     * @return A Mono emitting the response indicating success or failure of the operation.
     */
    Mono<SetTaskPushNotificationResponse> setTaskPushNotification(String taskId, PushNotificationConfig config);

    /**
     * Retrieves the push notification configuration for a task.
     *
     * @param taskId The ID of the task.
     * @param requestId A unique identifier for this request.
     * @return A Mono emitting the response containing the push notification configuration or an error if not found.
     */
    Mono<GetTaskPushNotificationResponse> getTaskPushNotification(String taskId, String requestId);

    /**
     * Retrieves the push notification configuration for a task with a generated request ID.
     *
     * @param taskId The ID of the task.
     * @return A Mono emitting the response containing the push notification configuration or an error if not found.
     */
    Mono<GetTaskPushNotificationResponse> getTaskPushNotification(String taskId);

    /**
     * Resubscribes to a task to receive streaming updates.
     *
     * @param taskId The ID of the task to resubscribe to.
     * @param requestId A unique identifier for this request.
     * @return A Flux of streaming responses with task updates.
     */
    Flux<SendTaskStreamingResponse> resubscribeTask(String taskId, String requestId);

    /**
     * Resubscribes to a task to receive streaming updates with a generated request ID.
     *
     * @param taskId The ID of the task to resubscribe to.
     * @return A Flux of streaming responses with task updates.
     */
    Flux<SendTaskStreamingResponse> resubscribeTask(String taskId);
} 
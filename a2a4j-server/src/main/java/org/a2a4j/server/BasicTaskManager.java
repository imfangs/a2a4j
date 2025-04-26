// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.server;

import org.a2a4j.models.*;
import org.a2a4j.models.jsonrpc.*;
import org.a2a4j.models.jsonrpc.errors.*;
import org.a2a4j.models.notification.*;
import org.a2a4j.models.params.*;
import org.a2a4j.models.streaming.*;
import org.a2a4j.server.notifications.BasicNotificationPublisher;
import org.a2a4j.server.notifications.NotificationPublisher;
import org.a2a4j.server.storage.TaskStorage;
import org.a2a4j.server.storage.TaskStorageLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the TaskManager interface.
 *
 * This class provides an implementation of the TaskManager interface that stores
 * tasks and their associated data in memory. It supports all operations defined
 * in the TaskManager interface, including task creation, retrieval, cancellation,
 * and subscription to task updates.
 */
public class BasicTaskManager implements TaskManager {

    private static final Logger log = LoggerFactory.getLogger(BasicTaskManager.class);

    private final TaskHandler taskHandler;
    private final TaskStorage taskStorage;
    private final NotificationPublisher notificationPublisher;

    /** Map of task IDs to lists of Sinks for server-sent events */
    private final Map<String, List<Sinks.Many<Object>>> taskSseSubscribers = new ConcurrentHashMap<>();

    /**
     * Constructs a new BasicTaskManager with the specified dependencies.
     *
     * @param taskHandler The task handler to process tasks
     * @param taskStorage The storage implementation to use (defaults to loaded via TaskStorageLoader)
     * @param notificationPublisher The notification publisher (defaults to BasicNotificationPublisher)
     */
    public BasicTaskManager(
            TaskHandler taskHandler,
            TaskStorage taskStorage,
            NotificationPublisher notificationPublisher
    ) {
        this.taskHandler = taskHandler;
        this.taskStorage = taskStorage;
        this.notificationPublisher = notificationPublisher;
    }

    /**
     * Constructs a new BasicTaskManager with the specified task handler.
     * Uses default implementations for other dependencies.
     *
     * @param taskHandler The task handler to process tasks
     */
    public BasicTaskManager(TaskHandler taskHandler) {
        this(
            taskHandler,
            TaskStorageLoader.loadTaskStorage(),
            new BasicNotificationPublisher()
        );
    }

    /**
     * {@inheritDoc}
     *
     * This implementation retrieves a task from the in-memory store by its ID.
     * If the task is not found, it returns an error response.
     */
    @Override
    public GetTaskResponse onGetTask(GetTaskRequest request) {
        log.info("Getting task {}", request.getParams().getId());
        TaskQueryParams<?> taskQueryParams = request.getParams();
        Task task = taskStorage.fetch(taskQueryParams.getId());

        if (task == null) {
            return GetTaskResponse.builder()
                .id(request.getId())
                .error(new TaskNotFoundError())
                .build();
        }

        Task taskResult = appendTaskHistory(task, taskQueryParams.getHistoryLength());
        return GetTaskResponse.builder()
            .id(request.getId())
            .result(taskResult)
            .build();
    }

    /**
     * {@inheritDoc}
     *
     * This implementation attempts to cancel a task.
     * Currently, tasks are not cancelable, so it always returns a TaskNotCancelableError.
     * If the task is not found, it returns a TaskNotFoundError.
     */
    @Override
    public CancelTaskResponse onCancelTask(CancelTaskRequest request) {
        log.info("Cancelling task {}", request.getParams().getId());
        TaskIdParams<?> taskIdParams = request.getParams();

        if (taskStorage.fetch(taskIdParams.getId()) == null) {
            return CancelTaskResponse.builder()
                .id(request.getId())
                .error(new TaskNotFoundError())
                .build();
        }

        // TODO: Implement actual cancellation logic if needed
        return CancelTaskResponse.builder()
            .id(request.getId())
            .error(new TaskNotCancelableError())
            .build();
    }

    /**
     * {@inheritDoc}
     *
     * This implementation creates or updates a task in the in-memory store.
     * If a push notification configuration is provided, it is stored as well.
     * If an error occurs during the operation, an InternalError is returned.
     */
    @Override
    public SendTaskResponse onSendTask(SendTaskRequest request) {
        log.info("Sending task {}", request.getParams().getId());
        TaskSendParams<?> taskSendParams = request.getParams();

        try {
            // Create or update the task
            Task task = upsertTask(taskSendParams);

            // Set push notification if provided
            if (taskSendParams.getPushNotification() != null) {
                setPushNotificationInfo(taskSendParams.getId(), taskSendParams.getPushNotification());
            }

            // Send Task to Agent
            Task handledTask = taskHandler.handle(task);
            taskStorage.store(handledTask);

            // Send push notification if configured
            PushNotificationConfig notificationConfig = taskStorage.fetchNotificationConfig(task.getId());
            if (notificationConfig != null && notificationPublisher != null) {
                notificationPublisher.publish(handledTask, notificationConfig);
            }

            // Return the task with appropriate history length
            Task taskResult = appendTaskHistory(handledTask, taskSendParams.getHistoryLength());
            return SendTaskResponse.builder()
                .id(request.getId())
                .result(taskResult)
                .build();
        } catch (Exception e) {
            log.error("Error while sending task: {}", e.getMessage(), e);
            return SendTaskResponse.builder()
                .id(request.getId())
                .error(new org.a2a4j.models.jsonrpc.errors.InternalError())
                .build();
        }
    }

    /**
     * {@inheritDoc}
     *
     * This implementation creates or updates a task and sets up
     * a subscription for streaming updates. It returns a flux of streaming responses
     * with task updates.
     */
    @Override
    public Flux<SendTaskStreamingResponse> onSendTaskSubscribe(SendTaskStreamingRequest request) {
        log.info("Sending task with subscription {}", request.getParams().getId());
        TaskSendParams<?> taskSendParams = request.getParams();

        try {
            // Create or update the task
            Task task = upsertTask(taskSendParams);

            // Set push notification if provided
            if (taskSendParams.getPushNotification() != null) {
                setPushNotificationInfo(taskSendParams.getId(), taskSendParams.getPushNotification());
            }

            // Set up SSE consumer
            Sinks.Many<Object> sseEventSink = setupSseConsumer(taskSendParams.getId());

            // Send initial task status update
            TaskStatusUpdateEvent initialStatusEvent = TaskStatusUpdateEvent.builder()
                .id(task.getId())
                .status(task.getStatus())
                .finalFlag(false)
                .build();
            sendSseEvent(task.getId(), initialStatusEvent);

            // Send Task to Agent
            Task handledTask = taskHandler.handle(task);
            taskStorage.store(handledTask);

            // Send push notification if configured
            PushNotificationConfig notificationConfig = taskStorage.fetchNotificationConfig(task.getId());
            if (notificationConfig != null && notificationPublisher != null) {
                notificationPublisher.publish(handledTask, notificationConfig);
            }

            // Send task artifacts updates if any
            if (handledTask.getArtifacts() != null) {
                for (Artifact artifact : handledTask.getArtifacts()) {
                    sendSseEvent(task.getId(), TaskArtifactUpdateEvent.builder()
                        .id(task.getId())
                        .artifact(artifact)
                        .build());
                }
            }

            // Send final task status update
            TaskStatusUpdateEvent finalStatusEvent = TaskStatusUpdateEvent.builder()
                .id(task.getId())
                .status(handledTask.getStatus())
                .finalFlag(true)
                .build();
            sendSseEvent(task.getId(), finalStatusEvent);

            // Return the flux of events
            String requestId = request.getId();
            String taskId = task.getId();

            return sseEventSink.asFlux()
                .map(event -> {
                    if (event instanceof TaskStatusUpdateEvent statusEvent) {
                        return SendTaskStreamingResponse.builder()
                            .id(requestId)
                            .result(statusEvent)
                            .build();
                    } else if (event instanceof TaskArtifactUpdateEvent artifactEvent) {
                        return SendTaskStreamingResponse.builder()
                            .id(requestId)
                            .result(artifactEvent)
                            .build();
                    } else {
                        log.warn("Unknown event type: {}", event.getClass().getName());
                        return SendTaskStreamingResponse.builder()
                            .id(requestId)
                            .error(new org.a2a4j.models.jsonrpc.errors.InternalError())
                            .build();
                    }
                })
                .doFinally(signalType -> {
                    // Remove the subscriber when the flux completes or errors
                    removeTaskSubscriber(taskId, sseEventSink);
                });

        } catch (Exception e) {
            log.error("Error while setting up task subscription: {}", e.getMessage(), e);
            return Flux.just(SendTaskStreamingResponse.builder()
                .id(request.getId())
                .error(new org.a2a4j.models.jsonrpc.errors.InternalError())
                .build());
        }
    }

    /**
     * {@inheritDoc}
     *
     * This implementation sets the push notification configuration for a task.
     * If the task is not found or an error occurs, it returns an error response.
     */
    @Override
    public SetTaskPushNotificationResponse onSetTaskPushNotification(SetTaskPushNotificationRequest request) {
        log.info("Setting task push notification {}", request.getParams().getId());
        // The params object IS the TaskPushNotificationConfig
        TaskPushNotificationConfig taskNotificationParams = request.getParams();

        try {
            // Extract the nested PushNotificationConfig
            PushNotificationConfig config = taskNotificationParams.getPushNotificationConfig();
            if (config == null) {
                 // Handle case where nested config is null, maybe return error or default?
                 // For now, let setPushNotificationInfo handle null config if necessary
                 log.warn("Received SetTaskPushNotificationRequest for task {} with null pushNotificationConfig.", taskNotificationParams.getId());
                 // Optionally return an error here if config is required by the handler
                 // return new SetTaskPushNotificationResponse(... InvalidParamsError ...);
            }

            setPushNotificationInfo(taskNotificationParams.getId(), config);
            // Return the original TaskPushNotificationConfig as the result, as per schema
            return SetTaskPushNotificationResponse.builder()
                .id(request.getId())
                .result(taskNotificationParams)
                .build();
        } catch (IllegalArgumentException e) {
            log.error("Task not found: {}", e.getMessage());
            return SetTaskPushNotificationResponse.builder()
                .id(request.getId())
                .error(new TaskNotFoundError())
                .build();
        } catch (Exception e) {
            log.error("Error setting push notification: {}", e.getMessage(), e);
            return SetTaskPushNotificationResponse.builder()
                .id(request.getId())
                .error(new org.a2a4j.models.jsonrpc.errors.InternalError())
                .build();
        }
    }

    /**
     * {@inheritDoc}
     *
     * This implementation retrieves the push notification configuration for a task.
     * If the task is not found, the configuration is not set, or an error occurs, it returns an error response.
     */
    @Override
    public GetTaskPushNotificationResponse onGetTaskPushNotification(GetTaskPushNotificationRequest request) {
        log.info("Getting task push notification {}", request.getParams().getId());
        TaskIdParams<?> taskParams = request.getParams();
        String taskId = taskParams.getId();

        try {
            PushNotificationConfig notificationConfig = taskStorage.fetchNotificationConfig(taskId);
            if (notificationConfig != null) {
                // Construct the TaskPushNotificationConfig result object
                TaskPushNotificationConfig result = TaskPushNotificationConfig.builder()
                    .id(taskId)
                    .pushNotificationConfig(notificationConfig)
                    .build();

                return GetTaskPushNotificationResponse.builder()
                    .id(request.getId())
                    .result(result)
                    .build();
            } else {
                // If no config found, return null result (as per schema: result is nullable)
                return GetTaskPushNotificationResponse.builder()
                    .id(request.getId())
                    .result(null)
                    .build();
            }
        } catch (Exception e) {
            log.error("Error while getting push notification info: {}", e.getMessage(), e);
            return GetTaskPushNotificationResponse.builder()
                .id(request.getId())
                .error(new org.a2a4j.models.jsonrpc.errors.InternalError())
                .build();
        }
    }

    /**
     * {@inheritDoc}
     *
     * This implementation resubscribes to a task in the in-memory store and sets up
     * a subscription for streaming updates. It returns a flux of streaming responses
     * with task updates.
     */
    @Override
    public Flux<SendTaskStreamingResponse> onResubscribeToTask(TaskResubscriptionRequest request) {
        log.info("Resubscribing to task {}", request.getParams().getId());
        TaskQueryParams<?> taskQueryParams = request.getParams();
        String taskId = taskQueryParams.getId();

        try {
            Task task = taskStorage.fetch(taskId);
            if (task == null) {
                return Flux.just(SendTaskStreamingResponse.builder()
                    .id(request.getId())
                    .error(new TaskNotFoundError())
                    .build());
            }

            // Set up SSE consumer with resubscribe flag
            Sinks.Many<Object> sseEventSink = setupSseConsumer(taskId, true);

            // Send current task status update
            TaskStatusUpdateEvent statusEvent = TaskStatusUpdateEvent.builder()
                .id(task.getId())
                .status(task.getStatus())
                .finalFlag(false) // Initial status is not final
                .build();
            sendSseEvent(task.getId(), statusEvent);

            // Return the flux of events
            String requestId = request.getId();

            return sseEventSink.asFlux()
                .map(event -> {
                    if (event instanceof TaskStatusUpdateEvent statusUpdate) {
                        return SendTaskStreamingResponse.builder()
                            .id(requestId)
                            .result(statusUpdate)
                            .build();
                    } else if (event instanceof TaskArtifactUpdateEvent artifactEvent) {
                        return SendTaskStreamingResponse.builder()
                            .id(requestId)
                            .result(artifactEvent)
                            .build();
                    } else {
                        log.warn("Unknown event type: {}", event.getClass().getName());
                        return SendTaskStreamingResponse.builder()
                            .id(requestId)
                            .error(new org.a2a4j.models.jsonrpc.errors.InternalError())
                            .build();
                    }
                })
                .doFinally(signalType -> {
                    // Remove the subscriber when the flux completes or errors
                    removeTaskSubscriber(taskId, sseEventSink);
                });
        } catch (Exception e) {
            log.error("Error while resubscribing to task: {}", e.getMessage(), e);
            return Flux.just(SendTaskStreamingResponse.builder()
                .id(request.getId())
                .error(new org.a2a4j.models.jsonrpc.errors.InternalError())
                .build());
        }
    }

    /**
     * Creates or updates a task based on the provided parameters.
     *
     * @param params The parameters containing task data
     * @return The created or updated task
     */
    private Task upsertTask(TaskSendParams<?> params) {
        Task existingTask = taskStorage.fetch(params.getId());

        List<Message> history = new ArrayList<>();
        if (existingTask != null && existingTask.getHistory() != null) {
            history.addAll(existingTask.getHistory());
        }

        if (params.getMessage() != null) {
            history.add(params.getMessage());
        }

        // Determine the status. If new task, set to submitted/working? Schema doesn't specify initial status.
        // Let's default to SUBMITTED for a new task, or keep existing status.
        TaskStatus status = (existingTask != null && existingTask.getStatus() != null)
            ? existingTask.getStatus()
            : TaskStatus.builder().state(TaskState.SUBMITTED).build();

        // Use Task builder
        Task.TaskBuilder taskBuilder = Task.builder()
            .id(params.getId())
            .sessionId(params.getSessionId())
            .status(status)
            .history(history);

        if (params.getMetadata() != null) {
             // We need to cast Map<String, ?> to Map<String, Object> for Task builder
            taskBuilder.metadata((Map<String, Object>) params.getMetadata());
        }
        if (existingTask != null && existingTask.getArtifacts() != null) {
             taskBuilder.artifacts(existingTask.getArtifacts()); // Keep existing artifacts if updating
        }

        Task newTask = taskBuilder.build();
        taskStorage.store(newTask);
        return newTask;
    }

    /**
     * Creates a limited view of a task by truncating its history based on the requested length.
     *
     * @param task The original task
     * @param historyLength The maximum number of history entries to include, or null for all
     * @return A new task with potentially truncated history
     */
    private Task appendTaskHistory(Task task, Integer historyLength) {
        if (historyLength == null || task.getHistory() == null || task.getHistory().size() <= historyLength) {
            return task;
        }

        List<Message> limitedHistory = task.getHistory().subList(
            Math.max(0, task.getHistory().size() - historyLength),
            task.getHistory().size()
        );

        // Use Task builder to create a copy with limited history
        return Task.builder()
            .id(task.getId())
            .sessionId(task.getSessionId())
            .status(task.getStatus())
            .history(limitedHistory)
            .artifacts(task.getArtifacts())
            .metadata(task.getMetadata())
            .build();
    }

    /**
     * Sets the push notification configuration for a task.
     *
     * @param taskId The ID of the task
     * @param notificationConfig The push notification configuration to set (can be null to remove)
     * @throws IllegalArgumentException if the task is not found (optional, depends on storage impl)
     */
    private void setPushNotificationInfo(String taskId, PushNotificationConfig notificationConfig) {
        // Allow storing null to remove config
        taskStorage.storeNotificationConfig(taskId, notificationConfig);
    }

    /**
     * Sets up a new subscriber for task events.
     *
     * @param taskId The ID of the task to subscribe to
     * @param isResubscribe Whether this is a resubscription to an existing task
     * @return A sink for receiving task update events
     * @throws IllegalArgumentException if resubscribing to a non-existent task
     */
    private Sinks.Many<Object> setupSseConsumer(String taskId, boolean isResubscribe) {
        Task existingTask = taskStorage.fetch(taskId); // Check if task exists
        if (existingTask == null && isResubscribe) {
             log.warn("Attempted to resubscribe to non-existent task: {}", taskId);
             throw new TaskNotFoundException(taskId); // Use a more specific exception
        }
        // If not resubscribing, the task might be created later, so don't check here.

        Sinks.Many<Object> sink = Sinks.many().multicast().onBackpressureBuffer();

        List<Sinks.Many<Object>> subscribers = taskSseSubscribers.computeIfAbsent(
            taskId,
            k -> Collections.synchronizedList(new ArrayList<>())
        );

        subscribers.add(sink);
        log.info("Added subscriber for task {}", taskId);
        return sink;
    }

    /**
     * Sets up a new subscriber for task events.
     *
     * @param taskId The ID of the task to subscribe to
     * @return A sink for receiving task update events
     */
    private Sinks.Many<Object> setupSseConsumer(String taskId) {
        return setupSseConsumer(taskId, false);
    }

    /**
     * Removes a subscriber from the list of subscribers for a task.
     *
     * @param taskId The ID of the task
     * @param sink The sink to remove
     */
    private void removeTaskSubscriber(String taskId, Sinks.Many<Object> sink) {
        List<Sinks.Many<Object>> subscribers = taskSseSubscribers.get(taskId);
        if (subscribers != null) {
            boolean removed = subscribers.remove(sink);
            if(removed) {
                log.info("Removed subscriber for task {}", taskId);
            } else {
                log.warn("Attempted to remove non-existent subscriber for task {}", taskId);
            }
            if (subscribers.isEmpty()) {
                boolean listRemoved = taskSseSubscribers.remove(taskId, subscribers);
                if(listRemoved) {
                    log.info("Removed subscriber list for task {} as it became empty", taskId);
                }
            }
        }
    }

    /**
     * Sends an event to all subscribers of a task.
     *
     * @param taskId The ID of the task
     * @param event The event to send
     */
    private void sendSseEvent(String taskId, Object event) {
        List<Sinks.Many<Object>> subscribers = taskSseSubscribers.get(taskId);
        if (subscribers != null && !subscribers.isEmpty()) {
            log.debug("Sending event {} to {} subscribers for task {}", event.getClass().getSimpleName(), subscribers.size(), taskId);
            // Create a copy to avoid ConcurrentModificationException if list is modified during iteration
            List<Sinks.Many<Object>> subscribersCopy = new ArrayList<>(subscribers);
            for (Sinks.Many<Object> sink : subscribersCopy) {
                Sinks.EmitResult result = sink.tryEmitNext(event);
                if (result.isFailure()) {
                    log.warn("Failed to send event to subscriber for task {}: {}", taskId, result);
                    // Optionally remove failed sink
                    // removeTaskSubscriber(taskId, sink);
                }
            }
        } else {
             log.debug("No subscribers found for task {} when trying to send event {}", taskId, event.getClass().getSimpleName());
        }
    }
}

// Custom exception for TaskNotFound during resubscribe
class TaskNotFoundException extends IllegalArgumentException {
    public TaskNotFoundException(String taskId) {
        super("Task not found for resubscription: " + taskId);
    }
}

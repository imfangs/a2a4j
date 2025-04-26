package org.a2a4j.spring;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.a2a4j.models.AgentCard;

import org.a2a4j.models.jsonrpc.*;
import org.a2a4j.models.jsonrpc.errors.*;
import org.a2a4j.server.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * REST controller that handles A2A protocol requests.
 *
 * This controller processes JSON-RPC requests for task management, including task creation,
 * retrieval, cancellation, and notification management. It also provides an endpoint for
 * retrieving the agent's metadata (agent card).
 */
@RestController
public class A2AController {

    private static final Logger log = LoggerFactory.getLogger(A2AController.class);
    private final ObjectMapper objectMapper;
    private final TaskManager taskManager;
    private final AgentCard agentCard;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    /**
     * Creates a new A2AController instance.
     *
     * @param objectMapper JSON mapper
     * @param taskManager Task manager
     * @param agentCard Agent card
     */
    public A2AController(ObjectMapper objectMapper, TaskManager taskManager, AgentCard agentCard) {
        this.objectMapper = objectMapper;
        this.taskManager = taskManager;
        this.agentCard = agentCard;
    }

    /**
     * Handles POST requests to the main endpoint.
     * This endpoint processes various JSON-RPC requests and returns the appropriate response.
     *
     * @param endpoint The endpoint path
     * @param jsonRpcRequestBody The JSON-RPC request body
     * @return The JSON-RPC response
     */
    @PostMapping("${a2a.server.endpoint:}")
    public ResponseEntity<String> handleJsonRpcRequest(
            @PathVariable(required = false) String endpoint,
            @RequestBody String jsonRpcRequestBody) {

        try {
            // Parse the JSON-RPC request
            JsonRpcRequest jsonRpcRequest = parseJsonRpcRequest(jsonRpcRequestBody);

            // Process the request based on its type
            Object result;
            if (jsonRpcRequest instanceof GetTaskRequest) {
                result = taskManager.onGetTask((GetTaskRequest) jsonRpcRequest);
            } else if (jsonRpcRequest instanceof SendTaskRequest) {
                result = taskManager.onSendTask((SendTaskRequest) jsonRpcRequest);
            } else if (jsonRpcRequest instanceof SendTaskStreamingRequest) {
                // For SendTaskStreamingRequest, create a non-streaming response
                SendTaskStreamingRequest streamingRequest = (SendTaskStreamingRequest) jsonRpcRequest;
                // Convert to regular SendTaskRequest
                SendTaskRequest sendTaskRequest = SendTaskRequest.builder()
                    .id(streamingRequest.getId())
                    .params(streamingRequest.getParams())
                    .build();
                // Process as a regular send-task request
                result = taskManager.onSendTask(sendTaskRequest);
            } else if (jsonRpcRequest instanceof CancelTaskRequest) {
                result = taskManager.onCancelTask((CancelTaskRequest) jsonRpcRequest);
            } else if (jsonRpcRequest instanceof SetTaskPushNotificationRequest) {
                result = taskManager.onSetTaskPushNotification((SetTaskPushNotificationRequest) jsonRpcRequest);
            } else if (jsonRpcRequest instanceof GetTaskPushNotificationRequest) {
                result = taskManager.onGetTaskPushNotification((GetTaskPushNotificationRequest) jsonRpcRequest);
            } else if (jsonRpcRequest instanceof UnknownMethodRequest) {
                result = ErrorResponse.builder()
                        .id(((UnknownMethodRequest) jsonRpcRequest).getId())
                        .error(new MethodNotFoundError())
                        .build();
            } else {
                throw new IllegalArgumentException("Unexpected request type: " + jsonRpcRequest.getClass().getName());
            }

            // Convert the result to JSON and return it
            String jsonResponse = objectMapper.writeValueAsString(result);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(jsonResponse);

        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * Handles streaming requests.
     * This endpoint establishes an SSE connection for long-running tasks.
     *
     * @param jsonRpcRequestBody The JSON-RPC request body
     * @return An SSE emitter that will emit events as the task progresses
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter handleStreamingRequest(@RequestBody String jsonRpcRequestBody) {
        SseEmitter emitter = new SseEmitter(0L); // No timeout

        try {
            // Parse the JSON-RPC request
            JsonRpcRequest jsonRpcRequest = parseJsonRpcRequest(jsonRpcRequestBody);

            // Process streaming requests
            if (jsonRpcRequest instanceof SendTaskStreamingRequest) {
                handleSendTaskStreaming((SendTaskStreamingRequest) jsonRpcRequest, emitter);
            } else if (jsonRpcRequest instanceof TaskResubscriptionRequest) {
                handleTaskResubscription((TaskResubscriptionRequest) jsonRpcRequest, emitter);
            } else {
                throw new IllegalArgumentException("Unexpected streaming request type: " + jsonRpcRequest.getClass().getName());
            }
        } catch (Exception e) {
            handleStreamingException(emitter, e);
        }

        return emitter;
    }

    /**
     * Returns the agent card.
     *
     * @return The agent card
     */
    @GetMapping("/.well-known/agent.json")
    public ResponseEntity<AgentCard> getAgentCard() {
        return ResponseEntity.ok(agentCard);
    }

    /**
     * Parses a JSON-RPC request from its string representation.
     *
     * @param jsonRpcRequestBody The JSON-RPC request body
     * @return The parsed JSON-RPC request
     * @throws Exception If parsing fails
     */
    private JsonRpcRequest parseJsonRpcRequest(String jsonRpcRequestBody) throws Exception {
        return objectMapper.readValue(jsonRpcRequestBody, JsonRpcRequest.class);
    }

    /**
     * Handles streaming task updates for send-task-streaming requests.
     *
     * @param request The send-task-streaming request
     * @param emitter The SSE emitter
     */
    private void handleSendTaskStreaming(SendTaskStreamingRequest request, SseEmitter emitter) {
        executor.execute(() -> {
            try {
                Flux<SendTaskStreamingResponse> responseFlux = taskManager.onSendTaskSubscribe(request);
                responseFlux.subscribe(update -> {
                    try {
                        String json = objectMapper.writeValueAsString(update);
                        emitter.send(SseEmitter.event().data(json, MediaType.APPLICATION_JSON));
                    } catch (Exception e) {
                        log.error("Error sending SSE event", e);
                        emitter.completeWithError(e);
                    }
                },
                error -> {
                    log.error("Error in stream", error);
                    emitter.completeWithError(error);
                },
                () -> {
                    log.debug("Stream completed");
                    emitter.complete();
                });
            } catch (Exception e) {
                handleStreamingException(emitter, e);
            }
        });
    }

    /**
     * Handles streaming task updates for task resubscription requests.
     *
     * @param request The task resubscription request
     * @param emitter The SSE emitter
     */
    private void handleTaskResubscription(TaskResubscriptionRequest request, SseEmitter emitter) {
        executor.execute(() -> {
            try {
                Flux<SendTaskStreamingResponse> responseFlux = taskManager.onResubscribeToTask(request);
                responseFlux.subscribe(update -> {
                    try {
                        String json = objectMapper.writeValueAsString(update);
                        emitter.send(SseEmitter.event().data(json, MediaType.APPLICATION_JSON));
                    } catch (Exception e) {
                        log.error("Error sending SSE event", e);
                        emitter.completeWithError(e);
                    }
                },
                error -> {
                    log.error("Error in stream", error);
                    emitter.completeWithError(error);
                },
                () -> {
                    log.debug("Stream completed");
                    emitter.complete();
                });
            } catch (Exception e) {
                handleStreamingException(emitter, e);
            }
        });
    }

    /**
     * Handles exceptions that occur during request processing.
     *
     * @param e The exception
     * @return An appropriate error response
     */
    private ResponseEntity<String> handleException(Exception e) {
        log.error("Exception detected: ", e);

        ErrorResponse errorResponse;
        if (e instanceof JsonParseException) {
            errorResponse = ErrorResponse.builder()
                    .error(new JsonParseError())
                    .build();
        } else if (e instanceof IllegalArgumentException) {
            errorResponse = ErrorResponse.builder()
                    .error(new InvalidRequestError())
                    .build();
        } else {
            log.error("Unhandled exception: ", e);
            errorResponse = ErrorResponse.builder()
                    .error(new org.a2a4j.models.jsonrpc.errors.InternalError())
                    .build();
        }

        try {
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonResponse);
        } catch (Exception ex) {
            log.error("Error serializing error response", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\":\"Internal server error\"}");
        }
    }

    /**
     * Handles exceptions that occur during streaming.
     *
     * @param emitter The SSE emitter
     * @param e The exception
     */
    private void handleStreamingException(SseEmitter emitter, Exception e) {
        log.error("Streaming exception detected: ", e);

        ErrorResponse errorResponse;
        if (e instanceof JsonParseException) {
            errorResponse = ErrorResponse.builder()
                    .error(new JsonParseError())
                    .build();
        } else if (e instanceof IllegalArgumentException) {
            errorResponse = ErrorResponse.builder()
                    .error(new InvalidRequestError())
                    .build();
        } else {
            log.error("Unhandled streaming exception: ", e);
            errorResponse = ErrorResponse.builder()
                    .error(new org.a2a4j.models.jsonrpc.errors.InternalError())
                    .build();
        }

        try {
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            emitter.send(SseEmitter.event().data(jsonResponse, MediaType.APPLICATION_JSON));
            emitter.complete();
        } catch (IOException ex) {
            log.error("Error sending error SSE event", ex);
            emitter.completeWithError(ex);
        }
    }
}

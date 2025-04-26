package org.a2a4j.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.a2a4j.models.AgentCard;
import org.a2a4j.models.Message;

import org.a2a4j.models.jsonrpc.*;
import org.a2a4j.models.notification.PushNotificationConfig;
import org.a2a4j.models.notification.TaskPushNotificationConfig;
import org.a2a4j.models.params.TaskIdParams;
import org.a2a4j.models.params.TaskQueryParams;
import org.a2a4j.models.params.TaskSendParams;
import org.a2a4j.models.streaming.TaskStatusUpdateEvent;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;

/**
 * Implementation of the A2A client interface using Spring WebClient.
 */
public class A2AClientImpl implements A2AClient {

    private final WebClient webClient;
    private final String apiUrl;
    private final String agentCardUrl;
    private final Duration requestTimeout;
    private final Duration connectTimeout;
    private final Duration readTimeout;

    /**
     * Creates a new A2AClientImpl with the specified base URL and endpoint.
     *
     * @param baseUrl The base URL of the A2A server.
     * @param endpoint The endpoint path for JSON-RPC requests.
     */
    public A2AClientImpl(String baseUrl, String endpoint) {
        this(baseUrl, endpoint, WebClient.builder().build(),
             Duration.ofSeconds(30), Duration.ofSeconds(15), Duration.ofSeconds(60));
    }

    /**
     * Creates a new A2AClientImpl with the specified base URL, endpoint, and Web client.
     *
     * @param baseUrl The base URL of the A2A server.
     * @param endpoint The endpoint path for JSON-RPC requests.
     * @param webClient The WebClient instance to use.
     * @param requestTimeout The timeout for requests.
     * @param connectTimeout The timeout for connection establishment.
     * @param readTimeout The timeout for reading data.
     */
    public A2AClientImpl(String baseUrl, String endpoint, WebClient webClient,
                         Duration requestTimeout, Duration connectTimeout, Duration readTimeout) {
        this.webClient = webClient;
        this.apiUrl = baseUrl + endpoint;
        this.agentCardUrl = baseUrl + "/.well-known/agent.json";
        this.requestTimeout = requestTimeout;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    /**
     * Generates a unique request ID.
     *
     * @return A unique string ID.
     */
    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Mono<AgentCard> getAgentCard() {
        return webClient.get()
                .uri(agentCardUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                    Mono.error(new ServerException("Failed to get agent card: " + response.statusCode())))
                .bodyToMono(String.class)
                .flatMap(body -> {
                    try {
                        return Mono.just(JsonUtils.fromJsonAgentCard(body));
                    } catch (JsonProcessingException e) {
                        return Mono.error(new ServerException("Failed to parse agent card", e));
                    }
                });
    }

    @Override
    public Mono<GetTaskResponse> getTask(String taskId, Integer historyLength, String requestId) {
        GetTaskRequest request = GetTaskRequest.builder()
                .id(requestId)
                .params(TaskQueryParams.builder()
                    .id(taskId)
                    .historyLength(historyLength)
                    .build())
                .build();

        return executeRequest(request, GetTaskResponse.class);
    }

    @Override
    public Mono<GetTaskResponse> getTask(String taskId) {
        return getTask(taskId, 10, generateRequestId());
    }

    @Override
    public Mono<SendTaskResponse> sendTask(Message message, String taskId, String sessionId, Integer historyLength, String requestId) {
        SendTaskRequest request = SendTaskRequest.builder()
                .id(requestId)
                .params(TaskSendParams.builder()
                    .id(taskId)
                    .sessionId(sessionId)
                    .message(message)
                    .historyLength(historyLength)
                    .pushNotification(null)
                    .metadata(new HashMap<>())
                    .build())
                .build();

        return executeRequest(request, SendTaskResponse.class);
    }

    @Override
    public Mono<SendTaskResponse> sendTask(Message message) {
        String taskId = "task::" + UUID.randomUUID();
        String sessionId = "session::" + UUID.randomUUID();
        return sendTask(message, taskId, sessionId, 10, generateRequestId());
    }

    @Override
    public Flux<SendTaskStreamingResponse> sendTaskStreaming(String taskId, String sessionId, Message message, Integer historyLength, String requestId) {
        SendTaskStreamingRequest request = SendTaskStreamingRequest.builder()
                .id(requestId)
                .params(TaskSendParams.builder()
                    .id(taskId)
                    .sessionId(sessionId)
                    .message(message)
                    .historyLength(historyLength)
                    .pushNotification(null)
                    .metadata(new HashMap<>())
                    .build())
                .build();

        return executeStreamingRequest(request)
                .takeUntilOther(
                    Mono.never().delaySubscription(
                        Flux.from(executeStreamingRequest(request))
                            .filter(response -> {
                                if (response.getResult() instanceof TaskStatusUpdateEvent) {
                                    return ((TaskStatusUpdateEvent) response.getResult()).getFinalFlag();
                                }
                                return false;
                            })
                            .next()
                    )
                );
    }

    @Override
    public Flux<SendTaskStreamingResponse> sendTaskStreaming(Message message) {
        String taskId = "task::" + UUID.randomUUID();
        String sessionId = "session::" + UUID.randomUUID();
        return sendTaskStreaming(taskId, sessionId, message, 10, generateRequestId());
    }

    @Override
    public Mono<CancelTaskResponse> cancelTask(String taskId, String requestId) {
        CancelTaskRequest request = CancelTaskRequest.builder()
                .id(requestId)
                .params(TaskIdParams.builder().id(taskId).build())
                .build();

        return executeRequest(request, CancelTaskResponse.class);
    }

    @Override
    public Mono<CancelTaskResponse> cancelTask(String taskId) {
        return cancelTask(taskId, generateRequestId());
    }

    @Override
    public Mono<SetTaskPushNotificationResponse> setTaskPushNotification(String taskId, PushNotificationConfig config, String requestId) {
        TaskPushNotificationConfig params = TaskPushNotificationConfig.builder()
                .id(taskId)
                .pushNotificationConfig(config)
                .build();

        SetTaskPushNotificationRequest request = SetTaskPushNotificationRequest.builder()
                .id(requestId)
                .params(params)
                .build();

        return executeRequest(request, SetTaskPushNotificationResponse.class);
    }

    @Override
    public Mono<SetTaskPushNotificationResponse> setTaskPushNotification(String taskId, PushNotificationConfig config) {
        return setTaskPushNotification(taskId, config, generateRequestId());
    }

    @Override
    public Mono<GetTaskPushNotificationResponse> getTaskPushNotification(String taskId, String requestId) {
        GetTaskPushNotificationRequest request = GetTaskPushNotificationRequest.builder()
                .id(requestId)
                .params(TaskIdParams.builder().id(taskId).build())
                .build();

        return executeRequest(request, GetTaskPushNotificationResponse.class);
    }

    @Override
    public Mono<GetTaskPushNotificationResponse> getTaskPushNotification(String taskId) {
        return getTaskPushNotification(taskId, generateRequestId());
    }

    @Override
    public Flux<SendTaskStreamingResponse> resubscribeTask(String taskId, String requestId) {
        TaskResubscriptionRequest request = TaskResubscriptionRequest.builder()
                .id(requestId)
                .params(TaskQueryParams.builder().id(taskId).build())
                .build();

        return executeStreamingRequest(request);
    }

    @Override
    public Flux<SendTaskStreamingResponse> resubscribeTask(String taskId) {
        return resubscribeTask(taskId, generateRequestId());
    }

    /**
     * Executes a JSON-RPC request and returns a Mono with the response.
     *
     * @param request The request to send.
     * @param responseType The response class type.
     * @param <T> The response type.
     * @return A Mono containing the response.
     */
    private <T extends JsonRpcResponse> Mono<T> executeRequest(JsonRpcRequest request, Class<T> responseType) {
        try {
            String requestBody = JsonUtils.toJson(request);

            return webClient.post()
                    .uri(apiUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response ->
                        Mono.error(new ServerException("Failed to execute request: " + response.statusCode())))
                    .bodyToMono(String.class)
                    .flatMap(body -> {
                        try {
                            return Mono.just(JsonUtils.fromJsonResponse(body, responseType));
                        } catch (JsonProcessingException e) {
                            return Mono.error(new ServerException("Failed to parse response", e));
                        }
                    });
        } catch (JsonProcessingException e) {
            return Mono.error(new ServerException("Failed to serialize request", e));
        }
    }

    /**
     * Executes a streaming JSON-RPC request and returns a Flux of events.
     *
     * @param request The request to send.
     * @return A Flux of streaming responses.
     */
    private Flux<SendTaskStreamingResponse> executeStreamingRequest(JsonRpcRequest request) {
        try {
            String requestBody = JsonUtils.toJson(request);

            return webClient.post()
                    .uri(apiUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response ->
                        Mono.error(new ServerException("Failed to execute streaming request: " + response.statusCode())))
                    .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
                    .filter(event -> event.data() != null)
                    .handle((event, sink) -> {
                        try {
                            sink.next(JsonUtils.fromJsonResponse(event.data(), SendTaskStreamingResponse.class));
                        } catch (JsonProcessingException e) {
                            sink.error(new ServerException("Failed to parse streaming response", e));
                        }
                    });
        } catch (JsonProcessingException e) {
            return Flux.error(new ServerException("Failed to serialize request", e));
        }
    }

    @Override
    public void close() throws IOException {
        // WebClient doesn't need explicit closing
    }

    /**
     * A builder for creating A2AClientImpl instances.
     */
    public static class Builder {
        private String baseUrl;
        private String endpoint = "/";
        private WebClient webClient;
        private Duration requestTimeout = Duration.ofSeconds(30);
        private Duration connectTimeout = Duration.ofSeconds(15);
        private Duration readTimeout = Duration.ofSeconds(60);

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder webClient(WebClient webClient) {
            this.webClient = webClient;
            return this;
        }

        public Builder requestTimeout(Duration requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        public Builder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder readTimeout(Duration readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public A2AClient build() {
            if (baseUrl == null) {
                throw new IllegalStateException("Base URL must be provided");
            }

            WebClient client = webClient != null ? webClient : WebClient.builder().build();

            return new A2AClientImpl(baseUrl, endpoint, client,
                                   requestTimeout, connectTimeout, readTimeout);
        }
    }

    /**
     * Creates a new builder for A2AClientImpl.
     *
     * @return A new builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }
}

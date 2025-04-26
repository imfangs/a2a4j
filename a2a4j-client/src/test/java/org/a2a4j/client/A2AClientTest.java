package org.a2a4j.client;

import org.a2a4j.models.AgentCard;
import org.a2a4j.models.Capabilities;
import org.a2a4j.models.Message;
import org.a2a4j.models.Role;
import org.a2a4j.models.Task;
import org.a2a4j.models.jsonrpc.GetTaskResponse;
import org.a2a4j.models.jsonrpc.SendTaskResponse;
import org.a2a4j.models.part.TextPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class A2AClientTest {

    @Mock
    private ExchangeFunction exchangeFunction;

    private WebClient webClient;
    private A2AClient client;
    
    private final String baseUrl = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();
        
        client = A2AClientImpl.builder()
                .baseUrl(baseUrl)
                .webClient(webClient)
                .build();
    }

    @Test
    void getAgentCard_shouldReturnAgentCard() throws Exception {
        // Given
        AgentCard agentCard = AgentCard.builder()
                .name("Test Agent")
                .description("A test agent for A2AClient")
                .url(baseUrl)
                .version("1.0.0")
                .capabilities(new Capabilities(true, true, true))
                .defaultInputModes(List.of("text"))
                .defaultOutputModes(List.of("text"))
                .skills(Collections.emptyList())
                .build();
        
        String agentCardJson = JsonUtils.toJson(agentCard);
        
        // Setup mock
        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK)
                .body(agentCardJson)
                .build();
        
        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.just(mockResponse));
        
        // When & Then
        StepVerifier.create(client.getAgentCard())
                .expectNextMatches(card -> 
                    card.getName().equals("Test Agent") &&
                    card.getDescription().equals("A test agent for A2AClient") &&
                    card.getVersion().equals("1.0.0"))
                .verifyComplete();
    }
    
    @Test
    void sendTask_shouldReturnTaskResponse() throws Exception {
        // Given
        String taskId = "test-task-123";
        String sessionId = "session-123";
        
        Message message = new Message(
                Role.USER,
                List.of(TextPart.builder().text("Hello").build()),
                Collections.emptyMap()
        );
        
        Task task = new Task(
                taskId, 
                sessionId, 
                null,  // Status would be populated in a real response
                List.of(message),  // History
                Collections.emptyList(),  // Artifacts
                Collections.emptyMap()  // Metadata
        );
        
        // Use builder for SendTaskResponse
        SendTaskResponse response = SendTaskResponse.builder()
                .result(task)
                .build();
        
        String responseJson = JsonUtils.toJson(response);
        
        // Setup mock
        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK)
                .body(responseJson)
                .build();
        
        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.just(mockResponse));
        
        // When & Then
        StepVerifier.create(client.sendTask(message, taskId, sessionId, 10, "request-id-123"))
                .expectNextMatches(resp -> 
                    resp.getResult() != null &&
                    resp.getResult().getId().equals(taskId) &&
                    resp.getResult().getSessionId().equals(sessionId) &&
                    resp.getResult().getHistory() != null &&
                    resp.getResult().getHistory().size() == 1)
                .verifyComplete();
    }
    
    @Test
    void getTask_shouldReturnTaskResponse() throws Exception {
        // Given
        String taskId = "test-task-123";
        
        Task task = new Task(
                taskId, 
                "session-123", 
                null,  // Status would be populated in a real response
                Collections.emptyList(),  // History
                Collections.emptyList(),  // Artifacts
                Collections.emptyMap()  // Metadata
        );
        
        // Use builder for GetTaskResponse
        GetTaskResponse response = GetTaskResponse.builder()
                .result(task)
                .build();
        
        String responseJson = JsonUtils.toJson(response);
        
        // Setup mock
        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK)
                .body(responseJson)
                .build();
        
        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.just(mockResponse));
        
        // When & Then
        StepVerifier.create(client.getTask(taskId))
                .expectNextMatches(resp -> 
                    resp.getResult() != null &&
                    resp.getResult().getId().equals(taskId))
                .verifyComplete();
    }
    
    @Test
    void getTask_shouldHandleError() throws Exception {
        // Given
        String taskId = "non-existent-task";
        
        // Setup mock
        ClientResponse mockResponse = ClientResponse.create(HttpStatus.NOT_FOUND)
                .build();
        
        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.just(mockResponse));
        
        // When & Then
        StepVerifier.create(client.getTask(taskId))
                .expectError(ServerException.class)
                .verify();
    }
} 
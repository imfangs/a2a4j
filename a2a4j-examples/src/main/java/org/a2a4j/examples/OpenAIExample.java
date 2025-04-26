// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.examples;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import org.a2a4j.client.A2AClient;
import org.a2a4j.client.A2AClientImpl;
import org.a2a4j.models.AgentCard;
import org.a2a4j.models.Capabilities;
import org.a2a4j.models.Message;
import org.a2a4j.models.Role;
import org.a2a4j.models.jsonrpc.SendTaskResponse;
import org.a2a4j.server.BasicTaskManager;
import org.a2a4j.spring.A2AServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Example demonstrating detailed integration with OpenAI API via Langchain4j.
 * This example shows more advanced usage with message history and system prompts.
 */
@SpringBootApplication
public class OpenAIExample implements CommandLineRunner {

    private final ApplicationContext applicationContext;
    
    // Store conversation history
    private final List<ChatMessage> conversationHistory = new ArrayList<>();
    
    public OpenAIExample(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        // Add a system message to define the assistant's behavior
        conversationHistory.add(new SystemMessage("You are a helpful assistant specializing in Java programming. " +
                "You provide concise, accurate answers about Java and related technologies."));
    }

    public static void main(String[] args) {
        SpringApplication.run(OpenAIExample.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Create the OpenAI chat model with advanced configuration
        ChatLanguageModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("gpt-4o-mini")
                .temperature(0.7)
                .topP(0.95)
                .maxTokens(500)
                .build();

        // Create a handler that calls OpenAI API via LangChain4j to process messages
        BasicTaskHandler taskHandler = new BasicTaskHandler(message -> {
            String userMessage = BasicTaskHandler.getMessageContent(message);
            return callOpenAI(chatModel, userMessage);
        });

        // Create a TaskManager with your TaskHandler
        BasicTaskManager taskManager = new BasicTaskManager(taskHandler);

        // Define your Agent's capabilities with streaming enabled
        Capabilities capabilities = new Capabilities(
            true, // streaming
            true, // pushNotifications
            true  // stateTransitionHistory
        );

        // Create an AgentCard for your Agent
        AgentCard agentCard = AgentCard.builder()
            .name("Advanced OpenAI Agent")
            .description("An A2A-4J Agent that uses OpenAI with conversation history")
            .url("https://example.com")
            .version("1.0.0")
            .capabilities(capabilities)
            .defaultInputModes(Collections.singletonList("text"))
            .defaultOutputModes(Collections.singletonList("text"))
            .skills(Collections.emptyList())
            .build();

        // Create and start the A2A Server
        A2AServer server = new A2AServer(
            "/",
            agentCard,
            taskManager,
            applicationContext
        );

        // Start the server
        server.start(false);

        // Wait for the server to start
        TimeUnit.SECONDS.sleep(1);

        // Create an A2A client to test the server
        A2AClient client = A2AClientImpl.builder()
            .baseUrl("http://localhost:8080")
            .build();

        try {
            System.out.println("⚠️ Make sure you have set the OPENAI_API_KEY environment variable!");
            
            // Get the Agent's metadata
            AgentCard remoteAgentCard = client.getAgentCard().block();
            System.out.println("Connected to Agent: " + remoteAgentCard.getName());

            // Demonstrate normal request/response
            Message message1 = new Message(Role.USER, "What are Java records and when should I use them?");
            System.out.println("\n[USER]: " + message1.getTextContent());
            
            SendTaskResponse response1 = client.sendTask(message1, "task-1", "session-1", 10, "request-1").block();
            if (response1.getResult() != null) {
                System.out.println("[AGENT]: Response received");
            } else {
                System.out.println("[ERROR]: " + response1.getError().getMessage());
            }
            
            TimeUnit.SECONDS.sleep(2);
            
            // Demonstrate streaming response
            Message message2 = new Message(Role.USER, "Give me a simple example of a Java record for storing geographic coordinates.");
            System.out.println("\n[USER]: " + message2.getTextContent());
            
            System.out.println("[AGENT STREAMING]:");
            client.sendTaskStreaming("task-2", "session-1", message2, 10, "request-2")
                  .doOnNext(response -> {
                      System.out.print(".");
                  })
                  .blockLast();
            
            System.out.println("\n[STREAMING COMPLETE]");
            
        } finally {
            // Close the client
            client.close();
            
            // Keep the application running until user intervention
            System.out.println("\nServer is running. Press Ctrl+C to exit.");
        }
    }

    /**
     * Calls OpenAI via LangChain4j with conversation history tracking
     *
     * @param chatModel The LangChain4j chat model
     * @param userPrompt The user's prompt
     * @return The response from OpenAI
     */
    private String callOpenAI(ChatLanguageModel chatModel, String userPrompt) {
        try {
            // Add the user message to history
            UserMessage userMessage = new UserMessage(userPrompt);
            conversationHistory.add(userMessage);
            
            // Get the response from the model with the full conversation history
            Response<AiMessage> response = chatModel.generate(conversationHistory);
            
            // Extract the AI message content
            AiMessage aiMessage = response.content();
            
            // Add the AI response to the conversation history for context in future messages
            conversationHistory.add(aiMessage);
            
            // Return just the text content
            return aiMessage.text();
            
        } catch (Exception e) {
            System.err.println("Error calling OpenAI via LangChain4j: " + e.getMessage());
            return "Sorry, I encountered an error: " + e.getMessage();
        }
    }
    
    @Bean
    public A2AServer a2aServer() {
        return null; // This is just a placeholder; we'll create the actual server in the run method
    }
} 
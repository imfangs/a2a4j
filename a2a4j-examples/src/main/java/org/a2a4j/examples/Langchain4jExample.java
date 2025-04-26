// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.examples;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
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

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Example demonstrating integration with LangChain4j and OpenAI.
 * This is the Java equivalent of the Kotlin Langchain4jExample.
 */
@SpringBootApplication
public class Langchain4jExample implements CommandLineRunner {

    private final ApplicationContext applicationContext;
    
    public Langchain4jExample(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static void main(String[] args) {
        SpringApplication.run(Langchain4jExample.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Create the OpenAI chat model from LangChain4j
        ChatLanguageModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("gpt-4o-mini")
                .temperature(0.7)
                .build();

        // Create a handler that calls LangChain4j to process messages
        BasicTaskHandler taskHandler = new BasicTaskHandler(message -> 
            callLangChain4j(chatModel, BasicTaskHandler.getMessageContent(message))
        );

        // Create a TaskManager with your TaskHandler
        BasicTaskManager taskManager = new BasicTaskManager(taskHandler);

        // Define your Agent's capabilities
        Capabilities capabilities = new Capabilities();

        // Create an AgentCard for your Agent
        AgentCard agentCard = AgentCard.builder()
            .name("LangChain4j Agent")
            .description("An A2A-4J Agent that uses LangChain4j with OpenAI")
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

            // Create a message to send to the Agent
            Message message = new Message(Role.USER, "Hello, tell me a fun fact about Java programming language in a single sentence.");

            // Send a task to the Agent
            SendTaskResponse response = client.sendTask(message, "task-123", "session-456", 10, "request-789").block();

            // Process the response
            if (response.getResult() != null) {
                System.out.println("Agent response: " + response);
            } else {
                System.out.println("Error: " + response.getError().getMessage());
            }
        } finally {
            // Close the client
            client.close();
            
            // Keep the application running until user intervention
            System.out.println("Server is running. Press Ctrl+C to exit.");
        }
    }

    /**
     * Calls the LangChain4j model with the given prompt
     *
     * @param chatModel The LangChain4j chat model
     * @param prompt The prompt to send
     * @return The response from the model
     */
    private String callLangChain4j(ChatLanguageModel chatModel, String prompt) {
        try {
            return chatModel.generate(prompt);
        } catch (Exception e) {
            System.err.println("Error calling LangChain4j: " + e.getMessage());
            return "Sorry, I encountered an error: " + e.getMessage();
        }
    }
    
    @Bean
    public A2AServer a2aServer() {
        return null; // This is just a placeholder; we'll create the actual server in the run method
    }
} 
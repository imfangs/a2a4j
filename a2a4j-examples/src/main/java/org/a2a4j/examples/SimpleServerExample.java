// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.examples;

import org.a2a4j.client.A2AClient;
import org.a2a4j.client.A2AClientImpl;
import org.a2a4j.models.AgentCard;
import org.a2a4j.models.Capabilities;
import org.a2a4j.models.Message;
import org.a2a4j.models.Role;
import org.a2a4j.models.Task;
import org.a2a4j.models.jsonrpc.SendTaskResponse;
import org.a2a4j.server.BasicTaskManager;
import org.a2a4j.server.TaskHandler;
import org.a2a4j.spring.A2AServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Example showing a simple A2A server and client interaction.
 */
@SpringBootApplication
public class SimpleServerExample implements CommandLineRunner {

    private final ApplicationContext applicationContext;
    
    public SimpleServerExample(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static void main(String[] args) {
        SpringApplication.run(SimpleServerExample.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Create a simple agent function that responds with a greeting
        // In a real example, this would call out to an actual AI/LLM
        String greeting = "Hello, from A2A!";
        BasicTaskHandler taskHandler = new BasicTaskHandler(message -> 
            greeting + " I received: " + BasicTaskHandler.getMessageContent(message)
        );

        // Create a TaskManager with your TaskHandler
        BasicTaskManager taskManager = new BasicTaskManager(taskHandler);

        // Define your Agent's capabilities
        Capabilities capabilities = new Capabilities(
            true, // streaming
            true, // pushNotifications
            true  // stateTransitionHistory
        );

        // Create an AgentCard for your Agent
        AgentCard agentCard = AgentCard.builder()
            .name("My Java Agent")
            .description("A simple A2A-4J Agent example")
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
            // Get the Agent's metadata
            AgentCard remoteAgentCard = client.getAgentCard().block();
            System.out.println("Connected to Agent: " + remoteAgentCard.getName());

            // Create a message to send to the Agent
            Message message = new Message(Role.USER, "Hello, Agent!");

            // Send a task to the Agent
            SendTaskResponse response = client.sendTask(message, "task-123", "session-456", 10, "request-789").block();

            // Process the response
            if (response != null) {
                try {
                    Task result = response.getResult();
                    if (result != null) {
                        System.out.println("Agent responded: " + result);
                    } else if (response.getError() != null) {
                        System.out.println("Error: " + response.getError().getMessage());
                    } else {
                        System.out.println("Received empty response");
                    }
                } catch (Exception e) {
                    System.err.println("Error processing result: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("No response received");
            }
        } catch (Exception e) {
            System.err.println("Error during A2A communication: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close the client
            client.close();
            
            // Keep the application running until user intervention
            System.out.println("Server is running. Press Ctrl+C to exit.");
        }
    }
    
    @Bean
    public A2AServer a2aServer() {
        return null; // This is just a placeholder; we'll create the actual server in the run method
    }

    @Bean
    public TaskHandler taskHandler() {
        // Create a simple agent function that responds with a greeting
        String greeting = "Hello, from A2A!";
        return new BasicTaskHandler(message -> 
            greeting + " I received: " + BasicTaskHandler.getMessageContent(message)
        );
    }
}

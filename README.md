# A2A-4J: Java Implementation of Agent-to-Agent Protocol

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) <!-- TODO: Add other badges (build status, coverage, maven central) -->

**Project Status:** [Alpha] <!-- TODO: Update project status -->

## Overview

A2A-4J is a Java implementation of the Agent-to-Agent (A2A) protocol. The A2A protocol facilitates standardized communication between AI agents, enabling them to exchange tasks, messages, and artifacts.

This project provides a high-quality, production-ready Java library leveraging the Spring ecosystem (Spring Boot 3.x).

**Core Features:**
*   Complete implementation of the A2A protocol specification.
*   Server implementation using Spring WebFlux/MVC.
*   Reactive and Blocking Client implementations.
*   Support for task streaming (Server-Sent Events).
*   Pluggable storage options (In-memory, Redis).
*   OpenAPI documentation generation.

## Quick Start

<!-- TODO: Add concise Maven/Gradle dependency snippets -->

**Example: Basic Server Setup**
```java
// 1. Define a TaskHandler (e.g., using BasicTaskHandler)
TaskHandler taskHandler = new BasicTaskHandler(message -> 
    "Hello back! You said: " + BasicTaskHandler.getMessageContent(message)
);

// 2. Create a TaskManager
TaskManager taskManager = new BasicTaskManager(taskHandler);

// 3. Define AgentCard (metadata for your agent)
AgentCard agentCard = AgentCard.builder()
    .name("My Simple Agent")
    .description("A basic A2A-4J Agent")
    .capabilities(new Capabilities()) // Use default capabilities
    .build();

// 4. Get the Spring ApplicationContext
// (Assuming you are in a Spring Boot application)
ApplicationContext context = ... ; 

// 5. Create and start the A2A Server
A2AServer server = new A2AServer(
    "/", // Base path for A2A endpoints
    agentCard,
    taskManager,
    context
);
server.start(false); // Start in foreground

System.out.println("A2A Server running on port 8080 (default)");
```

**Example: Basic Client Usage**
```java
// 1. Create an A2AClient instance
A2AClient client = A2AClientImpl.builder()
    .baseUrl("http://localhost:8080") // URL of the target A2A server
    .build();

// 2. Get the server's AgentCard (optional, good for verification)
AgentCard remoteAgentCard = client.getAgentCard().block();
System.out.println("Connected to: " + remoteAgentCard.getName());

// 3. Create a message to send
Message userMessage = new Message(Role.USER, "Hi Agent!");

// 4. Send the task (blocking example)
SendTaskResponse response = client.sendTask(
    userMessage,
    "my-task-id-1",   // Unique Task ID
    "my-session-id", // Optional Session ID
    10,             // Optional priority
    "my-request-id" // Optional request ID
).block();

// 5. Process the response
if (response != null && response.getResult() != null) {
    Task resultTask = response.getResult();
    System.out.println("Task completed. Result: " + resultTask);
    // Access artifacts, status, etc. from resultTask
} else if (response != null && response.getError() != null) {
    System.err.println("A2A Error: " + response.getError().getMessage());
} else {
    System.err.println("No response received or unexpected error.");
}

// 6. Close the client when done
client.close();
```

For more detailed examples, please see the `a2a4j-examples` module.

### Storage

The server supports pluggable storage for tasks:
*   **In-Memory:** Default, suitable for testing and development.
*   **Redis:** Requires Redis configuration (See `a2a4j-storage-redis`).

<!-- TODO: Explain how to select/configure storage -->

## Project Structure

The project is organized into the following Maven modules:

*   `a2a4j-models`: Core data models defined by the A2A protocol schema.
*   `a2a4j-server`: Server implementation (controllers, task management, storage interface).
*   `a2a4j-client`: Client implementation for interacting with A2A servers.
*   `a2a4j-storage-redis`: Redis-based storage implementation.
*   `a2a4j-spring-boot-starter`: Spring Boot auto-configuration and starter.
*   `a2a4j-examples`: Usage examples.

## Contributing

Contributions are welcome! Please feel free to:
*   Report bugs and suggest features
*   Set up your development environment
*   Submit pull requests
*   Adhere to code style

## License

This project is licensed under the Apache License 2.0. 

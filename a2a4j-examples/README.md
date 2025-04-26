# A2A-4J Examples

This module contains examples demonstrating how to use the A2A-4J library for building Agent-to-Agent communication in Java.

## Prerequisites

- JDK 17 or newer
- Maven
- For OpenAI examples: a valid OpenAI API key

## Available Examples

### 1. Simple Server Example

A basic example showing how to create an A2A server and client with a simple response handler.

To run:
```bash
mvn spring-boot:run -Dspring-boot.run.mainClass=org.a2a4j.examples.SimpleServerExample
```

### 2. LangChain4j Example

Shows how to integrate a LangChain4j-based agent with the A2A protocol.

To run:
```bash
export OPENAI_API_KEY=your_openai_api_key
mvn spring-boot:run -Dspring-boot.run.mainClass=org.a2a4j.examples.Langchain4jExample
```

### 3. Advanced OpenAI Example

Demonstrates more advanced features including:
- Conversation history management
- System prompts
- Streaming responses
- Multiple requests in a session

To run:
```bash
export OPENAI_API_KEY=your_openai_api_key
mvn spring-boot:run -Dspring-boot.run.mainClass=org.a2a4j.examples.OpenAIExample
```

## Key Components

Each example demonstrates these core A2A components:

1. **TaskHandler**: Processes incoming tasks and generates responses
2. **TaskManager**: Manages the lifecycle of tasks
3. **A2AServer**: Provides HTTP endpoints for the A2A protocol
4. **A2AClient**: Communicates with A2A servers

## Configuration

You can modify the server configuration in `src/main/resources/application.properties`.

## Notes

- These examples run on port 8080 by default
- The client connects to `http://localhost:8080` to communicate with the server
- For LLM-based examples, make sure your OpenAI API key is set as an environment variable 
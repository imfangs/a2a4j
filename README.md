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
// TODO: Add minimal code example for setting up a basic server
```

**Example: Basic Client Usage**
```java
// TODO: Add minimal code example for sending a task with the client
```

For more detailed examples, please see the `a2a4j-examples` module.

## Usage

<!-- TODO: Expand on basic usage patterns, configuration options, etc. -->

### Server Configuration

<!-- TODO: Explain how to configure the server (e.g., port, storage backend via application.properties/yml) -->

### Client Configuration

<!-- TODO: Explain how to configure the client (e.g., target URL, timeouts) -->

### Storage

The server supports pluggable storage for tasks:
*   **In-Memory:** Default, suitable for testing and development.
*   **Redis:** Requires Redis configuration (See `a2a4j-storage-redis`).

<!-- TODO: Explain how to select/configure storage -->

## API Documentation

*   **Server API (OpenAPI):** Available at `/swagger-ui.html` when the server is running.
*   **Javadoc:** [Link to hosted Javadoc] <!-- TODO: Add link to generated Javadoc -->

## Project Structure

The project is organized into the following Maven modules:

*   `a2a4j-models`: Core data models defined by the A2A protocol schema.
*   `a2a4j-server`: Server implementation (controllers, task management, storage interface).
*   `a2a4j-client`: Client implementation for interacting with A2A servers.
*   `a2a4j-storage-redis`: Redis-based storage implementation.
*   `a2a4j-spring-boot-starter`: Spring Boot auto-configuration and starter.
*   `a2a4j-examples`: Usage examples.

## Contributing

Contributions are welcome! Please read our [Contributing Guidelines](CONTRIBUTING.md) for details on how to:
*   Report bugs and suggest features
*   Set up your development environment
*   Submit pull requests
*   Adhere to code style

We follow the [Contributor Covenant Code of Conduct](CODE_OF_CONDUCT.md).

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.

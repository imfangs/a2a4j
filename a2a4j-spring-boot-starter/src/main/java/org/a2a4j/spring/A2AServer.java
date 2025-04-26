package org.a2a4j.spring;

import org.a2a4j.models.AgentCard;
import org.a2a4j.server.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;

/**
 * A2AServer implements an Agent-to-Agent communication server based on the A2A protocol.
 *
 * This server handles various JSON-RPC requests for task management, including task creation,
 * retrieval, cancellation, and notification management. It also provides an endpoint for
 * retrieving the agent's metadata (agent card).
 */
public class A2AServer {
    
    private static final Logger log = LoggerFactory.getLogger(A2AServer.class);
    
    private final String endpoint;
    private final AgentCard agentCard;
    private final TaskManager taskManager;
    private final ApplicationContext applicationContext;
    private WebServer webServer;
    private boolean running = false;
    
    /**
     * Creates a new A2AServer instance.
     *
     * @param endpoint The endpoint path for the server
     * @param agentCard The agent card containing metadata about this agent
     * @param taskManager The task manager responsible for handling task-related operations
     * @param applicationContext The Spring application context
     */
    public A2AServer(
            String endpoint,
            AgentCard agentCard,
            TaskManager taskManager,
            ApplicationContext applicationContext) {
        this.endpoint = endpoint;
        this.agentCard = agentCard;
        this.taskManager = taskManager;
        this.applicationContext = applicationContext;
    }
    
    /**
     * Starts the A2A server and begins listening for incoming connections.
     *
     * @param wait If true, the method will wait for the server to start
     */
    public void start(boolean wait) {
        log.info("Starting A2A server with endpoint: {}", endpoint);
        
        if (applicationContext instanceof ServletWebServerApplicationContext) {
            webServer = ((ServletWebServerApplicationContext) applicationContext).getWebServer();
            if (webServer != null && !running) {
                webServer.start();
                running = true;
                log.info("A2A server started on port {}", getPort());
            }
        } else {
            log.warn("ApplicationContext is not a ServletWebServerApplicationContext. Server may not start properly.");
        }
        
        if (wait) {
            // Wait for the server to be stopped
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Server wait interrupted", e);
                }
            }
        }
    }
    
    /**
     * Stops the A2A server.
     */
    public void stop() {
        if (webServer != null && running) {
            log.info("Stopping A2A server");
            webServer.stop();
            running = false;
            
            // Notify any waiting threads
            synchronized (this) {
                this.notifyAll();
            }
        }
    }
    
    /**
     * Gets the port number the server is listening on.
     *
     * @return The port number
     */
    public int getPort() {
        if (applicationContext instanceof ServletWebServerApplicationContext) {
            return ((ServletWebServerApplicationContext) applicationContext).getWebServer().getPort();
        }
        return -1;
    }
    
    /**
     * Gets the agent card for this server.
     *
     * @return The agent card
     */
    public AgentCard getAgentCard() {
        return agentCard;
    }
    
    /**
     * Gets the task manager for this server.
     *
     * @return The task manager
     */
    public TaskManager getTaskManager() {
        return taskManager;
    }
    
    /**
     * Gets the endpoint path for this server.
     *
     * @return The endpoint path
     */
    public String getEndpoint() {
        return endpoint;
    }
    
    /**
     * Checks if the server is running.
     *
     * @return true if the server is running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }
} 
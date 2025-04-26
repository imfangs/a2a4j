package org.a2a4j.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.a2a4j.models.AgentCard;
import org.a2a4j.server.BasicTaskManager;
import org.a2a4j.server.InMemoryTaskStorage;
import org.a2a4j.server.TaskHandler;
import org.a2a4j.server.TaskManager;
import org.a2a4j.server.notifications.BasicNotificationPublisher;
import org.a2a4j.server.notifications.NotificationPublisher;
import org.a2a4j.server.storage.TaskStorage;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot auto-configuration for A2A server.
 *
 * This class provides default beans for the A2A server, task manager, and related components.
 * It can be customized by providing alternative beans or by configuring properties.
 */
@AutoConfiguration
@EnableConfigurationProperties(A2AProperties.class)
public class A2AAutoConfiguration {

    /**
     * Creates a default TaskStorage bean if none is provided.
     *
     * @return An in-memory TaskStorage implementation
     */
    @Bean
    @ConditionalOnMissingBean
    public TaskStorage taskStorage() {
        return new InMemoryTaskStorage();
    }
    
    /**
     * Creates a default NotificationPublisher bean if none is provided.
     *
     * @return A BasicNotificationPublisher implementation
     */
    @Bean
    @ConditionalOnMissingBean
    public NotificationPublisher notificationPublisher() {
        return new BasicNotificationPublisher();
    }

    /**
     * Creates a default TaskManager bean if none is provided.
     *
     * @param taskStorage The task storage to use
     * @param taskHandler The task handler to use
     * @param notificationPublisher The notification publisher to use
     * @return A BasicTaskManager implementation
     */
    @Bean
    @ConditionalOnMissingBean
    public TaskManager taskManager(TaskStorage taskStorage, TaskHandler taskHandler, NotificationPublisher notificationPublisher) {
        return new BasicTaskManager(taskHandler, taskStorage, notificationPublisher);
    }

    /**
     * Creates a default A2AController bean if none is provided.
     *
     * @param objectMapper The ObjectMapper to use for JSON serialization/deserialization
     * @param taskManager The task manager to use
     * @param agentCard The agent card to expose
     * @return An A2AController implementation
     */
    @Bean
    @ConditionalOnMissingBean
    public A2AController a2aController(ObjectMapper objectMapper, TaskManager taskManager, AgentCard agentCard) {
        return new A2AController(objectMapper, taskManager, agentCard);
    }

    /**
     * Creates a default AgentCard bean if none is provided.
     *
     * @param properties The A2A properties
     * @return A default AgentCard
     */
    @Bean
    @ConditionalOnMissingBean
    public AgentCard agentCard(A2AProperties properties) {
        return AgentCard.builder()
                .name(properties.getServer().getName())
                .description(properties.getServer().getDescription())
                .build();
    }

    /**
     * Creates an A2AServer bean if server is enabled.
     *
     * @param properties The A2A properties
     * @param agentCard The agent card to expose
     * @param taskManager The task manager to use
     * @param applicationContext The Spring application context
     * @return An A2AServer instance
     */
    @Bean
    @ConditionalOnProperty(name = "a2a.server.enabled", havingValue = "true", matchIfMissing = true)
    public A2AServer a2aServer(
            A2AProperties properties,
            AgentCard agentCard,
            TaskManager taskManager,
            ApplicationContext applicationContext) {
        
        return new A2AServer(
                properties.getServer().getEndpoint(),
                agentCard,
                taskManager,
                applicationContext);
    }

    /**
     * Configuration for server initialization.
     */
    @Configuration
    @ConditionalOnProperty(name = "a2a.server.auto-start", havingValue = "true", matchIfMissing = true)
    public static class ServerInitializationConfiguration {

        /**
         * Creates a server initializer that starts the server on startup.
         *
         * @param a2aServer The A2A server to start
         * @return A ServerInitializer bean
         */
        @Bean
        public ServerInitializer serverInitializer(A2AServer a2aServer) {
            return new ServerInitializer(a2aServer);
        }
    }

    /**
     * Helper class that starts the A2A server on startup.
     */
    public static class ServerInitializer {
        private final A2AServer a2aServer;

        public ServerInitializer(A2AServer a2aServer) {
            this.a2aServer = a2aServer;
            initialize();
        }

        private void initialize() {
            a2aServer.start(false);
        }
    }
} 
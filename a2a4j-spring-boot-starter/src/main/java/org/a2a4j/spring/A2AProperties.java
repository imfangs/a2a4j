package org.a2a4j.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the A2A server.
 */
@ConfigurationProperties(prefix = "a2a")
public class A2AProperties {

    private final Server server = new Server();
    private final Client client = new Client();

    public Server getServer() {
        return server;
    }

    public Client getClient() {
        return client;
    }

    /**
     * Server-specific configuration properties.
     */
    public static class Server {
        /**
         * Whether the server is enabled.
         */
        private boolean enabled = true;
        
        /**
         * Whether to automatically start the server on application startup.
         */
        private boolean autoStart = true;
        
        /**
         * The hostname or IP address to bind to.
         */
        private String host = "0.0.0.0";
        
        /**
         * The port number to listen on.
         */
        private int port = 5000;
        
        /**
         * The endpoint path for the server.
         */
        private String endpoint = "/";
        
        /**
         * The name of the agent.
         */
        private String name = "Java A2A Agent";
        
        /**
         * The description of the agent.
         */
        private String description = "A Java implementation of the A2A protocol";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isAutoStart() {
            return autoStart;
        }

        public void setAutoStart(boolean autoStart) {
            this.autoStart = autoStart;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * Client-specific configuration properties.
     */
    public static class Client {
        /**
         * The base URL of the server to connect to.
         */
        private String serverUrl = "http://localhost:5000";

        public String getServerUrl() {
            return serverUrl;
        }

        public void setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
        }
    }
} 
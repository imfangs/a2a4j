package org.a2a4j.client;

/**
 * Exception thrown when there's a server-related error in the A2A client.
 */
public class ServerException extends A2AException {
    
    public ServerException(String message) {
        super(message);
    }
    
    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }
} 
package org.a2a4j.client;

/**
 * Base exception class for all A2A client exceptions.
 */
public class A2AException extends RuntimeException {
    
    public A2AException(String message) {
        super(message);
    }
    
    public A2AException(String message, Throwable cause) {
        super(message, cause);
    }
} 
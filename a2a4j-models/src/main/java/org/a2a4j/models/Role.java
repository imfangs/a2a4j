package org.a2a4j.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the role of the sender in a message.
 */
public enum Role {
    @JsonProperty("user")
    USER,
    @JsonProperty("agent")
    AGENT
} 
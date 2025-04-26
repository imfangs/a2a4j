package org.a2a4j.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the capabilities of an agent in the A2A protocol.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Capabilities {
    @Builder.Default
    private boolean streaming = false;
    @Builder.Default
    private boolean pushNotifications = false;
    @Builder.Default
    private boolean stateTransitionHistory = false;
} 
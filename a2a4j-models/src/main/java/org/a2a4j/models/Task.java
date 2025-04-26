// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a task in the A2A protocol.
 * <p>
 * Tasks are the primary unit of work in the A2A protocol. A task contains
 * information about its current status, conversation history, artifacts produced
 * during execution, and other metadata.
 * </p>
 */
@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task {
    
    private String id;
    private String sessionId;
    private TaskStatus status;
    private List<Message> history;
    private List<Artifact> artifacts;
    
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
} 
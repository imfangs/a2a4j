// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.models.streaming;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.a2a4j.models.Artifact;

import java.util.Collections;
import java.util.Map;

/**
 * Represents a task artifact update event in the A2A protocol streaming API.
 * <p>
 * This event is sent when a task produces or updates an artifact. It includes
 * the task ID, the artifact itself, and additional metadata.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskArtifactUpdateEvent implements TaskStreamingResult {
    
    private String id;
    private Artifact artifact;
    @Builder.Default
    private Map<String, Object> metadata = Collections.emptyMap();
    
    /**
     * Returns the ID of the task producing the artifact.
     *
     * @return The task ID
     */
    @Override
    public String getId() {
        return id;
    }
    
    /**
     * Returns the artifact being produced or updated.
     *
     * @return The artifact
     */
    public Artifact getArtifact() {
        return artifact;
    }
    
    /**
     * Returns the metadata associated with this update.
     *
     * @return A map of metadata key-value pairs
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }
} 
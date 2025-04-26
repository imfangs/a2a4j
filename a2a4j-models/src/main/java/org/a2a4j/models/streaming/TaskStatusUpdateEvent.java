// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.models.streaming;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.a2a4j.models.TaskStatus;

import java.util.Collections;
import java.util.Map;

/**
 * Represents a task status update event in the A2A protocol streaming API.
 * <p>
 * This event is sent when a task's status changes. It includes the task ID,
 * the new status, whether this is the final status update, and additional metadata.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskStatusUpdateEvent implements TaskStreamingResult {

    private String id;
    private TaskStatus status;

    @JsonProperty("final") // Map Java field name to JSON field name
    @Builder.Default
    private Boolean finalFlag = false;

    @Builder.Default
    private Map<String, Object> metadata = Collections.emptyMap();

    /**
     * Returns the ID of the task being updated.
     *
     * @return The task ID
     */
    @Override
    public String getId() {
        return id;
    }
    
    /**
     * Sets the ID of the task being updated.
     *
     * @param id The task ID
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Returns the new status of the task.
     *
     * @return The task status
     */
    public TaskStatus getStatus() {
        return status;
    }
    
    /**
     * Sets the new status of the task.
     *
     * @param status The task status
     */
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    /**
     * Returns the metadata associated with this update.
     *
     * @return A map of metadata key-value pairs
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    /**
     * Sets the metadata for this update.
     *
     * @param metadata A map of metadata key-value pairs
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : Collections.emptyMap();
    }
} 
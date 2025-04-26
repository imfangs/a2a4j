// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.models.streaming;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Marker interface for streaming task results in the A2A protocol.
 * <p>
 * This interface is implemented by concrete streaming result classes such as
 * {@link TaskStatusUpdateEvent} and {@link TaskArtifactUpdateEvent}.
 * Jackson uses the annotations to determine the correct subtype during deserialization.
 * </p>
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.DEDUCTION // Deduce subtype based on properties
)
@JsonSubTypes({
    @JsonSubTypes.Type(TaskStatusUpdateEvent.class),
    @JsonSubTypes.Type(TaskArtifactUpdateEvent.class)
})
public interface TaskStreamingResult {
    /**
     * Returns the task ID associated with this streaming result.
     *
     * @return The task ID
     */
    String getId();
} 
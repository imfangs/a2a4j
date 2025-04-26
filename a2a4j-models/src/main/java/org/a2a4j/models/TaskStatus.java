// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents the status of a task in the A2A protocol.
 * <p>
 * Task status includes the current state of the task, an optional message
 * providing additional information, and a timestamp indicating when the
 * status was last updated.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskStatus {

    private TaskState state;
    private Message message;
    @Builder.Default
    private String timestamp = Instant.now().toString(); // Default timestamp

    // Lombok generates constructors, getters, setters, equals, hashCode, toString, builder
} 
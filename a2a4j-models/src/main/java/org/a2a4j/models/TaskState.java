// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Enum representing the possible states of a task in the A2A protocol.
 */
public enum TaskState {
    @JsonProperty("submitted")
    SUBMITTED,

    @JsonProperty("working")
    WORKING,

    @JsonProperty("input-required")
    INPUT_REQUIRED,

    @JsonProperty("completed")
    COMPLETED,

    @JsonProperty("canceled")
    CANCELED,

    @JsonProperty("failed")
    FAILED,

    @JsonProperty("unknown")
    UNKNOWN
} 
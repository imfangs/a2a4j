// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.models.params;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;

/**
 * Parameters for task ID-based operations in the A2A protocol.
 * <p>
 * These parameters are used in requests that require only a task ID,
 * such as task retrieval or cancellation requests.
 * </p>
 * 
 * @param <T> the type of metadata values (defaults to Object if not specified)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskIdParams<T> {
    
    private String id;
    
    @Builder.Default
    private Map<String, T> metadata = Collections.emptyMap();
} 

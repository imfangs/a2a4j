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
 * Parameters for querying tasks in the A2A protocol.
 * <p>
 * These parameters are used for extended task requests that might include
 * options like history length control.
 * </p>
 * 
 * @param <T> the type of metadata values
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskQueryParams<T> {
    
    private String id;
    private Integer historyLength;
    
    @Builder.Default
    private Map<String, T> metadata = Collections.emptyMap();
} 

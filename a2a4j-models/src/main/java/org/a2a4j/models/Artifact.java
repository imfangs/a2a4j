// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.a2a4j.models.part.Part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents an artifact in the A2A protocol.
 * <p>
 * Artifacts are outputs produced by an agent while processing a task.
 * They can contain various types of content represented as parts.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Artifact {
    
    private String name;
    private String description;
    @Builder.Default
    private List<Part> parts = new ArrayList<>();
    @Builder.Default
    private Map<String, Object> metadata = Collections.emptyMap();
    @Builder.Default
    private Integer index = 0; // Default value from schema
    private Boolean append;
    private Boolean lastChunk;
} 
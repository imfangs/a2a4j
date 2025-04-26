// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.models.part;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;

/**
 * Represents a file part of a message in the A2A protocol.
 * <p>
 * File parts contain file data, including name, MIME type, and content.
 * </p>
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilePart extends Part {
    private static final String PART_TYPE = "file";

    @Builder.Default
    private String type = PART_TYPE;
    
    private FileData file;
    
    @Builder.Default
    private Map<String, Object> metadata = Collections.emptyMap();

    // Manual getters/setters/toString removed - Handled by @Data

    // Override toString to avoid potential issues with large FileData.toString() if needed,
    // but @Data's default should be fine unless FileData itself is problematic.
    // Keeping default Lombok toString for now.
}

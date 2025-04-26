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
 * Represents a text part of a message in the A2A protocol.
 * <p>
 * Text parts contain plain text content to be displayed or processed.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextPart extends Part {
    private static final String PART_TYPE = "text";

    @Builder.Default
    private String type = PART_TYPE;
    
    private String text;
    
    @Builder.Default
    private Map<String, Object> metadata = Collections.emptyMap();
}

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
 * Represents a structured data part of a message in the A2A protocol.
 * <p>
 * Data parts contain key-value pairs that represent structured data.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataPart extends Part {
    private static final String PART_TYPE = "data";

    @Builder.Default
    private String type = PART_TYPE;

    private Map<String, Object> data;
    
    @Builder.Default
    private Map<String, Object> metadata = Collections.emptyMap();
}

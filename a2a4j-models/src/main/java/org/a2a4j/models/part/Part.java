// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.models.part;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Collections;
import java.util.Map;

/**
 * The base class for different types of message parts in the A2A protocol.
 * <p>
 * Message parts can be text, files, or structured data. Each part type extends this
 * abstract class and provides its own specific properties.
 * </p>
 *
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = TextPart.class, name = "text"),
    @JsonSubTypes.Type(value = FilePart.class, name = "file"),
    @JsonSubTypes.Type(value = DataPart.class, name = "data")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Part {

    // Metadata moved to subclasses as per schema
    // private Map<String, Object> metadata = Collections.emptyMap();
    
    // Type field is needed for Jackson polymorphism with Id.NAME
    private String type;
}

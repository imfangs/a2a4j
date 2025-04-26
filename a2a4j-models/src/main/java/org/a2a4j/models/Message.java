// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import org.a2a4j.models.part.Part;
import org.a2a4j.models.part.TextPart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a message in the A2A protocol.
 * <p>
 * Messages are exchanged between agents and contain content in various formats.
 * Each message has a role (e.g., "user", "agent") and consists of one or more parts.
 * </p>
 */
@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {

    private Role role;

    @Builder.Default
    private List<Part> parts = new ArrayList<>();

    @Builder.Default
    private Map<String, Object> metadata = Collections.emptyMap();

    /**
     * Convenience constructor to create a simple text message.
     *
     * @param role The role of the message sender
     * @param text The text content of the message
     */
    public Message(Role role, String text) {
        this.role = role;
        this.parts = Collections.singletonList(TextPart.builder().text(text).build());
        this.metadata = Collections.emptyMap();
    }

    /**
     * Helper method to get the text content from the first text part of the message.
     * This is a convenience method for simple text messages.
     *
     * @return The text content, or null if no text part is found
     */
    public String getTextContent() {
        if (parts == null || parts.isEmpty()) {
            return null;
        }

        return parts.stream()
            .filter(part -> part instanceof TextPart)
            .map(part -> ((TextPart) part).getText())
            .findFirst()
            .orElse(null);
    }
}

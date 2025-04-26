package org.a2a4j.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents an agent's identity and capabilities in the A2A protocol.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentCard {
    private String name;
    private String description;
    private String url;
    private Provider provider;
    private String version;
    private String documentationUrl;
    private Capabilities capabilities;
    private Authentication authentication;
    @Builder.Default
    private List<String> defaultInputModes = List.of("text");
    @Builder.Default
    private List<String> defaultOutputModes = List.of("text");
    private List<Skill> skills;
} 
package org.a2a4j.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.a2a4j.models.AgentCard;
import org.a2a4j.models.jsonrpc.JsonRpcRequest;
import org.a2a4j.models.jsonrpc.JsonRpcResponse;

/**
 * Utility class for JSON serialization and deserialization.
 */
public class JsonUtils {
    
    private static final ObjectMapper objectMapper = createObjectMapper();
    
    /**
     * Creates and configures an ObjectMapper instance.
     * 
     * @return A properly configured ObjectMapper
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper()
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .findAndRegisterModules();
        return mapper;
    }
    
    /**
     * Converts a JSON string to a specific type.
     *
     * @param json The JSON string
     * @param valueType The class of the target type
     * @param <T> The target type
     * @return The deserialized object
     * @throws JsonProcessingException If deserialization fails
     */
    public static <T> T fromJson(String json, Class<T> valueType) throws JsonProcessingException {
        return objectMapper.readValue(json, valueType);
    }
    
    /**
     * Converts an object to a JSON string.
     *
     * @param value The object to serialize
     * @return The JSON string
     * @throws JsonProcessingException If serialization fails
     */
    public static String toJson(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }
    
    /**
     * Converts a JsonRpcRequest to a JSON string.
     *
     * @param request The request to serialize
     * @return The JSON string
     * @throws JsonProcessingException If serialization fails
     */
    public static String toJson(JsonRpcRequest request) throws JsonProcessingException {
        return objectMapper.writeValueAsString(request);
    }
    
    /**
     * Converts a JSON string to a JsonRpcResponse.
     *
     * @param json The JSON string
     * @param responseType The class of the target response type
     * @param <T> The target response type
     * @return The deserialized response
     * @throws JsonProcessingException If deserialization fails
     */
    public static <T extends JsonRpcResponse> T fromJsonResponse(String json, Class<T> responseType) throws JsonProcessingException {
        return objectMapper.readValue(json, responseType);
    }
    
    /**
     * Converts a JSON string to an AgentCard.
     *
     * @param json The JSON string
     * @return The deserialized AgentCard
     * @throws JsonProcessingException If deserialization fails
     */
    public static AgentCard fromJsonAgentCard(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, AgentCard.class);
    }
    
    /**
     * Get the configured ObjectMapper instance.
     * 
     * @return The ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
} 
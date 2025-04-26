package org.a2a4j.models.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;


/**
 * Base class for all JSON-RPC response objects in the A2A protocol.
 *
 * @param <R> the type of the result
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class JsonRpcResponse<R> {
    // Shared ObjectMapper instance with appropriate configuration
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private String jsonrpc = "2.0";
    private String id;
    private R result;
    private JsonRpcError error;

    /**
     * Converts the result object to a specific type.
     * This is useful for handling deserialization issues where Jackson may have
     * deserialized the result as a Map instead of the expected type.
     *
     * @param <T> The target type
     * @param resultClass The class of the target type
     * @return The result converted to the specified type, or null if result is null
     * @throws ClassCastException If the result cannot be converted to the specified type
     */
    protected <T> T convertResult(Class<T> resultClass) {
        if (result == null) {
            return null;
        }

        if (resultClass.isInstance(result)) {
            return resultClass.cast(result);
        }

        if (result instanceof Map) {
            // Use the pre-configured ObjectMapper that ignores unknown properties
            try {
                return OBJECT_MAPPER.convertValue(result, resultClass);
            } catch (Exception e) {
                throw new ClassCastException("Failed to convert " + result.getClass().getName()
                    + " to " + resultClass.getName() + ": " + e.getMessage());
            }
        }

        throw new ClassCastException("Cannot convert " + result.getClass().getName() + " to " + resultClass.getName());
    }
}

package org.a2a4j.models.jsonrpc.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.a2a4j.models.jsonrpc.JsonRpcError;

import java.util.Map;

/**
 * Error for JSON parse failures in the A2A protocol.
 * Code: -32700
 * Message: "Invalid JSON payload"
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonParseError extends JsonRpcError {
    private static final int ERROR_CODE = -32700;
    private static final String ERROR_MESSAGE = "Invalid JSON payload";

    /**
     * Default constructor setting the fixed code and message.
     */
    public JsonParseError() {
        super(ERROR_CODE, ERROR_MESSAGE, null);
    }

    /**
     * Constructor to create an error with specific data.
     *
     * @param data Custom data associated with the error.
     */
    public JsonParseError(Map<String, Object> data) {
        super(ERROR_CODE, ERROR_MESSAGE, data);
    }
} 
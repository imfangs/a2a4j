package org.a2a4j.models.jsonrpc.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.a2a4j.models.jsonrpc.JsonRpcError;

/**
 * Error for method not found in the A2A protocol.
 * Code: -32601
 * Message: "Method not found"
 * Data: Must be null according to schema.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MethodNotFoundError extends JsonRpcError {
    private static final int ERROR_CODE = -32601;
    private static final String ERROR_MESSAGE = "Method not found";

    /**
     * Default constructor setting the fixed code, message, and null data.
     */
    public MethodNotFoundError() {
        super(ERROR_CODE, ERROR_MESSAGE, null);
    }

    // No constructor with data needed as schema requires data to be null.
} 
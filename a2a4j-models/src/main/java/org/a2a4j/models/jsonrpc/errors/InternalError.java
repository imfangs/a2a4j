package org.a2a4j.models.jsonrpc.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.a2a4j.models.jsonrpc.JsonRpcError;

import java.util.Map;

/**
 * Error for internal errors in the A2A protocol.
 * Code: -32603
 * Message: "Internal error"
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InternalError extends JsonRpcError {
    private static final int ERROR_CODE = -32603;
    private static final String ERROR_MESSAGE = "Internal error";

    /**
     * Default constructor setting the fixed code and message.
     * Needed for Jackson deserialization.
     */
    public InternalError() {
        super(ERROR_CODE, ERROR_MESSAGE, null);
    }

    /**
     * Constructor to create an error with specific data.
     *
     * @param data Custom data associated with the error.
     */
    public InternalError(Map<String, Object> data) {
        super(ERROR_CODE, ERROR_MESSAGE, data);
    }
} 
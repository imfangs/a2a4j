package org.a2a4j.models.jsonrpc.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.a2a4j.models.jsonrpc.JsonRpcError;

import java.util.Map;

/**
 * Error for invalid parameters in the A2A protocol.
 * Code: -32602
 * Message: "Invalid parameters"
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvalidParamsError extends JsonRpcError {
    private static final int ERROR_CODE = -32602;
    private static final String ERROR_MESSAGE = "Invalid parameters";

    /**
     * Default constructor setting the fixed code and message.
     */
    public InvalidParamsError() {
        super(ERROR_CODE, ERROR_MESSAGE, null);
    }

    /**
     * Constructor to create an error with specific data.
     *
     * @param data Custom data associated with the error.
     */
    public InvalidParamsError(Map<String, Object> data) {
        super(ERROR_CODE, ERROR_MESSAGE, data);
    }
} 
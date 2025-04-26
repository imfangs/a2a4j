package org.a2a4j.models.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;


import java.util.Map;

/**
 * Represents a JSON-RPC request with an unknown method.
 * This is used as a fallback during deserialization if the method name doesn't match known requests.
 */
@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnknownMethodRequest extends JsonRpcRequest<Map<String, Object>> {

    // Constructors removed - Handled by Lombok

    @Override
    public String getMethod() {
        // Method is unknown by definition
        return null;
    }
}

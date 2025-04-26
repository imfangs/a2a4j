package org.a2a4j.models.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import org.a2a4j.models.params.TaskIdParams;

/**
 * JSON-RPC request for canceling a task in the A2A protocol.
 */
@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CancelTaskRequest extends JsonRpcRequest<TaskIdParams<?>> {
    private static final String METHOD_NAME = "tasks/cancel";

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}

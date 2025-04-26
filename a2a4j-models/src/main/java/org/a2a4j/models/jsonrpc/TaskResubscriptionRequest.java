package org.a2a4j.models.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import org.a2a4j.models.params.TaskQueryParams;

/**
 * JSON-RPC request for resubscribing to a task in the A2A protocol.
 */
@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskResubscriptionRequest extends JsonRpcRequest<TaskQueryParams<?>> {
    private static final String METHOD_NAME = "tasks/resubscribe";

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}

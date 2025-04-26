package org.a2a4j.models.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import org.a2a4j.models.params.TaskSendParams;

/**
 * JSON-RPC request for sending a task in the A2A protocol.
 */
@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendTaskRequest extends JsonRpcRequest<TaskSendParams> {
    private static final String METHOD_NAME = "tasks/send";

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}

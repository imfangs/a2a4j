package org.a2a4j.models.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;
import lombok.experimental.SuperBuilder;

import org.a2a4j.models.Task;

/**
 * JSON-RPC response for a task retrieval request in the A2A protocol.
 */
@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetTaskResponse extends JsonRpcResponse<Task> {

    @Override
    public Task getResult() {
        return convertResult(Task.class);
    }
}

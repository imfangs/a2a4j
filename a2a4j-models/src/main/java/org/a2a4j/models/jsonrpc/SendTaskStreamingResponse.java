package org.a2a4j.models.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;
import lombok.experimental.SuperBuilder;

import org.a2a4j.models.streaming.TaskStreamingResult;

/**
 * JSON-RPC response for a streaming task submission in the A2A protocol.
 */
@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendTaskStreamingResponse extends JsonRpcResponse<TaskStreamingResult> {

    @Override
    public TaskStreamingResult getResult() {
        return convertResult(TaskStreamingResult.class);
    }
}

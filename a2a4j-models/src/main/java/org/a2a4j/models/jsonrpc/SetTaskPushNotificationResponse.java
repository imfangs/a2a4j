package org.a2a4j.models.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;
import lombok.experimental.SuperBuilder;

import org.a2a4j.models.notification.TaskPushNotificationConfig;

/**
 * JSON-RPC response for a push notification configuration request in the A2A protocol.
 */
@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SetTaskPushNotificationResponse extends JsonRpcResponse<TaskPushNotificationConfig> {

    @Override
    public TaskPushNotificationConfig getResult() {
        return convertResult(TaskPushNotificationConfig.class);
    }
}

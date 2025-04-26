package org.a2a4j.models.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


/**
 * Base class for all JSON-RPC request objects in the A2A protocol.
 *
 * @param <T> the type of parameters for this request
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "method")
@JsonSubTypes({
    @JsonSubTypes.Type(value = SendTaskRequest.class, name = "tasks/send"),
    @JsonSubTypes.Type(value = GetTaskRequest.class, name = "tasks/get"),
    @JsonSubTypes.Type(value = CancelTaskRequest.class, name = "tasks/cancel"),
    @JsonSubTypes.Type(value = SetTaskPushNotificationRequest.class, name = "tasks/pushNotification/set"),
    @JsonSubTypes.Type(value = GetTaskPushNotificationRequest.class, name = "tasks/pushNotification/get"),
    @JsonSubTypes.Type(value = TaskResubscriptionRequest.class, name = "tasks/resubscribe"),
    @JsonSubTypes.Type(value = SendTaskStreamingRequest.class, name = "tasks/sendSubscribe"),
    @JsonSubTypes.Type(value = UnknownMethodRequest.class, name = "")
})
public abstract class JsonRpcRequest<T> {
    private String jsonrpc = "2.0";
    private String id;
    private T params;

    /**
     * Get the method name for this request.
     *
     * @return the method name
     */
    public abstract String getMethod();
}

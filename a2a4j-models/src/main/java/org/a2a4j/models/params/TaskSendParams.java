// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.models.params;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Builder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.a2a4j.models.Message;
import org.a2a4j.models.Task;
import org.a2a4j.models.notification.PushNotificationConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Parameters for sending a task in the A2A protocol.
 * <p>
 * These parameters are used for task creation or update requests.
 * </p>
 * 
 * @param <T> the type of metadata values
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskSendParams<T> {
    
    String id;
    String sessionId;
    Message message;
    Integer historyLength;
    PushNotificationConfig pushNotification;
    
    @Builder.Default
    Map<String, T> metadata = Collections.emptyMap();
    
    /**
     * Returns the message as a single-element list.
     * <p>
     * This is a convenience method for creating a task history.
     * </p>
     *
     * @return A list containing the message, or empty list if message is null.
     */
    public List<Message> getMessageAsList() {
        if (message == null) {
            return Collections.emptyList();
        }
        List<Message> messages = new ArrayList<>(1);
        messages.add(message);
        return messages;
    }
    
    /**
     * Creates a Task instance from these parameters.
     *
     * @return A new Task instance
     */
    @SuppressWarnings("unchecked")
    public Task toTask() {
        Map<String, Object> taskMetadata = (metadata instanceof Map)
            ? (Map<String, Object>) metadata 
            : Collections.emptyMap();
            
        return Task.builder()
                .id(id)
                .sessionId(sessionId)
                .history(getMessageAsList())
                .metadata(taskMetadata)
                .build();
    }
} 

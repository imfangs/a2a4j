// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.examples;

import org.a2a4j.models.Artifact;
import org.a2a4j.models.Message;
import org.a2a4j.models.Task;
import org.a2a4j.models.TaskState;
import org.a2a4j.models.TaskStatus;
import org.a2a4j.models.part.TextPart;
import org.a2a4j.server.TaskHandler;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A simple task handler that uses a function to process messages.
 */
public class BasicTaskHandler implements TaskHandler {

    private final Function<Message, String> messageProcessor;

    /**
     * Creates a new BasicTaskHandler with the given message processor.
     *
     * @param messageProcessor A function that takes a message and returns a string response
     */
    public BasicTaskHandler(Function<Message, String> messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    /**
     * Processes a task by applying the message processor to the last message in the task's history.
     *
     * @param task The task to process
     * @return The updated task with a response
     */
    @Override
    public Task handle(Task task) {
        // Get the latest message from the task's history
        List<Message> history = task.getHistory();
        if (history == null || history.isEmpty()) {
            throw new IllegalArgumentException("Task has no history");
        }
        
        Message latestMessage = history.get(history.size() - 1);
        
        // Process the message
        String response = messageProcessor.apply(latestMessage);
        
        // Create an artifact with the response
        Artifact responseArtifact = Artifact.builder()
                .name("agent-response")
                .parts(Collections.singletonList(
                    TextPart.builder().text(response).build()
                ))
                .build();
        
        // Update the task with the response
        TaskStatus completedStatus = TaskStatus.builder()
            .state(TaskState.COMPLETED)
            .build();

        return task
            .withStatus(completedStatus)
            .withArtifacts(Collections.singletonList(responseArtifact));
    }
    
    /**
     * Utility method to extract text content from a message.
     *
     * @param message The message to extract content from
     * @return The text content of the message
     */
    public static String getMessageContent(Message message) {
        if (message == null || message.getParts() == null || message.getParts().isEmpty()) {
            return "";
        }
        
        return message.getTextContent();
    }
} 
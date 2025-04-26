// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.server;

import org.a2a4j.models.Task;

/**
 * Interface for handling tasks in the A2A system.
 */
public interface TaskHandler {

    /**
     * Handles a task by processing its content and potentially modifying its state.
     *
     * @param task The task to handle
     * @return The processed task, potentially with updated status, history, and artifacts
     */
    Task handle(Task task);
}

// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.models.jsonrpc.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.a2a4j.models.jsonrpc.JsonRpcError;

/**
 * Error returned when a requested task cannot be found.
 * Code: -32001
 * Message: "Task not found"
 * Data: Must be null according to schema.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskNotFoundError extends JsonRpcError {

    private static final int ERROR_CODE = -32001;
    private static final String ERROR_MESSAGE = "Task not found";

    /**
     * Default constructor setting the fixed code, message, and null data.
     */
    public TaskNotFoundError() {
        super(ERROR_CODE, ERROR_MESSAGE, null);
    }

    // No constructor with data needed as schema requires data to be null.
}

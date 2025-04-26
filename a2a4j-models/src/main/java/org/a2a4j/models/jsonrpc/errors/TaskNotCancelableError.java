// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.models.jsonrpc.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.a2a4j.models.jsonrpc.JsonRpcError;

/**
 * Error returned when a task cannot be canceled.
 * Code: -32002
 * Message: "Task cannot be canceled"
 * Data: Must be null according to schema.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskNotCancelableError extends JsonRpcError {

    private static final int ERROR_CODE = -32002;
    private static final String ERROR_MESSAGE = "Task cannot be canceled";

    /**
     * Default constructor setting the fixed code, message, and null data.
     */
    public TaskNotCancelableError() {
        super(ERROR_CODE, ERROR_MESSAGE, null);
    }

    // No constructor with data needed as schema requires data to be null.
} 
// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.models.part;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents file data in the A2A protocol.
 * <p>
 * FileData contains information about a file, including its name, MIME type,
 * and the actual file content (either as a base64-encoded string or as a URI).
 * </p>
 * <p>
 * Constraint: Exactly one of 'bytes' or 'uri' must be provided.
 * </p>
 */
@Data
@Builder(buildMethodName = "buildInternal") // Make generated build method private
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileData {
    
    private String name;
    private String mimeType;
    private String bytes; // Base64 encoded
    private String uri;
    
    // Custom builder class to add validation
    public static class FileDataBuilder {
        // Lombok will generate fields and methods here

        public FileData build() {
            // Validation: Ensure exactly one of bytes or uri is non-null
            if ((bytes == null && uri == null) || (bytes != null && uri != null)) {
                throw new IllegalArgumentException("FileData requires exactly one of 'bytes' or 'uri' to be set.");
            }
            return buildInternal(); // Call the Lombok-generated build method
        }
    }

    @Override
    public String toString() {
        return "FileData{" +
                "name='" + name + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", bytes='" + (bytes != null ? "[BASE64 DATA]" : null) + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }
} 
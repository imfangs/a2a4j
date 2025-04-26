// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.models.notification;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.a2a4j.models.Authentication;

import java.util.Objects;

/**
 * Configuration for push notifications in the A2A protocol.
 * <p>
 * This class specifies the URL endpoint for push notifications,
 * an optional token, and optional authentication details.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PushNotificationConfig {

    private String url; // Required
    private String token; // Optional
    private Authentication authentication; // Optional

    @Override
    public String toString() {
        // Mask token in default toString for security
        return "PushNotificationConfig{" +
               "url='" + url + '\'' +
               ", token='" + (token != null ? "***" : null) + '\'' +
               ", authentication=" + authentication + // Authentication might need its own toString masking
               '}';
    }
} 
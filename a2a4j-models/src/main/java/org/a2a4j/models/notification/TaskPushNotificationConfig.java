// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.models.notification;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Associates a Push Notification Configuration with a specific Task ID.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskPushNotificationConfig {

    private String id; // Task ID (Required)
    private PushNotificationConfig pushNotificationConfig; // Push Config (Required)

    // Lombok handles constructors, getters, setters, equals, hashCode, toString, builder
} 
// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.server.notifications;

import org.a2a4j.models.Task;
import org.a2a4j.models.notification.PushNotificationConfig;

/**
 * Interface for publishing notifications about tasks to external systems.
 * Implementations of this interface handle the delivery of task information
 * to specified endpoints using the provided configuration.
 */
public interface NotificationPublisher {

    /**
     * Publishes a notification about a task to the specified endpoint.
     *
     * @param task The task to publish a notification about, containing its ID, status, and other details.
     * @param config The configuration for the push notification, including the target URL and authentication details.
     */
    void publish(Task task, PushNotificationConfig config);
}

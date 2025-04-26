// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.server.notifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.a2a4j.models.Task;
import org.a2a4j.models.notification.PushNotificationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Basic implementation of the NotificationPublisher interface.
 * This implementation uses the Java HTTP client to send notifications to configured endpoints.
 */
public class BasicNotificationPublisher implements NotificationPublisher {

    private static final Logger log = LoggerFactory.getLogger(BasicNotificationPublisher.class);
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new BasicNotificationPublisher with default HTTP client configuration.
     */
    public BasicNotificationPublisher() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Constructs a new BasicNotificationPublisher with a custom HTTP client.
     *
     * @param httpClient The HTTP client to use for sending notifications
     */
    public BasicNotificationPublisher(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * {@inheritDoc}
     *
     * This implementation sends an HTTP POST request to the endpoint specified in the configuration.
     * The request body contains a JSON representation of the task.
     * If an error occurs during the request, it is logged but no exception is thrown.
     */
    @Override
    public void publish(Task task, PushNotificationConfig config) {
        try {
            // Build the request body
            String jsonBody = objectMapper.writeValueAsString(task);

            // Build the HTTP request
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(config.getUrl()))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

            // Add authentication if provided
            if (config.getToken() != null && !config.getToken().isEmpty()) {
                requestBuilder.header("Authorization", "Bearer " + config.getToken());
            }

            // Send the request asynchronously
            CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(
                    requestBuilder.build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            // Log the response
            responseFuture.thenAccept(response -> {
                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    log.info("Successfully sent notification for task {} to {}",
                            task.getId(), config.getUrl());
                } else {
                    log.warn("Failed to send notification for task {} to {}. Status code: {}, Response: {}",
                            task.getId(), config.getUrl(), response.statusCode(), response.body());
                }
            }).exceptionally(e -> {
                log.error("Error sending notification for task {} to {}: {}",
                        task.getId(), config.getUrl(), e.getMessage(), e);
                return null;
            });
        } catch (Exception e) {
            log.error("Error preparing notification for task {} to {}: {}",
                    task.getId(), config.getUrl(), e.getMessage(), e);
        }
    }
}

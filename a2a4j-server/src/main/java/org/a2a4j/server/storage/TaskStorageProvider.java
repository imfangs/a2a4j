// SPDX-FileCopyrightText: 2025
//
// SPDX-License-Identifier: Apache-2.0
package org.a2a4j.server.storage;

/**
 * Factory interface for providing TaskStorage implementations.
 * This interface follows the factory pattern, allowing the system to abstract
 * the creation of TaskStorage instances and enabling different storage implementations
 * to be used interchangeably.
 *
 * Implementations of this interface should handle the creation and configuration
 * of TaskStorage instances according to their specific requirements.
 */
public interface TaskStorageProvider {

    /**
     * Provides an implementation of TaskStorage.
     *
     * @return A configured TaskStorage implementation ready for use or null.
     */
    TaskStorage provide();
}

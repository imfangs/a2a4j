//// SPDX-FileCopyrightText: 2025
////
//// SPDX-License-Identifier: Apache-2.0
//package org.a2a4j.server.storage.redis;
//
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.utility.DockerImageName;
//
///**
// * Base class for Redis tests that uses TestContainers to spin up a Redis instance.
// */
//@Testcontainers
//public abstract class TestBase {
//
//    private static final int REDIS_PORT = 6379;
//
//    /**
//     * Redis container for testing
//     */
//    @Container
//    protected static final GenericContainer<?> redisContainer =
//            new GenericContainer<>(DockerImageName.parse("redis:7.0.12"))
//                    .withExposedPorts(REDIS_PORT);
//
//    /**
//     * Creates a Redis connection factory for the test container.
//     *
//     * @return a LettuceConnectionFactory connected to the test container
//     */
//    protected LettuceConnectionFactory createRedisConnectionFactory() {
//        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
//        configuration.setHostName(redisContainer.getHost());
//        configuration.setPort(redisContainer.getMappedPort(REDIS_PORT));
//
//        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
//        factory.afterPropertiesSet();
//
//        return factory;
//    }
//}

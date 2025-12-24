package com.krzhi.utils.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
class RedisConfig {
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory?): RedisTemplate<String?, Any?> {
        val template = RedisTemplate<String?, Any?>()
        template.connectionFactory = connectionFactory

        // 使用 JSON 序列化
        val serializer = Jackson2JsonRedisSerializer(Any::class.java)
        template.valueSerializer = serializer
        template.hashValueSerializer = serializer

        // Key 使用 String 序列化
        template.keySerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()

        template.afterPropertiesSet()
        return template
    }

    @Bean
    fun cacheConfiguration(): RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair
                    .fromSerializer(GenericJackson2JsonRedisSerializer())
            )
    }
}
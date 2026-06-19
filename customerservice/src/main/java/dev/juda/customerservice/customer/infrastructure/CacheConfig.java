package dev.juda.customerservice.customer.infrastructure;

import tools.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager redisCacheManager(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {

        JacksonJsonRedisSerializer<Object> jsonSerializer =
                new JacksonJsonRedisSerializer<>(
                        objectMapper,
                        Object.class
                );

        RedisCacheConfiguration config =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))

                        .prefixCacheNameWith("customer::")

                        .disableCachingNullValues()

                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new StringRedisSerializer()
                                )
                        )

                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        jsonSerializer
                                )
                        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
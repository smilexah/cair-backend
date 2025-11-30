package ai.lab.cair.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        // Create Jackson2JsonRedisSerializer with custom ObjectMapper
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        
        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer)
                )
                .disableCachingNullValues();

        // Specific cache configurations
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Projects cache - 1 hour TTL
        cacheConfigurations.put("projects", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // Project by ID - 1 hour TTL
        cacheConfigurations.put("projectById", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // Project by slug - 1 hour TTL
        cacheConfigurations.put("projectBySlug", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // Team members cache - 1 hour TTL
        cacheConfigurations.put("teamMembers", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // Team member by ID - 1 hour TTL
        cacheConfigurations.put("teamMemberById", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // Translations cache - 2 hours TTL (more stable data)
        cacheConfigurations.put("translations", defaultConfig.entryTtl(Duration.ofHours(2)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}


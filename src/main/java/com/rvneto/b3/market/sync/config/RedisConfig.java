package com.rvneto.b3.market.sync.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Serializador de Chave
        template.setKeySerializer(new StringRedisSerializer());

        // Configuração do ObjectMapper para o Valor
        ObjectMapper om = new ObjectMapper();
        // Registra o suporte a Java 8 Date/Time
        om.registerModule(new JavaTimeModule());
        // Opcional: Salva no formato ISO-8601 legível em vez de array de números
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Serializador de Valor usando o ObjectMapper configurado
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(om, Object.class);
        template.setValueSerializer(serializer);

        return template;
    }
}
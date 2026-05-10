package com.rvneto.b3.market.sync.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rvneto.b3.market.sync.config.BrapiProperties;
import com.rvneto.b3.market.sync.dto.BrapiResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class MarketDataService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final BrapiProperties brapiProperties;
    private final ObjectMapper redisObjectMapper;

    private static final String CACHE_KEY_PREFIX = "market:price:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    public MarketDataService(RedisTemplate<String, Object> redisTemplate,
                             BrapiProperties brapiProperties,
                             @Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper) {
        this.redisTemplate = redisTemplate;
        this.brapiProperties = brapiProperties;
        this.redisObjectMapper = redisObjectMapper;
    }

    public void saveToCache(BrapiResultDTO quote) {
        String key = CACHE_KEY_PREFIX + quote.getTicker();
        try {
            redisTemplate.opsForValue().set(key, quote, CACHE_TTL);
            log.info("Ticker {} updated in cache: R$ {}", quote.getTicker(), quote.getRegularMarketPrice());
        } catch (Exception e) {
            log.error("Failed to save ticker {} to Redis: {}", quote.getTicker(), e.getMessage());
        }
    }

    public Optional<BrapiResultDTO> getFromCache(String ticker) {
        String key = CACHE_KEY_PREFIX + ticker.toUpperCase().trim();
        try {
            Object data = redisTemplate.opsForValue().get(key);
            if (Objects.isNull(data)) {
                log.warn("Ticker {} not found in cache", ticker);
                return Optional.empty();
            }
            BrapiResultDTO result = redisObjectMapper.convertValue(data, BrapiResultDTO.class);
            return Optional.of(result);
        } catch (Exception e) {
            log.error("Failed to read ticker {} from Redis: {}", ticker, e.getMessage());
            return Optional.empty();
        }
    }

    public List<BrapiResultDTO> getAllFromCache() {
        return brapiProperties.getTickers().stream()
                .map(this::getFromCache)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}

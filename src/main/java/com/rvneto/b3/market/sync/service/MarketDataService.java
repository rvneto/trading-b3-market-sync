package com.rvneto.b3.market.sync.service;

import com.rvneto.b3.market.sync.config.BrapiProperties;
import com.rvneto.b3.market.sync.dto.BrapiResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final BrapiProperties brapiProperties;
    private static final String CACHE_KEY_PREFIX = "market:price:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    public void saveToCache(BrapiResultDTO quote) {
        String key = CACHE_KEY_PREFIX + quote.getTicker();
        try {
            // TTL of 5 minutes — if sync stops, stale prices expire quickly
            // preventing the Matching Engine from using outdated data
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
            return Optional.of((BrapiResultDTO) data);
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

package com.rvneto.b3.market.sync.service;

import com.rvneto.b3.market.sync.dto.BrapiResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_KEY_PREFIX = "market:price:";

    public void saveToCache(BrapiResultDTO quote) {
        String key = CACHE_KEY_PREFIX + quote.getTicker();

        try {
            // Definimos um TTL de 5 minutos. Se o Sync parar, o dado expira
            // evitando que o Matching Engine use preços muito desatualizados.
            redisTemplate.opsForValue().set(key, quote, Duration.ofMinutes(100));

            log.info("Ticker {} atualizado no cache: R$ {}", quote.getTicker(), quote.getRegularMarketPrice());
        } catch (Exception e) {
            log.error("Erro ao salvar ticker {} no Redis: {}", quote.getTicker(), e.getMessage());
        }
    }

}
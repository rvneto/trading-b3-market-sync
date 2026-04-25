package com.rvneto.b3.market.sync.scheduler;

import com.rvneto.b3.market.sync.client.BrapiClient;
import com.rvneto.b3.market.sync.config.BrapiProperties;
import com.rvneto.b3.market.sync.dto.BrapiResponseDTO;
import com.rvneto.b3.market.sync.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarketSyncScheduler {

    private final BrapiClient brapiClient;
    private final BrapiProperties brapiProperties;
    private final MarketDataService marketDataService;

    @Scheduled(fixedRate = 1800000)
    public void sync() {

        if (!isMarketOpen()) {
            log.info("Sincronizacao abortada: Mercado Financeiro Fechado (Fora do horario comercial ou FDS).");
            return;
        }

        log.info("Iniciando rodada de sincronização para {} ativos", brapiProperties.getTickers().size());

        for (String ticker : brapiProperties.getTickers()) {
            try {
                log.debug("Buscando cotação para: {}", ticker);
                BrapiResponseDTO response = brapiClient.getQuote(ticker.trim(), brapiProperties.getToken());

                if (nonNull(response.getResults()) && !response.getResults().isEmpty()) {
                    marketDataService.saveToCache(response.getResults().getFirst());
                }

                // Um pequeno delay entre chamadas evita "Rate Limit" no plano free
                Thread.sleep(200);

            } catch (Exception e) {
                log.error("Falha ao sincronizar ticker {}: {}", ticker, e.getMessage());
            }
        }
    }

    private boolean isMarketOpen() {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek day = now.getDayOfWeek();
        int hour = now.getHour();

        // if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) { // TODO testando no domingo
        if (day == DayOfWeek.SATURDAY) {
            return false;
        }

        // return hour >= 10 && hour < 18;  // TODO testando antes das 10
        return hour >= 7 && hour < 23;
    }
}
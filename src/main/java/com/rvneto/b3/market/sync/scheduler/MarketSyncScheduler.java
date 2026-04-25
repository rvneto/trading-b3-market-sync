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
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarketSyncScheduler {

    private static final ZoneId SAO_PAULO = ZoneId.of("America/Sao_Paulo");

    private final BrapiClient brapiClient;
    private final BrapiProperties brapiProperties;
    private final MarketDataService marketDataService;

    @Scheduled(fixedRateString = "${app.sync.interval:1800000}")
    public void sync() {
        if (!isMarketOpen()) {
            log.info("Sync aborted: market is closed (outside trading hours or weekend).");
            return;
        }

        log.info("Starting sync round for {} assets", brapiProperties.getTickers().size());

        for (String ticker : brapiProperties.getTickers()) {
            try {
                log.debug("Fetching quote for: {}", ticker);
                BrapiResponseDTO response = brapiClient.getQuote(ticker.trim(), brapiProperties.getToken());

                if (nonNull(response.getResults()) && !response.getResults().isEmpty()) {
                    marketDataService.saveToCache(response.getResults().getFirst());
                }

                // Small delay between calls to avoid hitting rate limit on free plan
                Thread.sleep(200);

            } catch (Exception e) {
                log.error("Failed to sync ticker {}: {}", ticker, e.getMessage());
            }
        }
    }

    private boolean isMarketOpen() {
        ZonedDateTime now = ZonedDateTime.now(SAO_PAULO);
        DayOfWeek day = now.getDayOfWeek();
        int hour = now.getHour();

        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return false;
        }

        // B3 trading hours: 10:00 to 18:00 (Sao Paulo time)
        return hour >= 10 && hour < 18;
    }
}
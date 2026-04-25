package com.rvneto.b3.market.sync.controller;

import com.rvneto.b3.market.sync.dto.BrapiResultDTO;
import com.rvneto.b3.market.sync.service.MarketDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/quotes")
@RequiredArgsConstructor
@Tag(name = "Market Quotes", description = "Endpoints for querying cached market prices from Redis")
public class MarketSyncController {

    private final MarketDataService marketDataService;

    @GetMapping("/{ticker}")
    @Operation(summary = "Get cached price by ticker",
               description = "Returns the current cached market price for a given ticker. Returns 404 if not in cache.")
    public ResponseEntity<BrapiResultDTO> getByTicker(
            @Parameter(description = "Stock ticker symbol, e.g. PETR4") @PathVariable String ticker) {
        log.info("Fetching cached price for ticker: {}", ticker);
        return marketDataService.getFromCache(ticker)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "List all cached tickers",
               description = "Returns cached prices for all configured tickers")
    public ResponseEntity<List<BrapiResultDTO>> getAll() {
        log.info("Fetching all cached prices");
        return ResponseEntity.ok(marketDataService.getAllFromCache());
    }
}

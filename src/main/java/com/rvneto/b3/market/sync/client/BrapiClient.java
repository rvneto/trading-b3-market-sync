package com.rvneto.b3.market.sync.client;

import com.rvneto.b3.market.sync.dto.BrapiResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "brapi-client", url = "${app.brapi.url}")
public interface BrapiClient {

    @GetMapping("/quote/{ticker}")
    BrapiResponseDTO getQuote(
            @PathVariable("ticker") String ticker,
            @RequestParam("token") String token
    );
}
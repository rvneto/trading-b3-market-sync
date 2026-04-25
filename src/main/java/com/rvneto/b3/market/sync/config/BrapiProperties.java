package com.rvneto.b3.market.sync.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.brapi")
@Data
public class BrapiProperties {

    private String url;
    private String token;
    private List<String> tickers;
}


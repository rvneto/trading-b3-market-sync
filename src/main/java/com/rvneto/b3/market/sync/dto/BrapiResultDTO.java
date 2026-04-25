package com.rvneto.b3.market.sync.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.rvneto.b3.market.sync.deserializer.EpochToLocalDateTimeDeserializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrapiResultDTO {

    @JsonProperty("symbol")
    private String ticker;

    private BigDecimal regularMarketPrice;

    private Double regularMarketChangePercent;

    @JsonDeserialize(using = EpochToLocalDateTimeDeserializer.class)
    private LocalDateTime regularMarketTime;
}
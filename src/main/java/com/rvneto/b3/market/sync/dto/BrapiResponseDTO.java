package com.rvneto.b3.market.sync.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrapiResponseDTO {

    private List<BrapiResultDTO> results;

}


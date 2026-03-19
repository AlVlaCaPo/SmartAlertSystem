package com.smartalert.datacollector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoPriceResponse {
    private Map<String, Map<String, Double>> prices;
}

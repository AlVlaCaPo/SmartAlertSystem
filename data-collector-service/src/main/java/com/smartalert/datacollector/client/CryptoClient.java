package com.smartalert.datacollector.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "coingecko-client", url = "${api.crypto.url}", configuration = FeignConfig.class)
public interface CryptoClient {

    @GetMapping("/simple/price")
    Map<String, Map<String, Double>> getPrice(
            @RequestParam("ids") String ids,
            @RequestParam("vs_currencies") String vsCurrencies
    );
}

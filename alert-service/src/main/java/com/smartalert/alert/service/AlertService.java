package com.smartalert.alert.service;

import com.smartalert.alert.model.AlertNotification;
import com.smartalert.alert.producer.AlertProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertProducer alertProducer;

    // Umbrales fijos para el ejemplo (podrían venir de una BD en el futuro)
    private static final Double BTC_THRESHOLD = 60000.0;
    private static final Double ETH_THRESHOLD = 3000.0;

    public void processCryptoPrices(Map<String, Map<String, Double>> prices) {
        log.debug("Processing prices: {}", prices);

        // Procesar Bitcoin
        if (prices.containsKey("bitcoin")) {
            Double price = prices.get("bitcoin").get("usd");
            if (price > BTC_THRESHOLD) {
                createAndSendAlert("bitcoin", price, "¡ALERTA! Bitcoin ha superado los $" + BTC_THRESHOLD);
            }
        }

        // Procesar Ethereum
        if (prices.containsKey("ethereum")) {
            Double price = prices.get("ethereum").get("usd");
            if (price > ETH_THRESHOLD) {
                createAndSendAlert("ethereum", price, "¡ALERTA! Ethereum ha superado los $" + ETH_THRESHOLD);
            }
        }
    }

    private void createAndSendAlert(String asset, Double price, String message) {
        AlertNotification alert = AlertNotification.builder()
                .type("CRYPTO")
                .asset(asset)
                .currentPrice(price)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
        
        alertProducer.sendAlert(alert);
    }
}

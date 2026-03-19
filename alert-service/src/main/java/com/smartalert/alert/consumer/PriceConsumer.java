package com.smartalert.alert.consumer;

import com.smartalert.alert.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceConsumer {

    private final AlertService alertService;

    @KafkaListener(topics = "crypto-prices", groupId = "alert-group")
    public void consumePrices(Map<String, Map<String, Double>> prices) {
        log.info("Received prices from Kafka: {}", prices);
        alertService.processCryptoPrices(prices);
    }
}

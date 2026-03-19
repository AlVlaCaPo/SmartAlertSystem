package com.smartalert.datacollector.service;

import com.smartalert.datacollector.client.CryptoClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataCollectorService {

    private final CryptoClient cryptoClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String CRYPTO_TOPIC = "crypto-prices";

    @Retryable(
            value = {FeignException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void collectCryptoPrices() {
        log.info("Fetching crypto prices from CoinGecko...");
        Map<String, Map<String, Double>> response = cryptoClient.getPrice("bitcoin,ethereum", "usd");
        
        if (response != null && !response.isEmpty()) {
            log.info("Crypto prices fetched: {}", response);
            kafkaTemplate.send(CRYPTO_TOPIC, response);
            log.info("Sent crypto prices to Kafka topic: {}", CRYPTO_TOPIC);
        } else {
            log.warn("API response was empty. No data sent to Kafka.");
        }
    }

    @Recover
    public void recover(FeignException e) {
        log.error("Failed to fetch crypto prices after multiple retries. API might be down or rate-limited. Error: {}", e.getMessage());
    }

    @Recover
    public void recover(Exception e) {
        log.error("An unexpected error occurred during data collection: {}", e.getMessage());
    }
}

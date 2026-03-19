package com.smartalert.datacollector.scheduler;

import com.smartalert.datacollector.service.DataCollectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceScheduler {

    private final DataCollectorService dataCollectorService;

    // Ejecutar cada 1 minuto (60000 ms)
    @Scheduled(fixedRate = 60000)
    public void scheduleCryptoPriceCollection() {
        log.info("Running scheduled crypto price collection...");
        dataCollectorService.collectCryptoPrices();
    }
}

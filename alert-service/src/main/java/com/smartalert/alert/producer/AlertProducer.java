package com.smartalert.alert.producer;

import com.smartalert.alert.model.AlertNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertProducer {

    private final KafkaTemplate<String, AlertNotification> kafkaTemplate;
    private static final String ALERTS_TOPIC = "alerts-notifications";

    public void sendAlert(AlertNotification alert) {
        log.info("Sending alert to Kafka: {}", alert);
        kafkaTemplate.send(ALERTS_TOPIC, alert.getAsset(), alert);
    }
}

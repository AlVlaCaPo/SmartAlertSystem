package com.smartalert.alert.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertNotification {
    private String type; // e.g., "CRYPTO"
    private String asset; // e.g., "bitcoin"
    private Double currentPrice;
    private String message;
    private long timestamp;
}

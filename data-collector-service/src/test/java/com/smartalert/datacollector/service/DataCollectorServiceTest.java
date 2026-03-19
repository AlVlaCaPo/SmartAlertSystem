package com.smartalert.datacollector.service;

import com.smartalert.datacollector.client.CryptoClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataCollectorServiceTest {

    @Mock
    private CryptoClient cryptoClient;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private DataCollectorService dataCollectorService;

    private Map<String, Map<String, Double>> mockApiResponse;

    @BeforeEach
    void setUp() {
        mockApiResponse = new HashMap<>();
        Map<String, Double> btcPrice = new HashMap<>();
        btcPrice.put("usd", 70000.0);
        mockApiResponse.put("bitcoin", btcPrice);

        Map<String, Double> ethPrice = new HashMap<>();
        ethPrice.put("usd", 2500.0);
        mockApiResponse.put("ethereum", ethPrice);
    }

    @Test
    void collectCryptoPrices_Success() {
        // Simular respuesta del API
        when(cryptoClient.getPrice("bitcoin,ethereum", "usd")).thenReturn(mockApiResponse);

        // Ejecutar lógica
        dataCollectorService.collectCryptoPrices();

        // Verificar que se llamó al API
        verify(cryptoClient, times(1)).getPrice("bitcoin,ethereum", "usd");

        // Verificar que se envió a Kafka el tópico correcto y los datos correctos
        verify(kafkaTemplate, times(1)).send(eq("crypto-prices"), eq(mockApiResponse));
    }

    @Test
    void collectCryptoPrices_ApiError_ShouldThrowException() {
        // Simular error en el API
        when(cryptoClient.getPrice(anyString(), anyString())).thenThrow(new RuntimeException("API error"));

        // Ejecutar y verificar que lanza excepción (el retry lo manejaría Spring en runtime)
        assertThrows(RuntimeException.class, () -> dataCollectorService.collectCryptoPrices());

        // Verificar que NO se envió nada a Kafka
        verify(kafkaTemplate, never()).send(anyString(), any());
    }
}

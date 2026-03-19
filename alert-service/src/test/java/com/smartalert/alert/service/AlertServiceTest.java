package com.smartalert.alert.service;

import com.smartalert.alert.model.AlertNotification;
import com.smartalert.alert.producer.AlertProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertProducer alertProducer;

    @InjectMocks
    private AlertService alertService;

    private Map<String, Map<String, Double>> highPrices;
    private Map<String, Map<String, Double>> lowPrices;

    @BeforeEach
    void setUp() {
        highPrices = new HashMap<>();
        Map<String, Double> btcPrice = new HashMap<>();
        btcPrice.put("usd", 70000.0); // > 60000
        highPrices.put("bitcoin", btcPrice);

        Map<String, Double> ethPrice = new HashMap<>();
        ethPrice.put("usd", 4000.0); // > 3000
        highPrices.put("ethereum", ethPrice);

        lowPrices = new HashMap<>();
        Map<String, Double> lowBtc = new HashMap<>();
        lowBtc.put("usd", 50000.0);
        lowPrices.put("bitcoin", lowBtc);
    }

    @Test
    void processCryptoPrices_ShouldSendAlertWhenThresholdExceeded() {
        // Ejecutar lógica
        alertService.processCryptoPrices(highPrices);

        // Capturar la alerta enviada
        ArgumentCaptor<AlertNotification> alertCaptor = ArgumentCaptor.forClass(AlertNotification.class);
        
        // Debería haber enviado 2 alertas (BTC y ETH)
        verify(alertProducer, times(2)).sendAlert(alertCaptor.capture());

        AlertNotification btcAlert = alertCaptor.getAllValues().get(0);
        assertEquals("bitcoin", btcAlert.getAsset());
        assertEquals(70000.0, btcAlert.getCurrentPrice());
        assertEquals("CRYPTO", btcAlert.getType());

        AlertNotification ethAlert = alertCaptor.getAllValues().get(1);
        assertEquals("ethereum", ethAlert.getAsset());
        assertEquals(4000.0, ethAlert.getCurrentPrice());
    }

    @Test
    void processCryptoPrices_ShouldNotSendAlertWhenBelowThreshold() {
        // Ejecutar lógica
        alertService.processCryptoPrices(lowPrices);

        // Verificar que NO se envió ninguna alerta
        verify(alertProducer, never()).sendAlert(any());
    }
}

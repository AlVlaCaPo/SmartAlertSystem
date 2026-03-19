package com.smartalert.datacollector.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 400:
                log.error("Error 400: Petición inválida al API externa.");
                break;
            case 401:
                log.error("Error 401: API Key inválida o no proporcionada.");
                break;
            case 403:
                log.error("Error 403: Acceso prohibido al recurso.");
                break;
            case 429:
                log.warn("Error 429: Límite de peticiones alcanzado (Rate Limit). Esperando para reintentar...");
                break;
            case 500:
                log.error("Error 500: Error interno en el servidor de la API externa.");
                break;
            default:
                log.error("Error genérico en API externa: Código {}", response.status());
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }
}

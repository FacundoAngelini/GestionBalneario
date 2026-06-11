package com.Gestion.MiBalnearioGestion.Pagos;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
@Service
public class MercadoPagoService {

    @Value("${mp.accesstoken}")
    private String accessToken;

    @Value("${mp.backurl.success}")
    private String backUrlSuccess;

    @Value("${mp.backurl.failure}")
    private String backUrlFailure;

    @Value("${mp.webhook}")
    private String notificationUrl;

    public String crearPreferenciaPago(UUID publicIdPago, double monto, String descripcionItem) {
        try {
            String url = "https://api.mercadopago.com/checkout/preferences";
            RestTemplate clienteHttp = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            Map<String, Object> item = new HashMap<>();
            item.put("id", publicIdPago.toString());
            item.put("title", descripcionItem);
            item.put("quantity", 1);
            item.put("unit_price", monto);
            item.put("currency_id", "ARS");


            List<Map<String, Object>> items = new ArrayList<>();
            items.add(item);

            Map<String, String> backUrls = new HashMap<>();
            backUrls.put("success", backUrlSuccess);
            backUrls.put("failure", backUrlFailure);
            backUrls.put("pending", backUrlFailure);

            Map<String, Object> body = new HashMap<>();
            body.put("items", items);
            body.put("back_urls", backUrls);
            body.put("external_reference", publicIdPago.toString());
            body.put("notification_url", notificationUrl);
            body.put("auto_return", "approved");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = clienteHttp.postForEntity(url, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("init_point");
            } else {
                throw new RuntimeException("Respuesta inesperada de Mercado Pago");
            }

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("ERROR DE LA API DE MERCADO PAGO: " + e.getResponseBodyAsString());
            throw new RuntimeException("Mercado Pago rechazó la solicitud: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("ERROR INTERNO EN EL PROCESAMIENTO:");
            e.printStackTrace();
            throw new RuntimeException("Error al conectar con Mercado Pago: " + e.getMessage(), e);
        }
    }
}
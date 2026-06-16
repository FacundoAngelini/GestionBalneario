package com.Gestion.MiBalnearioGestion.Pagos.Controladores;

import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Pago.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/api/v1/public/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final PagoService pagoService;

    @PostMapping("/mercadopago")
    public ResponseEntity<Void> recibirNotificacionMP(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "topic", required = false) String topic,
            @RequestParam(value = "id", required = false) String idQueryParam,
            @RequestBody(required = false) Map<String, Object> data) {

        System.out.println("Webhook MP recibido -> type=" + type
                + " topic=" + topic
                + " idQueryParam=" + idQueryParam
                + " body=" + data);

        String paymentId = null;

        boolean esPagoPorBody = data != null
                && ("payment".equals(data.get("type"))
                || (data.get("action") != null && data.get("action").toString().startsWith("payment")));

        boolean esPagoPorQuery = "payment".equals(type) || "payment".equals(topic);

        if (esPagoPorBody) {
            Object dataObj = data.get("data");
            if (dataObj instanceof Map<?, ?> dataMap && dataMap.get("id") != null) {
                paymentId = dataMap.get("id").toString();
            }
        } else if (esPagoPorQuery && idQueryParam != null) {
            paymentId = idQueryParam;
        }

        if (paymentId != null) {
            try {
                pagoService.procesarNotificacionPago(paymentId);
            } catch (Exception e) {
                System.err.println("Error procesando notificación de pago " + paymentId + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok().build();
    }
}

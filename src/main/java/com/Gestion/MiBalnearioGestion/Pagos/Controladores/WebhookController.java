package com.Gestion.MiBalnearioGestion.Pagos.Controladores;

import com.Gestion.MiBalnearioGestion.Pagos.Servicios.PagoService;
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
            @RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "type", required = false) String type,
            @RequestBody Map<String, Object> data) {

        if ("payment.created".equals(action) || "payment".equals(type)) {
            Map<String, Object> dataObj = (Map<String, Object>) data.get("data");
            if (dataObj != null && dataObj.get("id") != null) {
                String paymentIdMP = dataObj.get("id").toString();
                pagoService.procesarNotificacionPago(paymentIdMP);
            }
        }

        return ResponseEntity.ok().build();
    }
}
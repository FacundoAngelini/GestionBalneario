package com.Gestion.MiBalnearioGestion.Pagos.Controladores;

import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Pago.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/api/v1/public/webhooks")
@RequiredArgsConstructor
@Tag(name = "Webhooks Públicos", description = "Endpoints accesibles sin autenticación para la recepción de notificaciones asincrónicas de pasarelas de pago externas")
public class WebhookController {

    private final PagoService pagoService;

    @PostMapping("/mercadopago")
    @Operation(
            summary = "Recibir notificación asincrónica de Mercado Pago (Webhook / IPN)",
            description = "Endpoint público que actúa como listener para Mercado Pago. Procesa tanto eventos Webhook modernos (vía JSON body) como eventos IPN tradicionales (vía Query Params). Filtra automáticamente los eventos que no pertenezcan a la categoría 'payment'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación recibida y aceptada correctamente (Mercado Pago requiere un estado 200/OK para no reintentar el envío)", content = @Content),
            @ApiResponse(responseCode = "400", description = "Estructura de petición malformada", content = @Content)
    })
    public ResponseEntity<Void> recibirNotificacionMP(
            @Parameter(description = "Tipo de evento enviado por las IPN clásicas de MP. Ejemplo: 'payment'", example = "payment")
            @RequestParam(value = "type", required = false) String type,

            @Parameter(description = "Tópico de la notificación alternativa en IPN antiguas. Ejemplo: 'payment'", example = "payment")
            @RequestParam(value = "topic", required = false) String topic,

            @Parameter(description = "ID del recurso transaccionado enviado por la URL (IPN)", example = "9988776655")
            @RequestParam(value = "id", required = false) String idQueryParam,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Cuerpo de la notificación Webhook en formato JSON. Estructura dinámica de Mercado Pago que contiene la acción y el mapa de datos internos ('data.id').",
                    content = @Content(schema = @Schema(example = "{\"action\":\"payment.created\",\"api_version\":\"v1\",\"data\":{\"id\":\"1234567890\"},\"date_created\":\"2026-06-16T02:15:00Z\",\"id\":11223344,\"live_mode\":true,\"type\":\"payment\",\"user_id\":\"12345\"}"))
            )
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
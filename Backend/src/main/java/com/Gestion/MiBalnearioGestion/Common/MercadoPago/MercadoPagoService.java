package com.Gestion.MiBalnearioGestion.Common.MercadoPago;
import org.springframework.beans.factory.annotation.Value;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.preference.Preference;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
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

    @jakarta.annotation.PostConstruct
    public void init() {
        com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken);
    }

    public record PreferenciaMP(String preferenceId, String initPoint) {}

    public PreferenciaMP crearPreferenciaPago(UUID publicIdPago, double monto, String descripcionItem) {
        try {

            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .id(publicIdPago.toString())
                    .title(descripcionItem)
                    .quantity(1)
                    .unitPrice(BigDecimal.valueOf(monto))
                    .currencyId("ARS")
                    .build();

            List<PreferenceItemRequest> items = List.of(item);

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(backUrlSuccess)
                    .failure(backUrlFailure)
                    .pending(backUrlFailure)
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .externalReference(publicIdPago.toString())
                    .notificationUrl(notificationUrl)
                    .autoReturn("approved")
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            return new PreferenciaMP(preference.getId(), preference.getInitPoint());

        } catch (com.mercadopago.exceptions.MPApiException e) {
            System.err.println("CODIGO DE ERROR MP: " + e.getApiResponse().getStatusCode());
            System.err.println("DETALLE DE LA API DE MP: " + e.getApiResponse().getContent());
            throw new RuntimeException("Mercado Pago rechazó los datos: " + e.getApiResponse().getContent(), e);
        } catch (Exception e) {
            System.err.println("ERROR EN LA GENERACIÓN DE PREFERENCIA MERCADO PAGO:");
            e.printStackTrace();
            throw new RuntimeException("Error al conectar con Mercado Pago mediante SDK: " + e.getMessage(), e);
        }
    }

    public void invalidarPreferenciaPago(String preferenceId, LocalDateTime fechaCreacion) {
        if (preferenceId == null || preferenceId.isBlank()) return;

        try {
            PreferenceClient client = new PreferenceClient();

            OffsetDateTime fechaExpiracionPasada = fechaCreacion
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime()
                    .minusMinutes(5);

            PreferenceRequest updateRequest = PreferenceRequest.builder()
                    .expires(true)
                    .expirationDateTo(fechaExpiracionPasada)
                    .build();

            client.update(preferenceId, updateRequest);
            System.out.println("Preferencia de Mercado Pago " + preferenceId + " invalidada (Seteada para expirar en el pasado).");

        } catch (Exception e) {
            System.err.println("No se pudo invalidar la preferencia " + preferenceId + " en MP: " + e.getMessage());
        }
    }
}
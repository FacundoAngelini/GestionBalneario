package com.Gestion.MiBalnearioGestion.Pagos;
import org.springframework.beans.factory.annotation.Value;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.preference.Preference;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    public String crearPreferenciaPago(UUID publicIdPago, double monto, String descripcionItem) {
        try {
            // seatamos el token de mp
            MercadoPagoConfig.setAccessToken(accessToken);

            // se crea el item del balneario
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .id(publicIdPago.toString())
                    .title(descripcionItem)
                    .quantity(1)
                    .unitPrice(new BigDecimal(monto))
                    .currencyId("ARS")
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(item);

            // creamos los back url de forma nativa
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(backUrlSuccess)
                    .failure(backUrlFailure)
                    .pending(backUrlFailure)
                    .build();

            //armamos la peticion
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .externalReference(publicIdPago.toString()) // hilo conductor para webhook
                    .notificationUrl(notificationUrl)           //link de ngrok público
                    .autoReturn("approved")                      // retorno automatico obligatorio
                    .build();

            // usamos el cliente oficial de mp
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            // retornamos el link de pago
            return preference.getInitPoint();

        } catch (com.mercadopago.exceptions.MPApiException e) {
            // esto por ahora esta para ayudarnos si se rompe algo
            System.err.println("CODIGO DE ERROR MP: " + e.getApiResponse().getStatusCode());
            System.err.println("DETALLE DE LA API DE MP: " + e.getApiResponse().getContent());
            throw new RuntimeException("Mercado Pago rechazó los datos: " + e.getApiResponse().getContent(), e);
        } catch (Exception e) {
            System.err.println("ERROR EN LA GENERACIÓN DE PREFERENCIA MERCADO PAGO:");
            e.printStackTrace();
            throw new RuntimeException("Error al conectar con Mercado Pago mediante SDK: " + e.getMessage(), e);
        }
    }
}
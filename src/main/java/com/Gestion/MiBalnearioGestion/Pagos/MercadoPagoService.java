package com.Gestion.MiBalnearioGestion.Pagos;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.MercadoPagoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MercadoPagoService {

    //registrado en mercadoPago haceme acordar que te agregue de colaborador
    @Value("${mp.accesstoken}")
    private String accessToken;


    //Problemas con MercadoPagoConfig(clase de la dependencia) no muestra metodos.
    //MercadoPagoConfig.setAccessToken(accessToken);



}

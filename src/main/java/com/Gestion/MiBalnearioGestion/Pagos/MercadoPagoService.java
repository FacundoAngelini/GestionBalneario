package com.Gestion.MiBalnearioGestion.Pagos;
import com.mercadopago.MercadoPagoConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MercadoPagoService {

    @Value("${mp.accesstoken}")
    private String accessToken;




}

package com.Gestion.MiBalnearioGestion.Common.Configuracion.OpenApi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gestion Integral de un Balneario ")
                        .version("1.0.0")
                        .description("API REST para la gestión integral del Balneario. " +
                                "Permite administrar reservas, pedidos, pagos con Mercado Pago, " +
                                "clientes y empleados. Autenticación mediante JWT."));

    }
}

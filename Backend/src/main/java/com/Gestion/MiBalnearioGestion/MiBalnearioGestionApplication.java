package com.Gestion.MiBalnearioGestion;

import com.mercadopago.MercadoPagoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MiBalnearioGestionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiBalnearioGestionApplication.class, args);
	}

}
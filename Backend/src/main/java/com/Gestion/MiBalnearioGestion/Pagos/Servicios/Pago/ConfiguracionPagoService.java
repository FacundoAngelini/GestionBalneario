package com.Gestion.MiBalnearioGestion.Pagos.Servicios.Pago;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.ConfiguracionPagoDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.ConfiguracionPagoEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.IConfiguracionPagoRepository;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.IConfiguracionPagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfiguracionPagoService {

    private static final double MAX_DESCUENTO = 50.0; // tope de seguridad contra errores de tipeo

    private final IConfiguracionPagoRepository repositorio;

    /** Devuelve la configuración; si todavía no existe, la crea sin descuento. */
    @Transactional
    public ConfiguracionPagoEntity obtener() {
        return repositorio.findAll().stream().findFirst()
                .orElseGet(() -> repositorio.save(ConfiguracionPagoEntity.builder().descuentoEfectivo(0.0).build()));
    }

    @Transactional
    public ConfiguracionPagoDTO actualizarDescuentoEfectivo(double porcentaje) {
        if (porcentaje < 0 || porcentaje > MAX_DESCUENTO) {
            throw new DatosInvalidoException("El descuento debe estar entre 0 y " + MAX_DESCUENTO + " %", "ConfiguracionPagoEntity");
        }
        ConfiguracionPagoEntity config = obtener();
        config.setDescuentoEfectivo(porcentaje);
        return new ConfiguracionPagoDTO(config.getDescuentoEfectivo());
    }
}
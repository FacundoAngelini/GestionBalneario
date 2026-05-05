package com.Gestion.MiBalnearioGestion.Pagos;

import com.Gestion.MiBalnearioGestion.Reservas.ReservaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="pago_reserva")
public class PagoReservaEntity extends PagoEntity {
    @OneToOne
    @JoinColumn(name="reserva_id", nullable = false)
    private ReservaEntity reserva;
}

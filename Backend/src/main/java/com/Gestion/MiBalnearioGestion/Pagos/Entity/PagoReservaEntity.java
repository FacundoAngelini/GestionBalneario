package com.Gestion.MiBalnearioGestion.Pagos.Entity;

import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name="pago_reserva")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PagoReservaEntity extends PagoEntity {

    @OneToOne
    @JoinColumn(name="reserva_id", nullable = false)
    @JsonBackReference
    private ReservaEntity reserva;
    @Column(name = "preference_id_mp")
    private String preferenceIdMp;
}
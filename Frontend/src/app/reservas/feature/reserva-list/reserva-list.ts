import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Reserva } from '../../data-access/reserva-model';
import { ReservaService } from '../../data-access/reserva-service';

@Component({
  imports: [DatePipe],
  selector: 'app-reserva-list',
  styleUrl: './reserva-list.css',
  templateUrl: './reserva-list.html',
})
export class ReservaList implements OnInit {
  private reservaService = inject(ReservaService);

  reservas = signal<Reserva[]>([]);
  cargando = signal<boolean>(false);
  errorMsg = signal<string>('');
  exitoMsg = signal<string>('');
  // publicId de la reserva que se está dando de baja
  cancelandoId = signal<string | null>(null);

  ngOnInit() {
    this.cargarReservas();
  }

  cargarReservas() {
    this.cargando.set(true);
    this.errorMsg.set('');
    this.reservaService.listar({ estado: 'CONFIRMADA' }).subscribe({
      next: (data) => {
        this.reservas.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        this.errorMsg.set(
          err?.status === 403
            ? 'No tenés permisos para ver las reservas'
            : 'No se pudieron cargar las reservas',
        );
        this.cargando.set(false);
      },
    });
  }

  darDeBaja(reserva: Reserva) {
    if (!reserva.publicId) return;
    if (!confirm('¿Seguro que querés dar de baja esta reserva?')) return;

    this.errorMsg.set('');
    this.exitoMsg.set('');
    this.cancelandoId.set(reserva.publicId);

    this.reservaService.cancelarAdministrativo(reserva.publicId).subscribe({
      next: () => {
        this.reservas.update((lista) => lista.filter((r) => r.publicId !== reserva.publicId));
        this.exitoMsg.set('Reserva dada de baja correctamente');
        this.cancelandoId.set(null);
      },
      error: (err) => {
        this.errorMsg.set(
          err?.status === 404
            ? 'La reserva no existe'
            : err?.status === 403
              ? 'No tenés permisos para dar de baja reservas'
              : 'No se pudo dar de baja la reserva',
        );
        this.cancelandoId.set(null);
      },
    });
  }
}

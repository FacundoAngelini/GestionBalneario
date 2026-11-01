import { Component, effect, inject, input, signal, untracked } from '@angular/core';
import { formatoFechaHora } from '../../../../core/utils/fechas';
import { ETIQUETA_ACCION, EventoReserva } from '../../../data-access/reserva-model';
import { ReservaService } from '../../../data-access/reserva-service';

/** Desplegable con las acciones hechas sobre una reserva. Se pide al abrirlo, no antes. */
@Component({
  selector: 'app-historial-reserva',
  templateUrl: './historial-reservas.html',
  styleUrl: './historial-reservas.css',
})
export class HistorialReserva {
  private readonly reservaService = inject(ReservaService);

  readonly reservaId = input.required<string>();
  /** Cambia cuando la reserva cambió (cobro, estado...): el historial se vuelve a pedir. */
  readonly huella = input('');

  protected readonly etiqueta = ETIQUETA_ACCION;
  protected readonly formatoFechaHora = formatoFechaHora;

  protected readonly eventos = signal<EventoReserva[] | null>(null);
  protected readonly error = signal('');
  private readonly abierto = signal(false);

  constructor() {
    effect(() => {
      this.huella();
      untracked(() => {
        this.eventos.set(null);
        if (this.abierto()) this.cargar();
      });
    });
  }

  protected alAlternar(evento: Event): void {
    const abierto = (evento.target as HTMLDetailsElement).open;
    this.abierto.set(abierto);
    if (abierto && this.eventos() === null) this.cargar();
  }

  private cargar(): void {
    this.error.set('');
    this.reservaService.historial(this.reservaId()).subscribe({
      next: (lista) => this.eventos.set(lista),
      error: () => this.error.set('No se pudo cargar el historial.'),
    });
  }
}

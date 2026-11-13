import { Component, effect, inject, input, signal, untracked } from '@angular/core';
import { formatoFechaHora } from '../../../core/utils/fechas';
import { ETIQUETA_ACCION_CLIENTE, EventoCliente } from '../../data-access/cliente-model';
import { ClienteService } from '../../data-access/cliente-service';

@Component({
  selector: 'app-historial-cliente',
  templateUrl: './historial-cliente.html',
  styleUrl: './historial-cliente.css',
})
export class HistorialCliente {
  private readonly clienteService = inject(ClienteService);

  readonly clienteId = input.required<string>();
  readonly huella = input('');

  protected readonly etiqueta = ETIQUETA_ACCION_CLIENTE;
  protected readonly formatoFechaHora = formatoFechaHora;

  protected readonly eventos = signal<EventoCliente[] | null>(null);
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
    this.clienteService.historial(this.clienteId()).subscribe({
      next: (lista) => this.eventos.set(lista),
      error: () => this.error.set('No se pudo cargar el historial.'),
    });
  }
}
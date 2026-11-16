import { Component, effect, inject, input, signal, untracked } from '@angular/core';
import { formatoFechaHora } from '../../../core/utils/fechas';
import {
  ESTADOS_EMPLEADO,
  ETIQUETA_ACCION_EMPLEADO,
  ETIQUETA_ESTADO_EMPLEADO,
  EstadoEmpleado,
  EventoEmpleado,
} from '../../data-access/empleado-model';
import { EmpleadoService } from '../../data-access/empleado-service';

@Component({
  selector: 'app-historial-empleado',
  templateUrl: './historial-empleado.html',
  styleUrl: './historial-empleado.css',
})
export class HistorialEmpleado {
  private readonly empleadoService = inject(EmpleadoService);

  readonly empleadoId = input.required<string>();
  /** Cambia cuando la ficha cambió (estado, datos...): el historial se vuelve a pedir. */
  readonly huella = input('');

  protected readonly etiqueta = ETIQUETA_ACCION_EMPLEADO;
  protected readonly formatoFechaHora = formatoFechaHora;

  protected readonly eventos = signal<EventoEmpleado[] | null>(null);
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

  // "ACTIVO → SUSPENDIDO" se muestra como "Activo → Suspendido"
  protected detalle(texto: string): string {
    return texto.replace(new RegExp(`\\b(${ESTADOS_EMPLEADO.join('|')})\\b`, 'g'), (estado) =>
      ETIQUETA_ESTADO_EMPLEADO[estado as EstadoEmpleado],
    );
  }

  private cargar(): void {
    this.error.set('');
    this.empleadoService.historial(this.empleadoId()).subscribe({
      next: (lista) => this.eventos.set(lista),
      error: () => this.error.set('No se pudo cargar el historial.'),
    });
  }
}

import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { form, FormField } from '@angular/forms/signals';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MAX_MOTIVO, MIN_MOTIVO } from '../../../empleados/data-access/empleado-model';
import { mensajeAmigable } from '../../../core/utils/errores';
import { formatoFecha, formatoRango } from '../../../core/utils/fechas';
import { formatoPrecio } from '../../../core/utils/moneda';
import { ETIQUETA_MODALIDAD } from '../../../recursos/data-access/recurso-model';
import { CLASE_ESTADO, ETIQUETA_ESTADO_RESERVA } from '../../../reservas/data-access/reserva-model';
import { ClienteFicha as Ficha } from '../../data-access/cliente-model';
import { ClienteService } from '../../data-access/cliente-service';
import { HistorialCliente } from '../historial-cliente/historial-cliente';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Dialogos } from '../../../shared/mensaje-dialog/dialogos';

type Panel = 'cerrado' | 'baja' | 'reactivar';

@Component({
  selector: 'app-cliente-ficha',
  imports: [FormField, RouterLink, HistorialCliente],
  templateUrl: './cliente-ficha.html',
  styleUrl: './cliente-ficha.css',
})
export class ClienteFicha {
  private readonly clienteService = inject(ClienteService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly id = inject(ActivatedRoute).snapshot.paramMap.get('id') ?? '';

  protected readonly etiquetaEstado = ETIQUETA_ESTADO_RESERVA;
  protected readonly claseEstado = CLASE_ESTADO;
  protected readonly etiquetaModalidad = ETIQUETA_MODALIDAD;
  protected readonly formatoFecha = formatoFecha;
  protected readonly formatoRango = formatoRango;
  protected readonly formatoPrecio = formatoPrecio;
  protected readonly minMotivo = MIN_MOTIVO;
  protected readonly maxMotivo = MAX_MOTIVO;

  protected readonly cliente = signal<Ficha | null>(null);
  protected readonly cargando = signal(true);
  protected readonly error = signal('');

  // ---- Baja y reactivación ----
  protected readonly panel = signal<Panel>('cerrado');
  protected readonly modelo = signal({ motivo: '' });
  protected readonly f = form(this.modelo);
  protected readonly guardando = signal(false);
  protected readonly errorEstado = signal('');

  private readonly router = inject(Router);
  private readonly dialogos = inject(Dialogos);

  // La baja se bloquea mientras tenga reservas pendientes o confirmadas (el servidor lo controla también)
  protected readonly tieneReservasActivas = computed(() => (this.cliente()?.reservasActivas.length ?? 0) > 0);

  protected readonly errorMotivo = computed(() => {
    const largo = this.modelo().motivo.trim().length;
    if (largo === 0) return `Indicá el motivo (al menos ${MIN_MOTIVO} caracteres)`;
    if (largo < MIN_MOTIVO) return `Al menos ${MIN_MOTIVO} caracteres`;
    return largo > MAX_MOTIVO ? `Máximo ${MAX_MOTIVO} caracteres` : null;
  });
  protected readonly valido = computed(() => this.errorMotivo() === null);

  // Cambia con cada modificación, para que el historial se vuelva a pedir
  protected readonly huella = computed(() => {
    const c = this.cliente();
    return `${c?.estado}|${c?.email}|${c?.telefono}|${c?.dni}`;
  });

  constructor() {
    this.cargar();
  }

  private cargar(): void {
    this.cargando.set(true);
    this.error.set('');
    this.clienteService.ficha(this.id).subscribe({
      next: (c) => {
        this.cliente.set(c);
        this.cargando.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.cargando.set(false);
        this.error.set(
          e.status === 404
            ? 'No se encontró al cliente.'
            : mensajeAmigable(e, 'No se pudo cargar la ficha. Intentá de nuevo.'),
        );
      },
    });
  }

  protected titulo(r: { recursos: string[] }): string {
    return r.recursos.length > 0 ? r.recursos.join(', ') : 'Reserva';
  }

  protected async abrirPanel(tipo: Exclude<Panel, 'cerrado'>): Promise<void> {
    if (tipo === 'baja' && this.tieneReservasActivas()) {
      const c = this.cliente();
      const n = c?.reservasActivas.length ?? 0;
      const eleccion = await this.dialogos.elegir(
        'advertencia',
        'No se puede dar de baja',
        `El cliente tiene ${n} ${n === 1 ? 'reserva activa' : 'reservas activas'} (pendientes o confirmadas).\n\n` +
          'Cancelalas antes de dar de baja la cuenta.',
        'Ir a las reservas',
        'Cerrar',
      );
      if (eleccion === 'principal') {
        void this.router.navigate(['/reservas/gestion'], { queryParams: { buscar: c?.dni } });
      }
      return;
    }
    this.modelo.set({ motivo: '' });
    this.errorEstado.set('');
    this.panel.set(tipo);
  }
  
  protected cerrarPanel(): void {
    this.panel.set('cerrado');
  }

  protected confirmar(evento: Event): void {
    evento.preventDefault();
    const tipo = this.panel();
    if (tipo === 'cerrado' || !this.valido() || this.guardando()) return;

    this.guardando.set(true);
    this.errorEstado.set('');
    this.clienteService
      .cambiarEstado(this.id, { activo: tipo === 'reactivar', motivo: this.modelo().motivo.trim() })
      .subscribe({
        next: (actualizado) => {
          this.guardando.set(false);
          this.panel.set('cerrado');
          this.cliente.set(actualizado);
          this.snackBar.open(tipo === 'baja' ? 'Cliente dado de baja' : 'Cliente reactivado', 'Cerrar', { duration: 5000 });
        },
        error: (e: HttpErrorResponse) => {
          this.guardando.set(false);
          this.errorEstado.set(mensajeAmigable(e, 'No se pudo cambiar el estado. Intentá de nuevo.'));
          this.cargar(); // las reservas pudieron cambiar mientras tanto
        },
      });
  }
}

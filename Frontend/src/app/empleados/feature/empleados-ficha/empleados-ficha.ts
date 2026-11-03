import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { form, FormField } from '@angular/forms/signals';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ETIQUETA_ROL } from '../../../auth/data-access/auth-model';
import { mensajeAmigable } from '../../../core/utils/errores';
import { formatoPrecio } from '../../../core/utils/moneda';
import {
  CLASE_ESTADO_EMPLEADO,
  Empleado,
  EstadoEmpleado,
  estadosDestino,
  ETIQUETA_ESTADO_EMPLEADO,
  MAX_MOTIVO,
  MIN_MOTIVO,
  pideMotivo,
} from '../../data-access/empleado-model';
import { EmpleadoService } from '../../data-access/empleado-service';
import { HistorialEmpleado } from '../historial-empleado/historial-empleado';

@Component({
  selector: 'app-empleado-ficha',
  imports: [FormField, RouterLink, HistorialEmpleado],
  templateUrl: './empleados-ficha.html',
  styleUrl: './empleados-ficha.css',
})
export class EmpleadoFicha {
  private readonly empleadoService = inject(EmpleadoService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly id = inject(ActivatedRoute).snapshot.paramMap.get('id') ?? '';

  protected readonly etiquetaEstado = ETIQUETA_ESTADO_EMPLEADO;
  protected readonly claseEstado = CLASE_ESTADO_EMPLEADO;
  protected readonly formatoPrecio = formatoPrecio;
  protected readonly minMotivo = MIN_MOTIVO;
  protected readonly maxMotivo = MAX_MOTIVO;

  protected readonly empleado = signal<Empleado | null>(null);
  protected readonly cargando = signal(true);
  protected readonly error = signal('');

  // ---- Cambio de estado ----
  protected readonly panelAbierto = signal(false);
  protected readonly modelo = signal({ estado: '' as '' | EstadoEmpleado, motivo: '' });
  protected readonly f = form(this.modelo);
  protected readonly guardando = signal(false);
  protected readonly errorEstado = signal('');

  protected readonly destinos = computed(() => {
    const e = this.empleado();
    return e ? estadosDestino(e.estadoEmpleado) : [];
  });

  protected readonly motivoObligatorio = computed(() => {
    const e = this.empleado();
    const nuevo = this.modelo().estado;
    return !!e && !!nuevo && pideMotivo(e.estadoEmpleado, nuevo);
  });

  protected readonly errores = computed(() => {
    const { estado, motivo } = this.modelo();
    const largo = motivo.trim().length;
    return {
      estado: estado ? null : 'Elegí el nuevo estado',
      motivo:
        largo > MAX_MOTIVO
          ? `Máximo ${MAX_MOTIVO} caracteres`
          : this.motivoObligatorio() && largo < MIN_MOTIVO
            ? `Indicá el motivo (al menos ${MIN_MOTIVO} caracteres)`
            : null,
    };
  });
  protected readonly valido = computed(() => Object.values(this.errores()).every((e) => e === null));

  // Cambia con cada modificación, para que el historial se vuelva a pedir
  protected readonly huella = computed(() => `${this.empleado()?.estadoEmpleado}|${this.empleado()?.email}`);

  constructor() {
    this.cargar();
  }

  private cargar(): void {
    this.cargando.set(true);
    this.error.set('');
    this.empleadoService.buscar(this.id).subscribe({
      next: (e) => {
        this.empleado.set(e);
        this.cargando.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.cargando.set(false);
        this.error.set(
          e.status === 404
            ? 'No se encontró al empleado.'
            : mensajeAmigable(e, 'No se pudo cargar la ficha. Intentá de nuevo.'),
        );
      },
    });
  }

  protected rolTexto(e: Empleado): string {
    return (e.rolSeguridad && ETIQUETA_ROL[e.rolSeguridad]) || e.rol?.tipoRol || '';
  }

  protected etiquetaDestino(destino: EstadoEmpleado): string {
    const actual = this.empleado()?.estadoEmpleado;
    if (destino === 'INACTIVO') return 'Dar de baja (no podrá ingresar)';
    if (destino === 'ACTIVO' && actual === 'INACTIVO') return 'Reactivar';
    if (destino === 'ACTIVO') return 'Volver a activo';
    return ETIQUETA_ESTADO_EMPLEADO[destino];
  }

  protected abrirPanel(): void {
    this.modelo.set({ estado: '', motivo: '' });
    this.errorEstado.set('');
    this.panelAbierto.set(true);
  }

  protected cerrarPanel(): void {
    this.panelAbierto.set(false);
  }

  protected cambiarEstado(evento: Event): void {
    evento.preventDefault();
    const nuevo = this.modelo().estado;
    if (!nuevo || !this.valido() || this.guardando()) return;

    this.guardando.set(true);
    this.errorEstado.set('');
    this.empleadoService
      .cambiarEstado(this.id, { estado: nuevo, motivo: this.modelo().motivo.trim() })
      .subscribe({
        next: (actualizado) => {
          this.guardando.set(false);
          this.panelAbierto.set(false);
          this.empleado.set(actualizado);
          this.snackBar.open('Estado actualizado', 'Cerrar', { duration: 5000 });
        },
        error: (e: HttpErrorResponse) => {
          this.guardando.set(false);
          this.errorEstado.set(mensajeAmigable(e, 'No se pudo cambiar el estado. Intentá de nuevo.'));
        },
      });
  }
}
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { form, FormField } from '@angular/forms/signals';
import { RouterLink } from '@angular/router';
import { catchError, of } from 'rxjs';
import { ETIQUETA_ROL } from '../../../auth/data-access/auth-model';
import { mensajeAmigable } from '../../../core/utils/errores';
import { Sector } from '../../../recursos/data-access/recurso-model';
import { RecursoService } from '../../../recursos/data-access/recurso-service';
import {
  CLASE_ESTADO_EMPLEADO,
  Empleado,
  EstadoEmpleado,
  ESTADOS_EMPLEADO,
  ETIQUETA_ESTADO_EMPLEADO,
  ROLES_ALTA,
} from '../../data-access/empleado-model';
import { EmpleadoService } from '../../data-access/empleado-service';

@Component({
  selector: 'app-empleados-lista',
  imports: [FormField, RouterLink],
  templateUrl: './empleados-lista.html',
  styleUrl: './empleados-lista.css',
})
export class EmpleadosLista {
  private readonly empleadoService = inject(EmpleadoService);
  private readonly recursoService = inject(RecursoService);

  protected readonly estados = ESTADOS_EMPLEADO;
  protected readonly etiquetaEstado = ETIQUETA_ESTADO_EMPLEADO;
  protected readonly claseEstado = CLASE_ESTADO_EMPLEADO;
  protected readonly opcionesRol = ROLES_ALTA;

  protected readonly sectores = toSignal(
    this.recursoService.listarSectores().pipe(catchError(() => of([] as Sector[]))),
    { initialValue: [] as Sector[] },
  );

  protected readonly empleados = signal<Empleado[] | null>(null); // null = todavía no cargó
  protected readonly cargando = signal(false);
  protected readonly errorCarga = signal('');

  // Los activos son lo habitual: se ven primero
  protected readonly filtros = signal({ texto: '', rol: '', sector: '', estado: 'ACTIVO' as '' | EstadoEmpleado });
  protected readonly f = form(this.filtros);

  protected readonly filtrados = computed(() => {
    const { texto, rol, sector, estado } = this.filtros();
    const buscado = texto.trim().toLowerCase();
    return (this.empleados() ?? [])
      .filter((e) => !estado || e.estadoEmpleado === estado)
      .filter((e) => !rol || e.rolSeguridad === rol)
      .filter((e) => !sector || e.sector?.publicId === sector)
      .filter((e) => !buscado || `${e.nombre} ${e.apellido} ${e.dni}`.toLowerCase().includes(buscado))
      .sort((a, b) => a.apellido.localeCompare(b.apellido, 'es') || a.nombre.localeCompare(b.nombre, 'es'));
  });

  constructor() {
    this.cargar();
  }

  protected cargar(): void {
    this.cargando.set(true);
    this.errorCarga.set('');
    this.empleadoService.listar().subscribe({
      next: (lista) => {
        this.empleados.set(lista);
        this.cargando.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.cargando.set(false);
        this.errorCarga.set(
          e.status === 403
            ? 'No tenés permiso para ver los empleados.'
            : mensajeAmigable(e, 'No se pudo cargar la lista de empleados. Intentá de nuevo.'),
        );
      },
    });
  }

  protected etiquetaRol(e: Empleado): string {
    return (e.rolSeguridad && ETIQUETA_ROL[e.rolSeguridad]) || e.rol?.tipoRol || '';
  }

  protected formatoDni(dni: number): string {
    return dni.toLocaleString('es-AR');
  }
}
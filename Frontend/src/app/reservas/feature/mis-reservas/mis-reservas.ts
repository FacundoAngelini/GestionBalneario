import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { ROUTE_SEGMENTS } from '../../../core/constants/route-segments.constants';
import { formatoRango, hoyIso } from '../../../core/utils/fechas';
import { formatoPrecio } from '../../../core/utils/moneda';
import { ETIQUETA_MODALIDAD, ETIQUETA_TIPO_RECURSO } from '../../../recursos/data-access/recurso-model';
import { plazoDeCancelacionVigente } from '../../data-access/periodo-reserva';
import {
  CLASE_ESTADO,
  ESTADOS_ACTIVOS,
  ETIQUETA_ESTADO_RESERVA,
  HORAS_PARA_CANCELAR,
  Reserva,
  TEXTO_PAGO,
} from '../../data-access/reserva-model';
import { ReservaService } from '../../data-access/reserva-service';
import { SeleccionReserva } from '../../data-access/seleccion-reserva';
import { MatSnackBar } from '@angular/material/snack-bar';

type Vista = 'ACTIVAS' | 'HISTORIAL';

/** Una cancelación esperando que el cliente la confirme. */
interface Cancelacion {
  reserva: Reserva;
  cocheras: Reserva[]; // cocheras que se cancelan junto con esta reserva
}

@Component({
  selector: 'app-mis-reservas',
  imports: [RouterLink],
  templateUrl: './mis-reservas.html',
  styleUrl: './mis-reservas.css',
})
export class MisReservas {
  private readonly auth = inject(AuthClient);
  private readonly reservaService = inject(ReservaService);
  private readonly seleccion = inject(SeleccionReserva);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly etiquetaEstado = ETIQUETA_ESTADO_RESERVA;
  protected readonly claseEstado = CLASE_ESTADO;
  protected readonly etiquetaModalidad = ETIQUETA_MODALIDAD;
  protected readonly etiquetaTipo = ETIQUETA_TIPO_RECURSO;
  protected readonly formatoPrecio = formatoPrecio;
  protected readonly formatoRango = formatoRango;
  protected readonly horasCancelar = HORAS_PARA_CANCELAR;
  protected readonly textoPago = TEXTO_PAGO;

  private readonly clienteId = this.auth.publicId;

  protected readonly reservas = signal<Reserva[] | null>(null); // null = no cargo todaviaa
  protected readonly cargando = signal(false);
  protected readonly errorCarga = signal('');
  protected readonly vista = signal<Vista>('ACTIVAS');
  protected readonly cancelacion = signal<Cancelacion | null>(null);
  protected readonly procesando = signal<string | null>(null); // publicId de la reserva en tramite

  // Las activas, de la más próxima a la más lejana
  protected readonly activas = computed(() =>
    (this.reservas() ?? [])
      .filter((r) => this.esActiva(r))
      .sort((a, b) => a.fechaInicio.localeCompare(b.fechaInicio)),
  );

  // El historial, de la mas reciente a la más vieja
  protected readonly historial = computed(() =>
    (this.reservas() ?? [])
      .filter((r) => !this.esActiva(r))
      .sort((a, b) => b.fechaInicio.localeCompare(a.fechaInicio)),
  );

  protected readonly visibles = computed(() =>
    this.vista() === 'ACTIVAS' ? this.activas() : this.historial(),
  );

  constructor() {
    this.cargar();
  }

  protected cargar(): void {
    const id = this.clienteId();
    if (!id) {
      this.errorCarga.set('No pudimos identificar tu cuenta. Cerrá sesión y volvé a ingresar.');
      return;
    }
    this.cargando.set(true);
    this.errorCarga.set('');
    this.reservaService.misReservas(id).subscribe({
      next: (lista) => {
        this.reservas.set(lista);
        this.cargando.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.cargando.set(false);
        this.errorCarga.set(this.mensajeDeError(e));
      },
    });
  }

  protected esActiva(r: Reserva): boolean {
    return ESTADOS_ACTIVOS.includes(r.estadoReserva);
  }

  protected puedeCancelar(r: Reserva): boolean {
    return this.esActiva(r) && plazoDeCancelacionVigente(r.fechaInicio, HORAS_PARA_CANCELAR);
  }

  protected titulo(r: Reserva): string {
    const nombres = (r.recursosDetalle ?? []).map((x) => x.nombre);
    return nombres.length > 0 ? nombres.join(', ') : 'Reserva';
  }

  protected nombresDe(lista: Reserva[]): string {
    return lista.flatMap((r) => (r.recursosDetalle ?? []).map((x) => x.nombre)).join(', ');
  }

  protected codigo(r: Reserva): string {
    return r.publicId.slice(0, 8);
  }

  // Paso 1: antes de pedir confirmación, se consulta qué más se cancelaría
  protected pedirCancelacion(reserva: Reserva): void {
    this.procesando.set(reserva.publicId);
    this.reservaService.cocherasDependientes(reserva.publicId).subscribe({
      next: (cocheras) => {
        this.cancelacion.set({ reserva, cocheras });
        this.procesando.set(null);
      },
      error: (e: HttpErrorResponse) => {
        this.procesando.set(null);
        this.snackBar.open(this.mensajeDeError(e), 'Cerrar', { duration: 8000 });
      },
    });
  }

  protected descartarCancelacion(): void {
    this.cancelacion.set(null);
  }

  // Paso 2: el cliente confirmó
  protected confirmarCancelacion(): void {
    const pendiente = this.cancelacion();
    const clienteId = this.clienteId();
    if (!pendiente || !clienteId) return;

    const { reserva, cocheras } = pendiente;
    this.procesando.set(reserva.publicId);
    this.reservaService
      .cancelar({
        publicId: reserva.publicId,
        clientePublicId: clienteId,
        fechaInicio: reserva.fechaInicio,
        fechaFin: reserva.fechaFin,
        cancelarCocherasAsociadas: cocheras.length > 0,
      })
      .subscribe({
        next: () => {
          this.procesando.set(null);
          this.cancelacion.set(null);
          this.snackBar.open('Reserva cancelada', 'Cerrar', { duration: 5000 });
          this.cargar();
        },
        error: (e: HttpErrorResponse) => {
          this.procesando.set(null);
          this.cancelacion.set(null);
          this.snackBar.open(this.mensajeDeError(e), 'Cerrar', { duration: 8000 });
          this.cargar(); // el estado pudo haber cambiado
        },
      });
  }

  // Lleva al catálogo con la modalidad (y las fechas, si siguen en el futuro) ya cargadas
  protected reservarDeNuevo(r: Reserva): void {
    const futura = r.fechaInicio >= hoyIso();
    this.seleccion.busqueda.set({
      fechaInicio: futura ? r.fechaInicio : '',
      fechaFin: futura && r.modalidad === 'DIA' ? r.fechaFin : '',
      modalidad: r.modalidad,
    });
    this.router.navigateByUrl(`/${ROUTE_SEGMENTS.catalogo}`);
  }

  private mensajeDeError(e: HttpErrorResponse): string {
    if (e.status === 0) return 'No se pudo conectar con el servidor';
    if (e.status === 403) return 'No tenés permiso para esta operación. Cerrá sesión y volvé a ingresar.';
    return e.error?.message ?? 'Ocurrió un error inesperado. Intentá de nuevo';
  }
}

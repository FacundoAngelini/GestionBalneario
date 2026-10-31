import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { form, FormField } from '@angular/forms/signals';
import { MatSnackBar } from '@angular/material/snack-bar';
import { formatoRango } from '../../../core/utils/fechas';
import { formatoPrecio } from '../../../core/utils/moneda';
import { ETIQUETA_MODALIDAD } from '../../../recursos/data-access/recurso-model';
import {
  CLASE_ESTADO,
  ReservaEstado,
  ETIQUETA_ESTADO_RESERVA,
  HORAS_PARA_CONFIRMAR,
  Reserva,
} from '../../data-access/reserva-model';
import { PendientesReservas } from '../../data-access/pendientes-reservas';
import { ReservaService } from '../../data-access/reserva-service';

const POR_PAGINA = 20;
const ESTADOS: ReservaEstado[] = [
  'PENDIENTE',
  'CONFIRMADA',
  'FINALIZADA',
  'CANCELADA',
  'RECHAZADA',
  'VENCIDA',
];

type Vista = 'PENDIENTES' | 'TODAS';
type FiltroEstado = 'TODOS' | ReservaEstado;

/** Una acción esperando que el personal la confirme. */
interface AccionPendiente {
  tipo: 'RECHAZAR' | 'CANCELAR';
  reserva: Reserva;
  cocheras: Reserva[]; // cocheras que se cancelan junto con esta reserva
}

@Component({
  selector: 'app-gestion-reservas',
  imports: [FormField],
  templateUrl: './gestion-reservas.html',
  styleUrl: './gestion-reservas.css',
})
export class GestionReservas {
  private readonly reservaService = inject(ReservaService);
  private readonly contador = inject(PendientesReservas);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly etiquetaEstado = ETIQUETA_ESTADO_RESERVA;
  protected readonly claseEstado = CLASE_ESTADO;
  protected readonly etiquetaModalidad = ETIQUETA_MODALIDAD;
  protected readonly formatoPrecio = formatoPrecio;
  protected readonly formatoRango = formatoRango;
  protected readonly estados = ESTADOS;

  protected readonly reservas = signal<Reserva[] | null>(null); // null = todavía no cargó
  protected readonly cargando = signal(false);
  protected readonly errorCarga = signal('');
  protected readonly vista = signal<Vista>('PENDIENTES');
  protected readonly accion = signal<AccionPendiente | null>(null);
  protected readonly procesando = signal<string | null>(null); // publicId de la reserva en trámite
  private readonly limite = signal(POR_PAGINA);

  // Filtros de la pestaña "Todas"
  protected readonly filtros = signal({ texto: '', estado: 'TODOS' as FiltroEstado });
  protected readonly f = form(this.filtros);

  // Las más antiguas primero: son las que vencen antes
  protected readonly pendientes = computed(() =>
    (this.reservas() ?? [])
      .filter((r) => r.estadoReserva === 'PENDIENTE')
      .sort((a, b) =>
        (a.fechaCreacion ?? a.fechaInicio).localeCompare(b.fechaCreacion ?? b.fechaInicio),
      ),
  );

  private readonly filtradas = computed(() => {
    const { texto, estado } = this.filtros();
    const buscado = texto.trim().toLowerCase();
    return (this.reservas() ?? [])
      .filter((r) => estado === 'TODOS' || r.estadoReserva === estado)
      .filter((r) => !buscado || this.textoBusqueda(r).includes(buscado))
      .sort((a, b) => b.fechaInicio.localeCompare(a.fechaInicio));
  });

  private readonly lista = computed(() =>
    this.vista() === 'PENDIENTES' ? this.pendientes() : this.filtradas(),
  );
  protected readonly visibles = computed(() => this.lista().slice(0, this.limite()));
  protected readonly hayMas = computed(() => this.lista().length > this.limite());

  constructor() {
    // Al cambiar de pestaña o de filtros, se vuelve a la primera página
    effect(() => {
      this.vista();
      this.filtros();
      untracked(() => this.limite.set(POR_PAGINA));
    });
    this.cargar();
  }

  protected cargar(): void {
    this.cargando.set(true);
    this.errorCarga.set('');
    this.reservaService.todas().subscribe({
      next: (lista) => {
        this.reservas.set(lista);
        this.cargando.set(false);
        this.contador.fijar(this.pendientes().length);
      },
      error: (e: HttpErrorResponse) => {
        this.cargando.set(false);
        this.errorCarga.set(this.mensajeDeError(e));
      },
    });
  }

  protected verMas(): void {
    this.limite.update((l) => l + POR_PAGINA);
  }

  // ---- Datos de cada tarjeta ----
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

  protected formatoDni(dni: number | null | undefined): string {
    return dni ? dni.toLocaleString('es-AR') : '';
  }

  /** Cuánto hace que se pidió y cuánto falta para que venza (solo las pendientes). */
  protected vencimiento(r: Reserva): { texto: string; urgente: boolean } | null {
    if (r.estadoReserva !== 'PENDIENTE' || !r.fechaCreacion) return null;
    const creada = new Date(r.fechaCreacion).getTime();
    if (Number.isNaN(creada)) return null;

    const transcurridas = (Date.now() - creada) / 3_600_000;
    const restantes = HORAS_PARA_CONFIRMAR - transcurridas;
    const pedida =
      transcurridas < 1 ? 'hace menos de 1 hora' : `hace ${Math.floor(transcurridas)} h`;

    if (restantes <= 0) {
      return { texto: `Pedida ${pedida} · plazo vencido, se libera en instantes`, urgente: true };
    }
    const vence = restantes < 1 ? 'menos de 1 hora' : `${Math.floor(restantes)} h`;
    return { texto: `Pedida ${pedida} · vence en ${vence}`, urgente: restantes < 6 };
  }

  private textoBusqueda(r: Reserva): string {
    return [
      r.clienteNombre ?? '',
      String(r.clienteDni ?? ''),
      this.codigo(r),
      ...(r.recursosDetalle ?? []).map((x) => x.nombre),
    ]
      .join(' ')
      .toLowerCase();
  }

  // ---- Acciones ----
  protected confirmar(reserva: Reserva): void {
    this.procesando.set(reserva.publicId);
    this.reservaService.confirmar(reserva.publicId).subscribe({
      next: (actualizada) => this.terminar(actualizada, 'Reserva confirmada'),
      error: (e: HttpErrorResponse) => this.fallar(e),
    });
  }

  protected pedirRechazo(reserva: Reserva): void {
    this.accion.set({ tipo: 'RECHAZAR', reserva, cocheras: [] });
  }

  // Antes de pedir confirmación, se consulta qué más se cancelaría
  protected pedirCancelacion(reserva: Reserva): void {
    this.procesando.set(reserva.publicId);
    this.reservaService.cocherasDependientes(reserva.publicId).subscribe({
      next: (cocheras) => {
        this.accion.set({ tipo: 'CANCELAR', reserva, cocheras });
        this.procesando.set(null);
      },
      error: (e: HttpErrorResponse) => this.fallar(e),
    });
  }

  protected descartarAccion(): void {
    this.accion.set(null);
  }

  protected confirmarAccion(): void {
    const pendiente = this.accion();
    if (!pendiente) return;
    const { tipo, reserva, cocheras } = pendiente;
    this.procesando.set(reserva.publicId);

    if (tipo === 'RECHAZAR') {
      this.reservaService.rechazar(reserva.publicId).subscribe({
        next: (actualizada) => this.terminar(actualizada, 'Solicitud rechazada'),
        error: (e: HttpErrorResponse) => this.fallar(e),
      });
      return;
    }

    this.reservaService.cancelarAdministrativa(reserva.publicId, cocheras.length > 0).subscribe({
      next: () => {
        this.procesando.set(null);
        this.accion.set(null);
        this.snackBar.open('Reserva cancelada', 'Cerrar', { duration: 5000 });
        this.cargar(); // también pudo cambiar una cochera asociada
      },
      error: (e: HttpErrorResponse) => this.fallar(e),
    });
  }

  // Una reserva confirmada o rechazada: se reemplaza en la lista con lo que devolvió el backend
  private terminar(actualizada: Reserva, mensaje: string): void {
    this.reservas.update((lista) =>
      (lista ?? []).map((r) => (r.publicId === actualizada.publicId ? actualizada : r)),
    );
    this.contador.fijar(this.pendientes().length);
    this.procesando.set(null);
    this.accion.set(null);
    this.snackBar.open(mensaje, 'Cerrar', { duration: 5000 });
  }

  private fallar(e: HttpErrorResponse): void {
    this.procesando.set(null);
    this.accion.set(null);
    this.snackBar.open(this.mensajeDeError(e), 'Cerrar', { duration: 8000 });
    this.cargar(); // el estado pudo haber cambiado (por ejemplo, si venció)
  }

  private mensajeDeError(e: HttpErrorResponse): string {
    if (e.status === 0) return 'No se pudo conectar con el servidor';
    if (e.status === 403) return 'No tenés permiso para esta operación. Cerrá sesión y volvé a ingresar.';
    return e.error?.message ?? 'Ocurrió un error inesperado. Intentá de nuevo';
  }
}

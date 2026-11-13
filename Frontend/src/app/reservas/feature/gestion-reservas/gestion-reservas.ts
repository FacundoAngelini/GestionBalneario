import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { form, FormField } from '@angular/forms/signals';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ROUTE_SEGMENTS } from '../../../core/constants/route-segments.constants';
import { formatoFecha, formatoFechaHora, formatoRango, hoyIso } from '../../../core/utils/fechas';
import { formatoPrecio } from '../../../core/utils/moneda';
import { ETIQUETA_MODALIDAD } from '../../../recursos/data-access/recurso-model';
import { PendientesReservas } from '../../data-access/pendientes-reservas';
import {
  CLASE_ESTADO,
  ReservaEstado,
  ETIQUETA_ESTADO_RESERVA,
  HORAS_PARA_CONFIRMAR,
  Reserva,
} from '../../data-access/reserva-model';
import { ReservaService } from '../../data-access/reserva-service';
import { HistorialReserva } from './historial-reservas/historial-reservas';
import { mensajeAmigable } from '../../../core/utils/errores';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { firstValueFrom } from 'rxjs';
import { Dialogos } from '../../../shared/mensaje-dialog/dialogos';

const POR_PAGINA = 20;
const MIN_MOTIVO = 5;
const MAX_MOTIVO = 300;
const ESTADOS: ReservaEstado[] = ['PENDIENTE', 'CONFIRMADA', 'FINALIZADA', 'CANCELADA', 'RECHAZADA', 'VENCIDA'];
const ESTADOS_COBRABLES: ReservaEstado[] = ['PENDIENTE', 'CONFIRMADA', 'FINALIZADA'];

type Vista = 'PENDIENTES' | 'POR_COBRAR' | 'INGRESO' | 'TODAS';
type FiltroEstado = 'TODOS' | ReservaEstado;

@Component({
  selector: 'app-gestion-reservas',
  imports: [FormField, RouterLink, HistorialReserva],
  templateUrl: './gestion-reservas.html',
  styleUrl: './gestion-reservas.css',
})
export class GestionReservas {
  private readonly reservaService = inject(ReservaService);
  private readonly contador = inject(PendientesReservas);
  private readonly snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);
  private readonly auth = inject(AuthClient);

  // El carpero consulta las reservas, pero no tiene ninguna acción
  protected readonly soloLectura = computed(() => !this.auth.esPersonal());

  protected readonly etiquetaEstado = ETIQUETA_ESTADO_RESERVA;
  protected readonly claseEstado = CLASE_ESTADO;
  protected readonly etiquetaModalidad = ETIQUETA_MODALIDAD;
  protected readonly formatoPrecio = formatoPrecio;
  protected readonly formatoRango = formatoRango;
  protected readonly formatoFechaHora = formatoFechaHora;
  protected readonly estados = ESTADOS;
  protected readonly minMotivo = MIN_MOTIVO;

  protected readonly reservas = signal<Reserva[] | null>(null); // null = todavía no cargó
  protected readonly cargando = signal(false);
  protected readonly errorCarga = signal('');
  protected readonly vista = signal<Vista>(this.auth.esPersonal() ? 'PENDIENTES' : 'INGRESO');  
  protected readonly procesando = signal<string | null>(null); // publicId de la reserva en trámite
  private readonly limite = signal(POR_PAGINA);
  private readonly descuento = signal(0); // % por pago en efectivo (solo para la vista previa)

  // Filtros de la pestaña "Todas"
  protected readonly filtros = signal({ texto: '', estado: 'TODOS' as FiltroEstado });
  protected readonly f = form(this.filtros);

  // Motivo de una anulación de cobro
  protected readonly datosMotivo = signal({ motivo: '' });
  protected readonly fm = form(this.datosMotivo);
  protected readonly motivoValido = computed(() => {
    const largo = this.datosMotivo().motivo.trim().length;
    return largo >= MIN_MOTIVO && largo <= MAX_MOTIVO;
  });
  protected readonly motivoLargo = computed(() => this.datosMotivo().motivo.trim().length > MAX_MOTIVO);

  private readonly dialogos = inject(Dialogos);
  protected readonly anulandoId = signal<string | null>(null); // reserva cuyo cobro se está anulando

  // Las más antiguas primero: son las que vencen antes
  protected readonly pendientes = computed(() =>
    (this.reservas() ?? [])
      .filter((r) => r.estadoReserva === 'PENDIENTE')
      .sort((a, b) =>
        (a.fechaCreacion ?? a.fechaInicio).localeCompare(b.fechaCreacion ?? b.fechaInicio),
      ),
  );

  // Confirmadas o terminadas que todavía no se cobraron; las que empiezan antes, primero
  protected readonly porCobrar = computed(() =>
    (this.reservas() ?? [])
      .filter((r) => ['CONFIRMADA', 'FINALIZADA'].includes(r.estadoReserva) && !r.cobro)
      .sort((a, b) => a.fechaInicio.localeCompare(b.fechaInicio)),
  );

    // Reservas que eetsna llegando en pendientes o condirmadas que otdavia no terminaron // Reservas que pueden estar llegando: pendientes o confirmadas que todavía no terminaron
  private readonly vigentes = computed(() => {
    const hoy = hoyIso();
    return (this.reservas() ?? []).filter(
      (r) => ['PENDIENTE', 'CONFIRMADA'].includes(r.estadoReserva) && r.fechaFin >= hoy,
    );
  });

  // Las que estan en curso hoy (es lo que se ve sin buscar)
  protected readonly vigentesHoy = computed(() => {
    const hoy = hoyIso();
    return this.vigentes().filter((r) => r.fechaInicio <= hoy);
  });

  // Con busqueda se encuentra cualquier reserva vigente (por ejemplo, alguien que llega un dia antes)
  private readonly ingreso = computed(() => {
    const buscado = this.filtros().texto.trim().toLowerCase();
    const base = buscado
      ? this.vigentes().filter((r) => this.textoBusqueda(r).includes(buscado))
      : this.vigentesHoy();
    return [...base].sort((a, b) => a.fechaInicio.localeCompare(b.fechaInicio));
  });

  private readonly filtradas = computed(() => {
    const { texto, estado } = this.filtros();
    const buscado = texto.trim().toLowerCase();
    return (this.reservas() ?? [])
      .filter((r) => estado === 'TODOS' || r.estadoReserva === estado)
      .filter((r) => !buscado || this.textoBusqueda(r).includes(buscado))
      .sort((a, b) => b.fechaInicio.localeCompare(a.fechaInicio));
  });

  private readonly lista = computed(() => {
    switch (this.vista()) {
      case 'PENDIENTES':
        return this.pendientes();
      case 'POR_COBRAR':
        return this.porCobrar();
      case 'INGRESO':
        return this.ingreso();
      case 'TODAS':
        return this.filtradas();
    }
  });
  protected readonly visibles = computed(() => this.lista().slice(0, this.limite()));
  protected readonly hayMas = computed(() => this.lista().length > this.limite());

  constructor() {
    // Al cambiar de pestaña o de filtros, se vuelve a la primera página
    effect(() => {
      this.vista();
      this.filtros();
      untracked(() => this.limite.set(POR_PAGINA));
    });
    if (!this.soloLectura()) {
      this.reservaService.configuracionPago().subscribe({
        next: (c) => this.descuento.set(c.descuentoEfectivo),
        error: () => this.descuento.set(0),
      });
    }
        // Si el contador (que consulta cada minuto) cambia y la lista quedó desfasada, se recarga sola
    effect(() => {
      const n = this.contador.cantidad();
      untracked(() => {
        if (this.reservas() !== null && !this.cargando() && this.procesando() === null && n !== this.pendientes().length) {
          this.cargar();
        }
      });
    });
    this.cargar();

        // Si se llega desde la ficha de un cliente, se busca directamente en "Todas"
    const buscar = inject(ActivatedRoute).snapshot.queryParamMap.get('buscar');
    if (buscar) {
      this.filtros.update((f) => ({ ...f, texto: buscar }));
      this.vista.set('TODAS');
    }
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

  protected esCobrable(r: Reserva): boolean {
    return ESTADOS_COBRABLES.includes(r.estadoReserva) && !r.cobro;
  }

  protected montoACobrar(r: Reserva): number {
    return Math.round((r.montoTotal ?? 0) * (100 - this.descuento())) / 100;
  }

  protected descuentoDe(r: Reserva): number {
    return (r.montoTotal ?? 0) - this.montoACobrar(r);
  }

  protected hayDescuento(): boolean {
    return this.descuento() > 0;
  }

  protected porcentajeDescuento(): number {
    return this.descuento();
  }

  // Cambia cuando la reserva cambia, para que su historial se vuelva a pedir
  protected huella(r: Reserva): string {
    return `${r.estadoReserva}|${r.cobro?.fecha ?? ''}`;
  }

  /** Cuánto hace que se pidió y cuánto falta para que venza (solo las pendientes). */
  protected vencimiento(r: Reserva): { texto: string; urgente: boolean } | null {
    if (r.estadoReserva !== 'PENDIENTE' || !r.fechaCreacion) return null;
    const creada = new Date(r.fechaCreacion.slice(0, 19)).getTime();
    if (Number.isNaN(creada)) return null;

    const transcurridas = (Date.now() - creada) / 3_600_000;
    const restantes = HORAS_PARA_CONFIRMAR - transcurridas;
    const pedida = transcurridas < 1 ? 'hace menos de 1 hora' : `hace ${Math.floor(transcurridas)} h`;

    if (restantes <= 0) {
      return { texto: `Pedida ${pedida} · plazo vencido, se libera en instantes`, urgente: true };
    }
    const vence = restantes < 1 ? 'menos de 1 hora' : `${Math.floor(restantes)} h`;
    return { texto: `Pedida ${pedida} · vence en ${vence}`, urgente: restantes < 6 };
  }

  /** Aviso para las confirmadas o terminadas que siguen sin cobrarse. */
  protected avisoDeCobro(r: Reserva): string | null {
    if (r.cobro || !['CONFIRMADA', 'FINALIZADA'].includes(r.estadoReserva)) return null;
    if (r.estadoReserva === 'FINALIZADA') return 'La reserva terminó sin cobrarse';
    return r.fechaInicio <= hoyIso() ? 'La reserva ya empezó y no está cobrada' : null;
  }

 /** Lo que ve quien atiende en la entrada: si la reserva puede ingresar hoy. */
  protected veredicto(r: Reserva): { texto: string; clase: string } | null {
    if (r.estadoReserva === 'PENDIENTE') {
      return { texto: 'Todavía no confirmada', clase: 'estado--pendiente' };
    }
    if (r.estadoReserva !== 'CONFIRMADA') return null;
    if (r.fechaInicio > hoyIso()) {
      return { texto: `Empieza el ${formatoFecha(r.fechaInicio)}`, clase: 'estado--finalizada' };
    }
    return r.cobro
      ? { texto: 'Puede ingresar', clase: 'estado--confirmada' }
      : { texto: 'Falta cobrar antes de ingresar', clase: 'estado--pendiente' };
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


  protected verComprobante(reserva: Reserva): void {
    void this.router.navigateByUrl(`/${ROUTE_SEGMENTS.reservas}/${ROUTE_SEGMENTS.comprobante}/${reserva.publicId}`);
  }

  protected async pedirRechazo(reserva: Reserva): Promise<void> {
    const confirmado = await this.dialogos.confirmar(
      'Rechazar la solicitud',
      `¿Rechazar la solicitud de ${reserva.clienteNombre}?\n${this.titulo(reserva)} · ${formatoRango(reserva.fechaInicio, reserva.fechaFin)}\n\n` +
        'Los recursos se liberan. Esta acción no se puede deshacer.',
      'Sí, rechazar',
    );
    if (!confirmado) return;

    this.procesando.set(reserva.publicId);
    this.reservaService.rechazar(reserva.publicId).subscribe({
      next: (actualizada) => this.terminar(actualizada, 'Solicitud rechazada'),
      error: (e: HttpErrorResponse) => this.fallar(e),
    });
  }

  // Antes de confirmar, se consulta qué más se cancelaría
  protected async pedirCancelacion(reserva: Reserva): Promise<void> {
    this.procesando.set(reserva.publicId);
    let cocheras: Reserva[];
    try {
      cocheras = await firstValueFrom(this.reservaService.cocherasDependientes(reserva.publicId));
    } catch (e) {
      this.fallar(e as HttpErrorResponse);
      return;
    }
    this.procesando.set(null);

    const aviso =
      cocheras.length > 0
        ? `\n\nTambién se cancela la cochera asociada (${this.nombresDe(cocheras)}): sin carpa ni sombrilla, no puede mantenerse.`
        : '';
    const confirmado = await this.dialogos.confirmar(
      'Cancelar la reserva',
      `¿Cancelar la reserva confirmada de ${reserva.clienteNombre}?\n${this.titulo(reserva)} · ${formatoRango(reserva.fechaInicio, reserva.fechaFin)}\n\n` +
        `Los recursos se liberan.${aviso}\n\nEsta acción no se puede deshacer.`,
      'Sí, cancelar',
    );
    if (!confirmado) return;

    this.procesando.set(reserva.publicId);
    this.reservaService.cancelarAdministrativa(reserva.publicId, cocheras.length > 0).subscribe({
      next: () => {
        this.procesando.set(null);
        this.snackBar.open('Reserva cancelada', 'Cerrar', { duration: 5000 });
        this.cargar(); // también pudo cambiar una cochera asociada
      },
      error: (e: HttpErrorResponse) => this.fallar(e),
    });
  }

  protected async pedirCobro(reserva: Reserva): Promise<void> {
    const lineas = [`Total: ${formatoPrecio(reserva.montoTotal ?? 0)}`];
    if (this.hayDescuento()) {
      lineas.push(`Descuento por efectivo (${this.porcentajeDescuento()} %): −${formatoPrecio(this.descuentoDe(reserva))}`);
    }
    lineas.push(`A cobrar en efectivo: ${formatoPrecio(this.montoACobrar(reserva))}`);

    const confirmado = await this.dialogos.confirmar(
      'Registrar cobro',
      `${reserva.clienteNombre} · ${this.titulo(reserva)}\n\n${lineas.join('\n')}` +
        `${reserva.estadoReserva === 'PENDIENTE' ? '\n\nLa reserva también queda confirmada.' : ''}\n\n` +
        'El monto final lo calcula el sistema. Queda registrado quién cobra.',
      'Sí, registrar cobro',
    );
    if (!confirmado) return;

    this.procesando.set(reserva.publicId);
    this.reservaService.registrarCobro(reserva.publicId).subscribe({
      next: () => {
        this.procesando.set(null);
        this.contador.refrescar();
        this.snackBar.open('Cobro registrado', 'Cerrar', { duration: 5000 });
        this.verComprobante(reserva); // el comprobante se puede imprimir desde ahí
      },
      error: (e: HttpErrorResponse) => this.fallar(e),
    });
  }

  // Anular un cobro pide un motivo: se resuelve con un panel dentro de la tarjeta
  protected pedirAnulacion(reserva: Reserva): void {
    this.datosMotivo.set({ motivo: '' });
    this.anulandoId.set(reserva.publicId);
  }

  protected descartarAnulacion(): void {
    this.anulandoId.set(null);
  }

  protected confirmarAnulacion(): void {
    const id = this.anulandoId();
    if (!id || !this.motivoValido()) return;
    this.procesando.set(id);
    this.reservaService.anularCobro(id, this.datosMotivo().motivo.trim()).subscribe({
      next: (actualizada) => this.terminar(actualizada, 'Cobro anulado'),
      error: (e: HttpErrorResponse) => this.fallar(e),
    });
  }

  // Una reserva modificada: se reemplaza en la lista con lo que devolvió el backend
  private terminar(actualizada: Reserva, mensaje: string): void {
    this.reservas.update((lista) =>
      (lista ?? []).map((r) => (r.publicId === actualizada.publicId ? actualizada : r)),
    );
    this.contador.fijar(this.pendientes().length);
    this.procesando.set(null);
    this.anulandoId.set(null);
    this.snackBar.open(mensaje, 'Cerrar', { duration: 5000 });
  }

  private fallar(e: HttpErrorResponse): void {
    this.procesando.set(null);
    this.anulandoId.set(null);
    void this.dialogos.avisar('error', 'No se pudo completar la acción', this.mensajeDeError(e));
    this.cargar(); // el estado pudo haber cambiado (por ejemplo, si venció)
  }

   private mensajeDeError(e: HttpErrorResponse): string {
    return mensajeAmigable(e, 'Ocurrió un error inesperado. Intentá de nuevo');
  }
}
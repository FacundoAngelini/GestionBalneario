import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { form, FormField } from '@angular/forms/signals';
import { RouterLink } from '@angular/router';
import { catchError, firstValueFrom, of } from 'rxjs';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { formatoRango } from '../../../core/utils/fechas';
import { formatoPrecio } from '../../../core/utils/moneda';
import {
  ETIQUETA_MODALIDAD,
  ETIQUETA_TIPO_RECURSO,
  Modalidad,
  RecursoCatalogo,
  Temporada,
} from '../../../recursos/data-access/recurso-model';
import {
  ETIQUETA_ESTADO_RESERVA,
  HORAS_PARA_CANCELAR,
  HORAS_PARA_CONFIRMAR,
  Reserva,
  TEXTO_PAGO,
} from '../../data-access/reserva-model';
import { ReservaService } from '../../data-access/reserva-service';
import { SeleccionReserva } from '../../data-access/seleccion-reserva';
import { RecursoService } from '../../../recursos/data-access/recurso-service';
import { toSignal } from '@angular/core/rxjs-interop';
import { diasEntre, fechaFinEfectiva, precioVigente } from '../../data-access/periodo-reserva';

const ORDEN_MODALIDAD: Modalidad[] = ['DIA', 'QUINCENA', 'MES', 'TEMPORADA'];

interface OpcionModalidad {
  modalidad: Modalidad;
  fechaFin: string; // hasta cuando dura realmente la reserva
  dias: number;
  total: number; // lo que calcula el backend
}

interface ReservaCreada {
  reserva: Reserva;
  nombres: string;
}

interface Resultado {
  creadas: ReservaCreada[];
  noCreadas: string; // nombres de lo que no se pudo solicitar ('' si salió todo bien)
  motivo: string;
}

@Component({
  selector: 'app-confirmar-solicitud',
  imports: [FormField, RouterLink],
  templateUrl: './confirmar-solicitud.html',
  styleUrl: './confirmar-solicitud.css',
})
export class ConfirmarSolicitud {
  private readonly auth = inject(AuthClient);
  private readonly reservaService = inject(ReservaService);
  protected readonly seleccion = inject(SeleccionReserva);

  protected readonly etiquetaTipo = ETIQUETA_TIPO_RECURSO;
  protected readonly etiquetaModalidad = ETIQUETA_MODALIDAD;
  protected readonly etiquetaEstado = ETIQUETA_ESTADO_RESERVA;
  protected readonly formatoPrecio = formatoPrecio;
  protected readonly formatoRango = formatoRango;
  protected readonly horasConfirmar = HORAS_PARA_CONFIRMAR;
  protected readonly horasCancelar = HORAS_PARA_CANCELAR;
    protected readonly textoPago = TEXTO_PAGO;

  private readonly recursoService = inject(RecursoService);
  private readonly temporada = toSignal(
    this.recursoService.obtenerTemporada().pipe(catchError(() => of(null as Temporada | null))),
    { initialValue: null as Temporada | null },
  );

  protected readonly fechaInicio = computed(() => this.seleccion.periodo()?.fechaInicio ?? '');

  protected readonly modelo = signal({ modalidad: '' as '' | Modalidad });
  protected readonly f = form(this.modelo);

  protected readonly enviando = signal(false);
  protected readonly errorEnvio = signal('');
  protected readonly resultado = signal<Resultado | null>(null);

  // Si el token es de antes de la Fase 0, no trae el ID del cliente: hay que iniciar sesión de nuevo
  private readonly clienteId = this.auth.publicId;
  protected readonly sinIdCliente = computed(() => this.clienteId() === null);

  protected readonly periodoTexto = computed(() => {
    const p = this.seleccion.periodo();
    return p ? formatoRango(p.fechaInicio, p.fechaFin) : '';
  });

  // Solo las modalidades que el backend aceptaria: con precio para todo lo elegido y dentro de la temporada
  protected readonly opciones = computed<OpcionModalidad[]>(() => {
    const elegidos = this.seleccion.elegidos();
    const periodo = this.seleccion.periodo();
    if (!periodo || elegidos.length === 0) return [];
    const finTemporada = this.temporada()?.finTemporada ?? null;

    return ORDEN_MODALIDAD.flatMap((modalidad) => {
      const fechaFin = fechaFinEfectiva(modalidad, periodo.fechaInicio, periodo.fechaFin, finTemporada);
      if (!fechaFin) return [];
      if (finTemporada && fechaFin > finTemporada) return [];

      const dias = diasEntre(periodo.fechaInicio, fechaFin);
      let total = 0;
      for (const recurso of elegidos) {
        const precio = precioVigente(recurso, modalidad, periodo.fechaInicio);
        if (precio === null) return [];
        total += modalidad === 'DIA' ? precio * dias : precio;
      }
      return [{ modalidad, fechaFin, dias, total }];
    });
  });

  protected readonly sinModalidades = computed(
    () => this.seleccion.elegidos().length > 0 && this.opciones().length === 0,
  );

  protected readonly opcionActiva = computed(() => {
    const elegida = this.modelo().modalidad;
    return this.opciones().find((o) => o.modalidad === elegida) ?? null;
  });

  protected readonly puedeEnviar = computed(
    () => this.opcionActiva() !== null && !this.sinIdCliente() && !this.enviando(),
  );

  // Carpas y sombrillas primero; las cocheras después (la cochera exige un alojamiento ya reservado)
  private readonly grupos = computed(() =>
    [this.seleccion.alojamientos(), this.seleccion.cocheras()].filter((g) => g.length > 0),
  );

  constructor() {
    // Si hay una sola modalidad posible, se deja elegida
    effect(() => {
      const opciones = this.opciones();
      if (opciones.length === 1) {
        untracked(() => this.modelo.update((m) => ({ ...m, modalidad: opciones[0].modalidad })));
      }
    });
  }

  protected async confirmar(evento: Event): Promise<void> {
    const opcion = this.opcionActiva();
    const periodo = this.seleccion.periodo();
    const clienteId = this.clienteId();
    if (!opcion || !periodo || !clienteId || this.enviando()) return;
    this.enviando.set(true);
    this.errorEnvio.set('');

    const grupos = this.grupos();
    const creadas: ReservaCreada[] = [];
    let motivo = '';

    for (const recursos of grupos) {
      try {
        const reserva = await firstValueFrom(
            this.reservaService.reservarSinPago({
            clientePublicId: clienteId,
            fechaInicio: periodo.fechaInicio,
            fechaFin: opcion.fechaFin,
            modalidad: opcion.modalidad,
            recursosPublicIds: recursos.map((r) => r.publicId),
          }),
        );
        creadas.push({ reserva, nombres: this.nombres(recursos) });
      } catch (error) {
        motivo = this.mensajeDeError(error);
        break; // si falla un grupo, no se intenta el siguiente
      }
    }
    this.enviando.set(false);

    // No se creó nada: se queda en la pantalla para corregir o reintentar
    if (creadas.length === 0) {
      this.errorEnvio.set(motivo);
      return;
    }

    // Como se corta en el primer error, lo que no se creó son los grupos que siguen
    const noCreadas = this.nombres(grupos.slice(creadas.length).flat());
    this.seleccion.limpiar();
    this.resultado.set({ creadas, noCreadas, motivo });
  }

  private nombres(recursos: RecursoCatalogo[]): string {
    return recursos.map((r) => r.nombre).join(', ');
  }

  private mensajeDeError(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 0) return 'No se pudo conectar con el servidor';
      if (error.status === 403) {
        return 'No tenés permiso para hacer esta reserva. Cerrá sesión y volvé a ingresar.';
      }
      if (error.status === 409) {
        return 'Alguno de los recursos no está libre durante todo el período de la modalidad elegida. Probá con otra modalidad o con otras fechas.';
      }
      if (error.error?.message) return error.error.message;
    }
    return 'Ocurrió un error inesperado. Intentá de nuevo';
  }
}
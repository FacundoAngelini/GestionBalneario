import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { form, FormField } from '@angular/forms/signals';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { formatoRango } from '../../../core/utils/fechas';
import { formatoPrecio } from '../../../core/utils/moneda';
import {
  ETIQUETA_MODALIDAD,
  ETIQUETA_TIPO_RECURSO,
  Modalidad,
  RecursoCatalogo,
} from '../../../recursos/data-access/recurso-model';
import {
  ETIQUETA_ESTADO_RESERVA,
  HORAS_PARA_CANCELAR,
  HORAS_PARA_CONFIRMAR,
  Reserva,
} from '../../data-access/reserva-model';
import { ReservaService } from '../../data-access/reserva-service';
import { SeleccionReserva } from '../../data-access/seleccion-reserva';

const ORDEN_MODALIDAD: Modalidad[] = ['DIA', 'QUINCENA', 'MES', 'TEMPORADA'];

interface OpcionModalidad {
  modalidad: Modalidad;
  precio: number; // suma de los precios de todos los recursos elegidos
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

  // Solo las modalidades en las que TODOS los recursos elegidos tienen precio
  protected readonly opciones = computed<OpcionModalidad[]>(() => {
    const elegidos = this.seleccion.elegidos();
    if (elegidos.length === 0) return [];

    return ORDEN_MODALIDAD.flatMap((modalidad) => {
      const precios = elegidos
        .map((r) => r.precios?.find((p) => p.modalidad === modalidad)?.precio ?? null)
        .filter((p): p is number => p !== null);
      if (precios.length !== elegidos.length) return [];
      return [{ modalidad, precio: precios.reduce((suma, p) => suma + p, 0) }];
    });
  });

  protected readonly sinModalidades = computed(
    () => this.seleccion.elegidos().length > 0 && this.opciones().length === 0,
  );

  // La modalidad del formulario, siempre que siga siendo una opción válida
  protected readonly modalidadActiva = computed<Modalidad | null>(() => {
    const elegida = this.modelo().modalidad;
    return elegida && this.opciones().some((o) => o.modalidad === elegida) ? elegida : null;
  });

  protected readonly precioElegido = computed(
    () => this.opciones().find((o) => o.modalidad === this.modalidadActiva())?.precio ?? null,
  );

  protected readonly puedeEnviar = computed(
    () => this.modalidadActiva() !== null && !this.sinIdCliente() && !this.enviando(),
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
    evento.preventDefault();
    const modalidad = this.modalidadActiva();
    const periodo = this.seleccion.periodo();
    const clienteId = this.clienteId();
    if (!modalidad || !periodo || !clienteId || this.enviando()) return;

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
            fechaFin: periodo.fechaFin,
            modalidad,
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
        return 'Alguno de los recursos ya fue reservado por otra persona. Volvé al catálogo y buscá de nuevo.';
      }
      if (error.error?.message) return error.error.message;
    }
    return 'Ocurrió un error inesperado. Intentá de nuevo';
  }
}
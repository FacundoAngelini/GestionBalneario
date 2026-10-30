import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { formatoRango } from '../../../core/utils/fechas';
import { formatoPrecio } from '../../../core/utils/moneda';
import {
  ETIQUETA_MODALIDAD,
  ETIQUETA_TIPO_RECURSO,
  RecursoCatalogo,
} from '../../../recursos/data-access/recurso-model';
import { totalRecurso } from '../../data-access/periodo-reserva';
import {
  ETIQUETA_ESTADO_RESERVA,
  HORAS_PARA_CANCELAR,
  HORAS_PARA_CONFIRMAR,
  Reserva,
  TEXTO_PAGO,
} from '../../data-access/reserva-model';
import { ReservaService } from '../../data-access/reserva-service';
import { SeleccionReserva } from '../../data-access/seleccion-reserva';

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
  imports: [RouterLink],
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

  protected readonly enviando = signal(false);
  protected readonly errorEnvio = signal('');
  protected readonly resultado = signal<Resultado | null>(null);

  // Si el token es de antes de la Fase 0, no trae el ID del cliente: hay que iniciar sesión de nuevo
  private readonly clienteId = this.auth.publicId;
  protected readonly sinIdCliente = computed(() => this.clienteId() === null);

  protected readonly periodoTexto = computed(() => {
    const p = this.seleccion.periodo();
    if (!p) return '';
    const dias = this.seleccion.dias();
    return `${formatoRango(p.fechaInicio, p.fechaFin)} (${dias} ${dias === 1 ? 'día' : 'días'})`;
  });

  protected readonly modalidadTexto = computed(() => {
    const p = this.seleccion.periodo();
    return p ? ETIQUETA_MODALIDAD[p.modalidad] : '';
  });

  // Cada recurso con su total, calculado con la regla del backend
  protected readonly lineas = computed(() => {
    const p = this.seleccion.periodo();
    if (!p) return [];
    const dias = this.seleccion.dias();
    return this.seleccion.elegidos().map((recurso) => ({
      recurso,
      total: totalRecurso(recurso, p.modalidad, p.fechaInicio, dias) ?? 0,
    }));
  });

  protected readonly puedeEnviar = computed(
    () => this.seleccion.periodo() !== null && !this.sinIdCliente() && !this.enviando(),
  );

  // Carpas y sombrillas primero; las cocheras después (la cochera exige un alojamiento ya reservado)
  private readonly grupos = computed(() =>
    [this.seleccion.alojamientos(), this.seleccion.cocheras()].filter((g) => g.length > 0),
  );

  protected async confirmar(evento: Event): Promise<void> {
    evento.preventDefault();
    const periodo = this.seleccion.periodo();
    const clienteId = this.clienteId();
    if (!periodo || !clienteId || this.enviando()) return;

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
            fechaFin: periodo.fechaFin, // el fin real de la modalidad
            modalidad: periodo.modalidad,
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
        return 'Alguno de los recursos ya no está libre. Volvé al catálogo y buscá de nuevo.';
      }
      if (error.error?.message) return error.error.message;
    }
    return 'Ocurrió un error inesperado. Intentá de nuevo';
  }
}
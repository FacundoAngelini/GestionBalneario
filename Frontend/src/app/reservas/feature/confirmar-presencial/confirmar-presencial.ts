import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { form, FormField } from '@angular/forms/signals';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { formatoRango } from '../../../core/utils/fechas';
import { formatoPrecio } from '../../../core/utils/moneda';
import {
  ETIQUETA_MODALIDAD,
  ETIQUETA_TIPO_RECURSO,
  RecursoCatalogo,
} from '../../../recursos/data-access/recurso-model';
import { totalRecurso } from '../../data-access/periodo-reserva';
import {
  ClienteBusqueda,
  ETIQUETA_ESTADO_RESERVA,
  ReservaPresencialResponse,
} from '../../data-access/reserva-model';
import { ReservaService } from '../../data-access/reserva-service';
import { SeleccionReserva } from '../../data-access/seleccion-reserva';

interface ReservaCreada {
  respuesta: ReservaPresencialResponse;
  nombres: string;
}

interface Resultado {
  cliente: ClienteBusqueda;
  creadas: ReservaCreada[];
  noCreadas: string; // nombres de lo que no se pudo cargar ('' si salió todo bien)
  motivo: string;
}

@Component({
  selector: 'app-confirmar-presencial',
  imports: [FormField, RouterLink],
  templateUrl: './confirmar-presencial.html',
  styleUrl: './confirmar-presencial.css',
})
export class ConfirmarPresencial {
  private readonly reservaService = inject(ReservaService);
  protected readonly seleccion = inject(SeleccionReserva);

  protected readonly etiquetaTipo = ETIQUETA_TIPO_RECURSO;
  protected readonly etiquetaModalidad = ETIQUETA_MODALIDAD;
  protected readonly etiquetaEstado = ETIQUETA_ESTADO_RESERVA;
  protected readonly formatoPrecio = formatoPrecio;
  protected readonly formatoRango = formatoRango;

  protected readonly cliente = this.seleccion.clienteMostrador;

  // Cobrar ahora viene tildado: es lo habitual en el mostrador
  protected readonly modelo = signal({ cobrarAhora: true });
  protected readonly f = form(this.modelo);

  protected readonly descuento = signal(0); // % por pago en efectivo, lo define el administrador
  protected readonly enviando = signal(false);
  protected readonly errorEnvio = signal('');
  protected readonly resultado = signal<Resultado | null>(null);

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

  protected readonly lineas = computed(() => {
    const p = this.seleccion.periodo();
    if (!p) return [];
    const dias = this.seleccion.dias();
    return this.seleccion.elegidos().map((recurso) => ({
      recurso,
      total: totalRecurso(recurso, p.modalidad, p.fechaInicio, dias) ?? 0,
    }));
  });

  // Vista previa; el monto final lo calcula el backend
  protected readonly descuentoMonto = computed(() =>
    this.modelo().cobrarAhora ? Math.round(this.seleccion.total() * this.descuento()) / 100 : 0,
  );
  protected readonly aCobrar = computed(() => this.seleccion.total() - this.descuentoMonto());

  protected readonly puedeEnviar = computed(
    () => this.cliente() !== null && this.seleccion.periodo() !== null && !this.enviando(),
  );

  // Carpas y sombrillas primero; las cocheras después (la cochera exige un alojamiento ya reservado)
  private readonly grupos = computed(() =>
    [this.seleccion.alojamientos(), this.seleccion.cocheras()].filter((g) => g.length > 0),
  );

  constructor() {
    this.reservaService.configuracionPago().subscribe({
      next: (c) => this.descuento.set(c.descuentoEfectivo),
      error: () => this.descuento.set(0), // sin dato, no se muestra descuento (el backend aplica el real)
    });
  }

  protected async confirmar(evento: Event): Promise<void> {
    evento.preventDefault();
    const periodo = this.seleccion.periodo();
    const cliente = this.cliente();
    if (!periodo || !cliente || this.enviando()) return;

    this.enviando.set(true);
    this.errorEnvio.set('');

    const cobrarAhora = this.modelo().cobrarAhora;
    const grupos = this.grupos();
    const creadas: ReservaCreada[] = [];
    let motivo = '';

    for (const recursos of grupos) {
      try {
        const respuesta = await firstValueFrom(
          this.reservaService.crearPresencial({
            clientePublicId: cliente.publicId,
            fechaInicio: periodo.fechaInicio,
            fechaFin: periodo.fechaFin,
            modalidad: periodo.modalidad,
            recursosPublicIds: recursos.map((r) => r.publicId),
            cobrarAhora,
          }),
        );
        creadas.push({ respuesta, nombres: this.nombres(recursos) });
      } catch (error) {
        motivo = this.mensajeDeError(error);
        break; // si falla un grupo, no se intenta el siguiente
      }
    }
    this.enviando.set(false);

    if (creadas.length === 0) {
      this.errorEnvio.set(motivo);
      return;
    }

    const noCreadas = this.nombres(grupos.slice(creadas.length).flat());
    this.resultado.set({ cliente, creadas, noCreadas, motivo });
    this.seleccion.limpiar();
  }

  private nombres(recursos: RecursoCatalogo[]): string {
    return recursos.map((r) => r.nombre).join(', ');
  }

  private mensajeDeError(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 0) return 'No se pudo conectar con el servidor';
      if (error.status === 403) return 'No tenés permiso para esta operación. Cerrá sesión y volvé a ingresar.';
      if (error.status === 409) return 'Alguno de los recursos ya no está libre. Volvé al catálogo y buscá de nuevo.';
      if (error.error?.message) return error.error.message;
    }
    return 'Ocurrió un error inesperado. Intentá de nuevo';
  }
}

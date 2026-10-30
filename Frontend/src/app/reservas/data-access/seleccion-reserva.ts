import { computed, Service, signal } from '@angular/core';
import { Modalidad, RecursoCatalogo } from '../../recursos/data-access/recurso-model';

export interface Periodo {
  fechaInicio: string;
  fechaFin: string;
}

@Service()
export class SeleccionReserva {
readonly busqueda = signal<Busqueda>({ fechaInicio: '', fechaFin: '', modalidad: 'DIA' });
  /** fechas de la ultima búsqueda hecha: a ellas pertenece lo que se eligió. */
  
  readonly periodo = signal<Periodo | null>(null);

  readonly elegidos = signal<RecursoCatalogo[]>([]);

  readonly idsElegidos = computed(() => new Set(this.elegidos().map((r) => r.publicId)));
  readonly alojamientos = computed(() => this.elegidos().filter((r) => r.tipo !== 'COCHERA'));
  readonly cocheras = computed(() => this.elegidos().filter((r) => r.tipo === 'COCHERA'));

  
  // Regla del negocio: una cochera solo se reserva junto con una carpa o una sombrilla
  readonly cocheraSinAlojamiento = computed(
    () => this.cocheras().length > 0 && this.alojamientos().length === 0,
  );
  readonly puedeContinuar = computed(
    () => this.elegidos().length > 0 && !this.cocheraSinAlojamiento(),
  );

  alternar(recurso: RecursoCatalogo): void {
    this.elegidos.update((lista) =>
      lista.some((r) => r.publicId === recurso.publicId)
        ? lista.filter((r) => r.publicId !== recurso.publicId)
        : [...lista, recurso],
    );
  }

  quitar(publicId: string): void {
    this.elegidos.update((lista) => lista.filter((r) => r.publicId !== publicId));
  }

    vaciar(): void {
    this.elegidos.set([]);
  }
    /** Tras enviar la solicitud: se empieza de cero */
  limpiar(): void {
    this.elegidos.set([]);
    this.periodo.set(null);
    this.busqueda.set({ fechaInicio: '', fechaFin: '', modalidad: 'DIA' });
  }

  /**
   * Tras una búsqueda nueva: guarda las fechas y conserva solo los recursos elegidos que
   * siguen libres. Se reemplazan por los datos frescos (precios incluidos).
   * Devuelve cuántos se perdieron.
   */
  aplicarBusqueda(periodo: Periodo, disponibles: RecursoCatalogo[]): number {
    const ids = this.idsElegidos();
    const siguen = disponibles.filter((r) => ids.has(r.publicId));
    const perdidos = this.elegidos().length - siguen.length;
    this.periodo.set(periodo);
    this.elegidos.set(siguen);
    return perdidos;
  }
  
}
export interface Busqueda extends Periodo {
  modalidad: Modalidad;
}

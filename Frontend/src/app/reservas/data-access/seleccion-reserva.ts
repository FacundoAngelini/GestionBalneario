import { computed, Service, signal } from '@angular/core';
import { Modalidad, RecursoCatalogo } from '../../recursos/data-access/recurso-model';
import { diasEntre, totalRecurso } from './periodo-reserva';

/** Lo que el usuario tiene cargado en el buscador (se conserva al ir al login y volver). */
export interface Busqueda {
  fechaInicio: string; 
  fechaFin: string; // solo se usa con la modalidad Por dia
  modalidad: Modalidad;
}

/** La ultima busqueda realizada: a ella pertenece lo que se eligio. fechaFin es el fin reall de la reserva. */
export interface Periodo {
  fechaInicio: string;
  fechaFin: string;
  modalidad: Modalidad;
}

const BUSQUEDA_INICIAL: Busqueda = { fechaInicio: '', fechaFin: '', modalidad: 'DIA' };

@Service()
export class SeleccionReserva {
  readonly busqueda = signal<Busqueda>({ ...BUSQUEDA_INICIAL });
  readonly periodo = signal<Periodo | null>(null);
  readonly elegidos = signal<RecursoCatalogo[]>([]);

  readonly idsElegidos = computed(() => new Set(this.elegidos().map((r) => r.publicId)));
  readonly alojamientos = computed(() => this.elegidos().filter((r) => r.tipo !== 'COCHERA'));
  readonly cocheras = computed(() => this.elegidos().filter((r) => r.tipo === 'COCHERA'));

  readonly dias = computed(() => {
    const p = this.periodo();
    return p ? diasEntre(p.fechaInicio, p.fechaFin) : 0;
  });

  /** Total de todo lo elegido, con la regla del backend. */
  readonly total = computed(() => {
    const p = this.periodo();
    if (!p) return 0;
    const dias = this.dias();
    return this.elegidos().reduce(
      (suma, r) => suma + (totalRecurso(r, p.modalidad, p.fechaInicio, dias) ?? 0),
      0,
    );
  });

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

  /**
   * Tras una busqueda nueva: guarda el periodo y conserva solo los recursos elegidos que siguen
   * libres (reemplazados por los datos frescos). Devuelve cuántos se perdieron.
   */
  aplicarBusqueda(periodo: Periodo, disponibles: RecursoCatalogo[]): number {
    const ids = this.idsElegidos();
    const siguen = disponibles.filter((r) => ids.has(r.publicId));
    const perdidos = this.elegidos().length - siguen.length;
    this.periodo.set(periodo);
    this.elegidos.set(siguen);
    return perdidos;
  }

  /** Tras enviar la solicitud: se empieza de cero */
  limpiar(): void {
    this.elegidos.set([]);
    this.periodo.set(null);
    this.busqueda.set({ ...BUSQUEDA_INICIAL });
  }

   vaciar(): void {
    this.elegidos.set([]);
  }
}
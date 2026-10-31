import { aIso, desdeIso } from '../../core/utils/fechas';
import { Modalidad, RecursoCatalogo } from '../../recursos/data-access/recurso-model';

const MS_POR_DIA = 86_400_000;

const sumarDias = (fecha: Date, dias: number): Date =>
  new Date(fecha.getFullYear(), fecha.getMonth(), fecha.getDate() + dias);

//si el dia de ese mes no existee, se toma en cuenta el ultimo 
const sumarMeses = (fecha: Date, meses: number): Date => {
  const destino = new Date(fecha.getFullYear(), fecha.getMonth() + meses, 1);
  const ultimoDia = new Date(destino.getFullYear(), destino.getMonth() + 1, 0).getDate();
  destino.setDate(Math.min(fecha.getDate(), ultimoDia));
  return destino;
};

export const diasEntre = (inicio: string, fin: string): number => {
  const a = desdeIso(inicio);
  const b = desdeIso(fin);
  return a && b ? Math.round((b.getTime() - a.getTime()) / MS_POR_DIA) + 1 : 0;
};

//hasta que dia dura la reserva segun la modalidad
export function fechaFinEfectiva(
  modalidad: Modalidad,
  inicio: string,
  finBuscado: string,
  finTemporada: string | null,
): string | null {
  const desde = desdeIso(inicio);
  if (!desde) return null;
  switch (modalidad) {
    case 'DIA':
      return finBuscado;
    case 'QUINCENA':
      return aIso(sumarDias(desde, 14));
    case 'MES':
      return aIso(sumarDias(sumarMeses(desde, 1), -1));
    case 'TEMPORADA':
      return finTemporada;
  }
}

//precio que el backend toma
export const precioVigente = (
  recurso: RecursoCatalogo,
  modalidad: Modalidad,
  inicio: string,
): number | null =>
  recurso.precios?.find(
    (p) => p.modalidad === modalidad && p.fechaVigencia <= inicio && inicio <= p.fechaCaducada,
  )?.precio ?? null;

  /** Total de un recurso en el periodo (misma regla que ReservaServicio: solo por dia (multiplica por los dias). */
export function totalRecurso(
  recurso: RecursoCatalogo,
  modalidad: Modalidad,
  inicio: string,
  dias: number,
): number | null {
  const precio = precioVigente(recurso, modalidad, inicio);
  if (precio === null) return null;
  return modalidad === 'DIA' ? precio * dias : precio;
}

/**El cliente cancela hasta N horas antes de las 00:00 del primer día. */
export const plazoDeCancelacionVigente = (fechaInicio: string, horas: number): boolean => {
  const inicio = desdeIso(fechaInicio);
  return !!inicio && Date.now() <= inicio.getTime() - horas * 3_600_000;
};
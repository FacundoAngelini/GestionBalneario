import { Modalidad } from '../../recursos/data-access/recurso-model';

/** 'YYYY-MM-DD' → Date a medianoche LOCAL (no UTC). */
export function parsearIso(iso: string): Date {
  const [y, m, d] = iso.split('-').map(Number);
  return new Date(y, m - 1, d);
}

export const aIso = (d: Date): string =>
  `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;

export const desdeIso = (s: string): Date | null => {
  const [y, m, d] = s.split('-').map(Number);
  return y && m && d ? new Date(y, m - 1, d) : null;
};

export const hoyIso = (): string => aIso(new Date());

// "2026-12-01" -> "1 dic 2026"
export const formatoFecha = (iso: string): string =>
  desdeIso(iso)?.toLocaleDateString('es-AR', { day: 'numeric', month: 'short', year: 'numeric' }) ?? '';


function sumarDias(f: Date, n: number): Date {
  return new Date(f.getFullYear(), f.getMonth(), f.getDate() + n);
}

// Igual que LocalDate.plusMonths de Java: si el día no existe (31 → febrero), usa el último del mes.
function sumarMeses(f: Date, n: number): Date {
  const mes = f.getMonth() + n;
  const ultimoDia = new Date(f.getFullYear(), mes + 1, 0).getDate();
  return new Date(f.getFullYear(), mes, Math.min(f.getDate(), ultimoDia));
}

/** Vista previa de la fecha de fin. El backend es quien decide el valor real. */
export function calcularFechaFin(
  modalidad: Modalidad, inicioIso: string, finTemporadaIso: string, finDiaIso: string,
): string {
  const inicio = parsearIso(inicioIso);
  switch (modalidad) {
    case 'DIA': return finDiaIso;
    case 'QUINCENA': return aIso(sumarDias(inicio, 14));
    case 'MES': return aIso(sumarDias(sumarMeses(inicio, 1), -1));
    case 'TEMPORADA': return finTemporadaIso;
  }
}
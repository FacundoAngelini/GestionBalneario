import { Modalidad } from '../../recursos/data-access/recurso-model';

/** 'YYYY-MM-DD' → Date a medianoche LOCAL (no UTC). */
export function parsearIso(iso: string): Date {
  const [y, m, d] = iso.split('-').map(Number);
  return new Date(y, m - 1, d);
}

export function aIso(f: Date): string {
  const mm = String(f.getMonth() + 1).padStart(2, '0');
  const dd = String(f.getDate()).padStart(2, '0');
  return `${f.getFullYear()}-${mm}-${dd}`;
}

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
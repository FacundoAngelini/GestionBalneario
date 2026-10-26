export interface Pais {
  nombre: string;
  prefijo: string;
  min: number;
  max: number;
  ejemplo: string;
  ayuda: string;
}

export const PAISES: Pais[] = [
  { nombre: 'Argentina', prefijo: '54', min: 10, max: 10, ejemplo: '2235123456', ayuda: 'Sin 0 ni 15' },
  { nombre: 'Uruguay', prefijo: '598', min: 8, max: 8, ejemplo: '94231234', ayuda: 'Sin 0 inicial' },
  { nombre: 'Chile', prefijo: '56', min: 9, max: 9, ejemplo: '912345678', ayuda: 'Sin 0 inicial' },
  { nombre: 'Brasil', prefijo: '55', min: 10, max: 11, ejemplo: '11961234567', ayuda: 'Con código de área, sin 0' },
  { nombre: 'Paraguay', prefijo: '595', min: 7, max: 9, ejemplo: '961456789', ayuda: 'Sin 0 inicial' },
  { nombre: 'España', prefijo: '34', min: 9, max: 9, ejemplo: '612345678', ayuda: 'Sin espacios' },
];

export const buscarPais = (prefijo: string): Pais | undefined => PAISES.find((p) => p.prefijo === prefijo);
export type TipoRecurso = 'CARPA' | 'SOMBRILLA' | 'COCHERA';
export type Modalidad = 'DIA' | 'QUINCENA' | 'MES' | 'TEMPORADA';
export type TamanioSombrilla = 'CHICO' | 'MEDIANO' | 'GRANDE';

export const ETIQUETA_MODALIDAD: Record<Modalidad, string> = {
  DIA: 'Por día',
  QUINCENA: 'Quincena',
  MES: 'Mes',
  TEMPORADA: 'Temporada',
};

export const ETIQUETA_TIPO_RECURSO: Record<TipoRecurso, string> = {
  CARPA: 'Carpa',
  SOMBRILLA: 'Sombrilla',
  COCHERA: 'Cochera',
};

export interface PrecioModalidad {
  modalidad: Modalidad;
  precio: number;
  fechaVigencia: string;   // 'YYYY-MM-DD'
  fechaCaducada: string;
}

export interface RecursoCatalogo {
  publicId: string;
  nombre: string;
  tipo: TipoRecurso;
  sectorPublicId: string;
  sectorNombre: string;
  numero: number | null;
  pasillo: number | null;
  capacidad: number | null;
  tamanio: TamanioSombrilla | null;
  precios: PrecioModalidad[];
}

export interface RecursoLoteRequest {
  tipo: TipoRecurso;
  prefijoNombre: string;
  numeroDesde: number;
  numeroHasta: number;
  sectorPublicId: string;
  pasillo?: number;
  capacidad?: number;
  tamanio?: TamanioSombrilla;
  precios: PrecioModalidad[];
}

export interface RecursoLoteResponse { creados: number; }
export interface Sector { publicId: string; nombre: string; }
export interface Temporada { inicioTemporada: string; finTemporada: string; }

export const ETIQUETA_TIPO_PLURAL: Record<TipoRecurso, string> = {
  CARPA: 'Carpas',
  SOMBRILLA: 'Sombrillas',
  COCHERA: 'Cocheras',
};

export const ETIQUETA_TAMANIO: Record<TamanioSombrilla, string> = {
  CHICO: 'Chico',
  MEDIANO: 'Mediano',
  GRANDE: 'Grande',
};

// Un recurso tal como se ve en el inventario (los tres tipos, normalizados)
export interface RecursoInventario {
  publicId: string;
  tipo: TipoRecurso;
  nombre: string;
  esReservable: boolean;
  sectorPublicId: string;
  numero: number;
  pasillo: number | null; // solo carpas
  capacidad: number | null; // solo carpas
  tamanio: TamanioSombrilla | null; // solo sombrillas
}

// Lo que el administrador puede editar de un recurso
export interface DatosRecurso {
  nombre: string;
  esReservable: boolean;
  sectorPublicId: string;
  numero: number;
  pasillo: number;
  capacidad: number;
  tamanio: TamanioSombrilla;
}

// Un precio de un recurso para un periodo
export interface PrecioRecurso {
  publicId: string;
  recursoPublicId: string;
  modalidad: Modalidad;
  precio: number;
  fechaVigencia: string;
  fechaCaducada: string;
}

// Lo que se envía para crear o modificar un precio
export type PrecioRecursoRequest = Omit<PrecioRecurso, 'publicId'>;
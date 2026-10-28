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
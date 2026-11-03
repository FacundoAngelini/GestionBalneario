import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { map } from 'rxjs';
import { environment } from '../../core/environments/environment';
import {
  AjustePreciosRequest,
  DatosRecurso,
  PrecioRecurso,
  PrecioRecursoRequest,
  RecursoCatalogo,
  RecursoInventario,
  RecursoLoteRequest,
  RecursoLoteResponse,
  ResultadoAjuste,
  Sector,
  TamanioSombrilla,
  Temporada,
  TipoRecurso,
} from './recurso-model';

// Un endpoint por tipo de recurso
const RUTA_TIPO: Record<TipoRecurso, string> = {
  CARPA: 'recursos-carpas',
  SOMBRILLA: 'recursos-sombrillas',
  COCHERA: 'recursos-cocheras',
};

// Lo que devuelve el backend (los tres DTO juntos; cada tipo trae solo lo suyo)
interface RecursoDto {
  publicId: string;
  nombre: string;
  esReservable: boolean;
  sectorPublicId: string;
  numero?: number; // carpa y sombrilla
  numeroCochera?: number; // cochera
  pasillo?: number;
  capacidad?: number;
  tamanio?: TamanioSombrilla;
}

const aInventario = (tipo: TipoRecurso, r: RecursoDto): RecursoInventario => ({
  publicId: r.publicId,
  tipo,
  nombre: r.nombre,
  esReservable: r.esReservable,
  sectorPublicId: r.sectorPublicId,
  numero: (tipo === 'COCHERA' ? r.numeroCochera : r.numero) ?? 0,
  pasillo: r.pasillo ?? null,
  capacidad: r.capacidad ?? null,
  tamanio: r.tamanio ?? null,
});

// El PUT de cada tipo espera su propio DTO
const aPeticion = (tipo: TipoRecurso, d: DatosRecurso) => {
  const base = {
    nombre: d.nombre.trim(),
    esReservable: d.esReservable,
    sectorPublicId: d.sectorPublicId,
  };
  switch (tipo) {
    case 'CARPA':
      return { ...base, numero: d.numero, pasillo: d.pasillo, capacidad: d.capacidad };
    case 'SOMBRILLA':
      return { ...base, numero: d.numero, tamanio: d.tamanio };
    case 'COCHERA':
      return { ...base, numeroCochera: d.numero };
  }
};

@Service()
export class RecursoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  catalogo(fechaInicio: string, fechaFin: string, tipo?: TipoRecurso) {
    let params = new HttpParams().set('fechaInicio', fechaInicio).set('fechaFin', fechaFin);
    if (tipo) params = params.set('tipo', tipo);
    return this.http.get<RecursoCatalogo[]>(`${this.apiUrl}/recursos/catalogo`, { params });
  }

  crearLote(lote: RecursoLoteRequest) {
    return this.http.post<RecursoLoteResponse>(`${this.apiUrl}/recursos/lote`, lote);
  }

  inventario(tipo: TipoRecurso) {
    return this.http
      .get<RecursoDto[]>(`${this.apiUrl}/${RUTA_TIPO[tipo]}`)
      .pipe(map((lista) => lista.map((r) => aInventario(tipo, r))));
  }

  actualizar(tipo: TipoRecurso, publicId: string, datos: DatosRecurso) {
    return this.http.put<unknown>(
      `${this.apiUrl}/${RUTA_TIPO[tipo]}/${publicId}`,
      aPeticion(tipo, datos),
    );
  }

  listarSectores() {
    return this.http.get<Sector[]>(`${this.apiUrl}/sectores`);
  }

  obtenerTemporada() {
    return this.http.get<Temporada>(`${this.apiUrl}/temporada`);
  }

    listarPrecios() {
    return this.http.get<PrecioRecurso[]>(`${this.apiUrl}/recursos-precios`);
  }

  crearPrecio(precio: PrecioRecursoRequest) {
    return this.http.post<PrecioRecurso>(`${this.apiUrl}/recursos-precios`, precio);
  }

  actualizarPrecio(publicId: string, precio: PrecioRecursoRequest) {
    return this.http.put<PrecioRecurso>(`${this.apiUrl}/recursos-precios/${publicId}`, precio);
  }

    ajustarPrecios(pedido: AjustePreciosRequest) {
    return this.http.post<ResultadoAjuste>(`${this.apiUrl}/recursos-precios/ajuste-masivo`, pedido);
  }
}


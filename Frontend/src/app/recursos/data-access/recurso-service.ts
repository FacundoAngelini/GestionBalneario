import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { environment } from '../../core/environments/environment';
import {
  RecursoCatalogo, RecursoLoteRequest, RecursoLoteResponse,
  Sector, Temporada, TipoRecurso,
} from './recurso-model';

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

  listarSectores() {
    return this.http.get<Sector[]>(`${this.apiUrl}/sectores`);
  }

  obtenerTemporada() {
    return this.http.get<Temporada>(`${this.apiUrl}/temporada`);
  }
}
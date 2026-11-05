import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { environment } from '../../core/environments/environment';
import {
  CambioEstadoClienteRequest,
  Cliente,
  ClienteAltaRequest,
  ClienteDatosRequest,
  ClienteFicha,
  EventoCliente,
} from './cliente-model';

@Service()
export class ClienteService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/clientes/gestion`;

  /** activo: true = solo activos, false = solo dados de baja, undefined = todos. */
  listar(texto: string, activo?: boolean) {
    let params = new HttpParams();
    if (texto.trim()) params = params.set('texto', texto.trim());
    if (activo !== undefined) params = params.set('activo', activo);
    return this.http.get<Cliente[]>(this.apiUrl, { params });
  }

  ficha(publicId: string) {
    return this.http.get<ClienteFicha>(`${this.apiUrl}/${publicId}`);
  }

  crear(pedido: ClienteAltaRequest) {
    return this.http.post<ClienteFicha>(this.apiUrl, pedido);
  }

  actualizar(publicId: string, pedido: ClienteDatosRequest) {
    return this.http.put<ClienteFicha>(`${this.apiUrl}/${publicId}`, pedido);
  }

  cambiarEstado(publicId: string, pedido: CambioEstadoClienteRequest) {
    return this.http.put<ClienteFicha>(`${this.apiUrl}/${publicId}/estado`, pedido);
  }

  historial(publicId: string) {
    return this.http.get<EventoCliente[]>(`${this.apiUrl}/${publicId}/historial`);
  }
}
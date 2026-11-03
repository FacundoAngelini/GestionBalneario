import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { environment } from '../../core/environments/environment';
import {
  CambioEstadoRequest,
  Empleado,
  EmpleadoAltaRequest,
  EmpleadoUpdateRequest,
  EventoEmpleado,
} from './empleado-model';

@Service()
export class EmpleadoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/empleados`;

  listar() {
    return this.http.get<Empleado[]>(this.apiUrl); // sin filtro: todos los estados
  }

  buscar(publicId: string) {
    return this.http.get<Empleado>(`${this.apiUrl}/${publicId}`);
  }

  crear(pedido: EmpleadoAltaRequest) {
    return this.http.post<Empleado>(this.apiUrl, pedido);
  }

  actualizar(publicId: string, pedido: EmpleadoUpdateRequest) {
    return this.http.put<Empleado>(`${this.apiUrl}/${publicId}`, pedido);
  }

  cambiarEstado(publicId: string, pedido: CambioEstadoRequest) {
    return this.http.put<Empleado>(`${this.apiUrl}/${publicId}/estado`, pedido);
  }

  historial(publicId: string) {
    return this.http.get<EventoEmpleado[]>(`${this.apiUrl}/${publicId}/historial`);
  }
}
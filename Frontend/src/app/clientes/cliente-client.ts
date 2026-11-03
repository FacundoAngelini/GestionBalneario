import { HttpClient } from '@angular/common/http';
import { Service, inject } from '@angular/core';

import { environment } from '../../app/core/environments/environment';
import {
  Cliente,
  ClienteUpdateRequest,
  CambioContraseniaRequest,
  CambioNombreUsuarioRequest,
} from './cliente.model';

@Service()
export class ClienteClient {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/clientes`;

  
  actualizarMiPerfil(datos: ClienteUpdateRequest) {
    return this.http.patch<Cliente>(`${this.apiUrl}/me`, datos);
  }

  cambiarContrasenia(publicId: string, datos: CambioContraseniaRequest) {
    return this.http.patch<void>(`${this.apiUrl}/${publicId}/contrasenia`, datos);
  }

  cambiarNombreUsuario(publicId: string, datos: CambioNombreUsuarioRequest) {
    return this.http.patch<void>(`${this.apiUrl}/${publicId}/nombre-usuario`, datos);
  }
}
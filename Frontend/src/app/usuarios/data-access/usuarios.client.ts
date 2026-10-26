import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { environment } from '../../core/environments/environment';
import {
  ActualizarPerfilRequest,
  CambioContraseniaRequest,
  CambioNombreUsuarioRequest,
  PerfilCliente,
  PerfilUsuario,
} from './usuario-model';

@Service()
export class UsuarioClient {
  private readonly http = inject(HttpClient);
  private readonly usuariosUrl = `${environment.apiUrl}/usuarios`;
  private readonly clientesUrl = `${environment.apiUrl}/clientes`;

  obtenerMiPerfil() {
    return this.http.get<PerfilUsuario>(`${this.usuariosUrl}/me`);
  }

  actualizarMiPerfil(datos: ActualizarPerfilRequest) {
    return this.http.patch<PerfilCliente>(`${this.clientesUrl}/me`, datos);
  }

  cambiarContrasenia(clientePublicId: string, datos: CambioContraseniaRequest) {
    return this.http.patch<void>(`${this.clientesUrl}/${clientePublicId}/contrasenia`, datos);
  }

  cambiarNombreUsuario(clientePublicId: string, datos: CambioNombreUsuarioRequest) {
    return this.http.patch<void>(`${this.clientesUrl}/${clientePublicId}/nombre-usuario`, datos);
  }
}
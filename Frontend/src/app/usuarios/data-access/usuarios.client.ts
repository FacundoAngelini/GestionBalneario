import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { environment } from '../../core/environments/environment';
import {
  ActualizarPerfilRequest,
  CambioContraseniaRequest,
  CambioNombreUsuarioRequest,
  PerfilCliente,
  PerfilEmpleado,
  PerfilUsuario,
} from './usuario-model';

@Service()
export class UsuarioClient {
  private readonly http = inject(HttpClient);
  private readonly usuariosUrl = `${environment.apiUrl}/usuarios`;
  private readonly clientesUrl = `${environment.apiUrl}/clientes`;
  private readonly empleadosUrl = `${environment.apiUrl}/empleados`;

  obtenerMiPerfil() {
    return this.http.get<PerfilUsuario>(`${this.usuariosUrl}/me`);
  }

  actualizarMiPerfil(datos: ActualizarPerfilRequest) {
    return this.http.patch<PerfilCliente>(`${this.clientesUrl}/me`, datos);
  }

  actualizarMiFichaEmpleado(datos: { email: string; telefono: string }) {
    return this.http.patch<PerfilEmpleado>(`${this.empleadosUrl}/me`, datos);
  }

  cambiarContrasenia(datos: CambioContraseniaRequest) {
    return this.http.patch<void>(`${this.usuariosUrl}/me/contrasenia`, datos);
  }

  cambiarNombreUsuario(datos: CambioNombreUsuarioRequest) {
    return this.http.patch<void>(`${this.usuariosUrl}/me/nombre-usuario`, datos);
  }

  darmeDeBaja(datos: { motivo: string }) {
    return this.http.post<void>(`${this.clientesUrl}/me/baja`, datos);
  }
}
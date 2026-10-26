import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';

import { environment } from '../../core/environments/environment';
import { PerfilUsuario } from './usuario-model';

@Service()
export class UsuarioClient {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/usuarios`;

  obtenerMiPerfil() {
    return this.http.get<PerfilUsuario>(`${this.apiUrl}/me`);
  }
}
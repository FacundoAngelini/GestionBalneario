import { HttpClient } from '@angular/common/http';
import { computed, inject, Service, signal } from '@angular/core';
import { environment } from '../../core/environments/environment';
import { AuthResponse, LoginRequest, RegistroClienteRequest } from './auth-model';
import { finalize, tap } from 'rxjs';
import { extraerRoles, leerPayload, tokenVencido } from './jwt.utils';

const TOKEN_KEY = 'jwt_token';

@Service()
export class AuthClient {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/auth`;

  private readonly token = signal<string | null>(this.leerTokenGuardado()); //la uncia fuente de la verdad
  readonly accessToken = this.token.asReadonly();

  readonly usuarioLogueado = computed(() => this.token() !== null);
  readonly roles = computed(() => extraerRoles(this.token()));
  readonly nombreUsuario = computed(() => {
    const token = this.token();
    return token ? (leerPayload(token)?.sub ?? '') : '';
  });

  login(credenciales: LoginRequest) {
    return this.http
    .post<AuthResponse>(`${this.apiUrl}/login`, credenciales)
    .pipe(tap(respuesta => this.guardarSesion(respuesta.token)));
  }

  registro(datos: RegistroClienteRequest) {
    return this.http.post(`${this.apiUrl}/register`, datos);
  }

logout() {
    this.http.post<void>(`${this.apiUrl}/logout`, {}).subscribe({ error: () => {} });
    this.limpiarSesion();
  }

  private guardarSesion(token: string) {
    localStorage.setItem(TOKEN_KEY, token);
    this.token.set(token);
  }

    limpiarSesion() {
    localStorage.removeItem(TOKEN_KEY);
    this.token.set(null);
  }

  private leerTokenGuardado(): string | null {
    const guardado = localStorage.getItem(TOKEN_KEY);
    if (guardado && tokenVencido(guardado)) {
      localStorage.removeItem(TOKEN_KEY);
      return null;
    }
    return guardado;
  }
}



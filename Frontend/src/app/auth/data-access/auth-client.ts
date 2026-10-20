import { HttpClient } from '@angular/common/http';
import { inject, Service, signal } from '@angular/core';
import { environment } from '../../core/environments/environment';
import { AuthResponse, LoginRequest, RegistroClienteRequest } from './auth-model';
import { tap } from 'rxjs';

@Service()
export class AuthClient {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/auth`;

  //para ver en que estado de sesion esta 
  usuarioLogueado = signal<boolean>(this.hasToken());

  login(credentials: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(res => this.setSession(res))
    );
  }

  registro(datos: RegistroClienteRequest) {
    return this.http.post(`${this.apiUrl}/register`, datos);
  }

  logout() {
    return this.http.post(`${this.apiUrl}/logout`, {}).pipe(
      tap(() => {
        localStorage.removeItem('jwt_token');
        this.usuarioLogueado.set(false);
      })
    );
  }

  private setSession(authResult: AuthResponse) {
    localStorage.setItem('jwt_token', authResult.token);
    this.usuarioLogueado.set(true);
  }

  private hasToken(): boolean {
    return !!localStorage.getItem('jwt_token');
  }

  obtenerRolesUsuario(): string[] {
    const token = localStorage.getItem('jwt_token');
    if (!token) return [];

    try {
      // El JWT tiene 3 partes separadas por punto. El payload es la segunda [1].
      const payloadBase64 = token.split('.')[1];
      const payloadDecoded = JSON.parse(atob(payloadBase64));
      
      // Spring Security / JWT suele guardar los roles en un array
      return payloadDecoded.roles || [];
    } catch (error) {
      console.error('Error decodificando token', error);
      return [];
    }
  }
}


import { HttpClient } from '@angular/common/http';
import { computed, inject, Service, signal } from '@angular/core';
import { environment } from '../../core/environments/environment';
import { AuthResponse,ETIQUETA_ROL,LoginRequest, RegistroClienteRequest } from './auth-model';
import { finalize, tap } from 'rxjs';
import { extraerRoles, leerPayload, tokenVencido } from './jwt.utils';
import { ROLES, ROLES_GESTION_EMPLEADOS, ROLES_RESUELVEN_RESERVAS, ROLES_VER_RESERVAS } from './roles.constants';

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
    readonly publicId = computed(() => {
    const token = this.token();
    return token ? (leerPayload(token)?.publicId ?? null) : null;
  });
  readonly esCliente = computed(() => this.roles().includes(ROLES.CLIENTE));
  readonly esPersonal = computed(() => this.tieneAlgunRol(ROLES_RESUELVEN_RESERVAS));
  readonly esAdmin = computed(() => this.roles().includes(ROLES.ADMIN));


    readonly restringido = computed(() => this.roles().includes(ROLES.RESTRINGIDO));
  readonly estadoEmpleado = computed(() => {
    const token = this.token();
    return token ? (leerPayload(token)?.estadoEmpleado ?? null) : null;
  });
  readonly etiquetaRol = computed(() => {
    const token = this.token();
    const rol = this.restringido() ? (token ? leerPayload(token)?.rolOriginal : undefined) : this.roles()[0];
    return rol ? (ETIQUETA_ROL[rol] ?? '') : '';
  });
  readonly esCarpero = computed(() => this.roles().includes(ROLES.CARPERO));
  readonly puedeVerReservas = computed(() => this.tieneAlgunRol(ROLES_VER_RESERVAS));
  readonly puedeGestionarEmpleados = computed(() => this.tieneAlgunRol(ROLES_GESTION_EMPLEADOS));

  tieneAlgunRol(grupo: readonly string[]): boolean {
    return this.roles().some((rol) => grupo.includes(rol));
  }

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



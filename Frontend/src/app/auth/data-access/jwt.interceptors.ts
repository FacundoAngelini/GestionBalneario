import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { environment } from '../../core/environments/environment';
import { AuthClient } from './auth-client';

// Endpoints públicos de auth: si reciben un token vencido, el backend responde 401
// aunque no lo necesiten. /auth/logout NO está acá: sí necesita el token.
const RUTAS_PUBLICAS = [
  '/auth/login',
  '/auth/register',
  '/auth/refresh',
  '/auth/contrasenia-olvidada',
  '/auth/resetear-contrasenia',
];

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(AuthClient).accessToken();
  const esDeNuestraApi = req.url.startsWith(environment.apiUrl);
  const esPublica = RUTAS_PUBLICAS.some((ruta) => req.url.startsWith(`${environment.apiUrl}${ruta}`));

  if (!token || !esDeNuestraApi || esPublica) {
    return next(req);
  }
  return next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
};
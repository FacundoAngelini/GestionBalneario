import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { ROUTE_SEGMENTS } from '../../core/constants/route-segments.constants';
import { environment } from '../../core/environments/environment';
import { AuthClient } from './auth-client';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthClient);
  const router = inject(Router);
  const snackBar = inject(MatSnackBar);

  return next(req).pipe(
    catchError((error: unknown) => {
      const esDeAuth = req.url.startsWith(`${environment.apiUrl}/auth/`);
      if (error instanceof HttpErrorResponse && error.status === 401 && !esDeAuth && auth.usuarioLogueado()) {
        auth.limpiarSesion();
        snackBar.open('Tu sesión venció. Iniciá sesión de nuevo.', 'Cerrar', { duration: 6000 });
        router.navigateByUrl(`/${ROUTE_SEGMENTS.auth}/${ROUTE_SEGMENTS.login}`);
      }
      return throwError(() => error);
    }),
  );
};
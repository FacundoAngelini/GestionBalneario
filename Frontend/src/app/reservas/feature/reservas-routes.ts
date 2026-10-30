import { Routes } from '@angular/router';
import { ReservasLayout } from '../UI/reservas-layout/reservas-layout';
import { ROUTE_SEGMENTS } from '../../core/constants/route-segments.constants';
import { roleGuard } from '../../auth/data-access/auth.guard';
import { seleccionGuard } from '../data-access/seleccion.guard';
import { ROLES } from '../../auth/data-access/roles.constants';
; // Verificar si la ruta relativa coincide en tu proyecto

export const reservasRoutes: Routes = [
  {
    path: ROUTE_SEGMENTS.sinPago,
    canActivate: [roleGuard, seleccionGuard],
    data: { roles: [ROLES.CLIENTE] },
    loadComponent: () =>
      import('./confirmar-solicitud/confirmar-solicitud').then((m) => m.ConfirmarSolicitud),
  },
];
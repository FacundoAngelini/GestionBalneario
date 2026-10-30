import { Routes } from '@angular/router';
import { ReservasLayout } from '../UI/reservas-layout/reservas-layout';
import { ROUTE_SEGMENTS } from '../../core/constants/route-segments.constants';
import { roleGuard } from '../../auth/data-access/auth.guard';
import { seleccionGuard } from '../data-access/seleccion.guard';
import { ROLES, ROLES_PERSONAL } from '../../auth/data-access/roles.constants';

export const reservasRoutes: Routes = [
  {
    path: ROUTE_SEGMENTS.sinPago,
    canActivate: [roleGuard, seleccionGuard],
    data: { roles: [ROLES.CLIENTE] },
    loadComponent: () =>
      import('./confirmar-solicitud/confirmar-solicitud').then((m) => m.ConfirmarSolicitud),
  },
  {
    path: ROUTE_SEGMENTS.misReservas,
    canActivate: [roleGuard],
    data: { roles: [ROLES.CLIENTE] },
    loadComponent: () =>
      import('./mis-reservas/mis-reservas').then((m) => m.MisReservas),
  },
  {
    path: ROUTE_SEGMENTS.gestionReservas,
    canActivate: [roleGuard],
    data: { roles: ROLES_PERSONAL },
    loadComponent: () =>
      import('./gestion-reservas/gestion-reservas').then((m) => m.GestionReservas),
  },
];
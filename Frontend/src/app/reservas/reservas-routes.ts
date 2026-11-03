import { Routes } from '@angular/router';
import { roleGuard } from '../auth/data-access/auth.guard';
import { ROLES, ROLES_PERSONAL, ROLES_VER_RESERVAS } from '../auth/data-access/roles.constants';
import { ROUTE_SEGMENTS } from '../core/constants/route-segments.constants';
import { mostradorGuard, seleccionGuard } from './data-access/seleccion.guard';

export const reservasRoutes: Routes = [
  {
    path: ROUTE_SEGMENTS.sinPago,
    canActivate: [roleGuard, seleccionGuard],
    data: { roles: [ROLES.CLIENTE] },
    loadComponent: () =>
      import('./feature/confirmar-solicitud/confirmar-solicitud').then((m) => m.ConfirmarSolicitud),
  },
  {
    path: ROUTE_SEGMENTS.misReservas,
    canActivate: [roleGuard],
    data: { roles: [ROLES.CLIENTE] },
    loadComponent: () => import('./feature/mis-reservas/mis-reservas').then((m) => m.MisReservas),
  },
  {
    path: ROUTE_SEGMENTS.gestionReservas,
    canActivate: [roleGuard],
    data: { roles: ROLES_VER_RESERVAS },
    loadComponent: () =>
      import('./feature/gestion-reservas/gestion-reservas').then((m) => m.GestionReservas),
  },
  {
    path: ROUTE_SEGMENTS.presencial,
    canActivate: [roleGuard],
    data: { roles: ROLES_PERSONAL },
    loadComponent: () =>
      import('./feature/presencial-cliente/presencial-cliente').then((m) => m.PresencialCliente),
  },
  {
    path: `${ROUTE_SEGMENTS.presencial}/${ROUTE_SEGMENTS.confirmar}`,
    canActivate: [roleGuard, mostradorGuard],
    data: { roles: ROLES_PERSONAL },
    loadComponent: () =>
      import('./feature/confirmar-presencial/confirmar-presencial').then((m) => m.ConfirmarPresencial),
  },
  {
    path: `${ROUTE_SEGMENTS.comprobante}/:id`,
    canActivate: [roleGuard],
    data: { roles: [...ROLES_PERSONAL, ROLES.CLIENTE] },
    loadComponent: () => import('./feature/comprobante/comprobante').then((m) => m.ComprobanteReserva),
  },
];

export default reservasRoutes;
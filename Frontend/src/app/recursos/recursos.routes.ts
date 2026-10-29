import { Routes } from '@angular/router';
import { roleGuard } from '../auth/data-access/auth.guard';
import { ROLES } from '../auth/data-access/roles.constants';
import { ROUTE_SEGMENTS } from '../core/constants/route-segments.constants';

export const recursosRoutes: Routes = [
  {
    path: ROUTE_SEGMENTS.cargaMasiva,
    canActivate: [roleGuard],
    data: { roles: [ROLES.ADMIN] },
    loadComponent: () =>
      import('./features/carga-masiva/carga-masiva').then((m) => m.CargaMasiva),
  },
];
import { Routes } from '@angular/router';
import { roleGuard } from '../auth/data-access/auth.guard';
import { ROLES, ROLES_PERSONAL } from '../auth/data-access/roles.constants';
import { ROUTE_SEGMENTS } from '../core/constants/route-segments.constants';

export const recursosRoutes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    canActivate: [roleGuard],
    data: { roles: ROLES_PERSONAL },
    loadComponent: () => import('./features/inventario/inventario').then((m) => m.Inventario),
  },
  {
    path: ROUTE_SEGMENTS.precios,
    canActivate: [roleGuard],
    data: { roles: [ROLES.ADMIN] },
    loadComponent: () =>
      import('./features/ajuste-precios/ajuste-precios').then((m) => m.AjustePrecios),
  },
  {
    path: ROUTE_SEGMENTS.cargaMasiva,
    canActivate: [roleGuard],
    data: { roles: [ROLES.ADMIN] },
    loadComponent: () =>
      import('./features/carga-masiva/carga-masiva').then((m) => m.CargaMasiva),
  },
];
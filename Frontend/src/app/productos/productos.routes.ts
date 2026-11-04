import { Routes } from '@angular/router';
import { roleGuard } from '../auth/data-access/auth.guard';
import { ROLES_PERSONAL } from '../auth/data-access/roles.constants';
import { ROUTE_SEGMENTS } from '../core/constants/route-segments.constants';

export const productosRoutes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    loadComponent: () => import('./feature/menu/menu').then((m) => m.Menu),
  },
  {
    path: ROUTE_SEGMENTS.cargarProducto,
    canActivate: [roleGuard],
    data: { roles: ROLES_PERSONAL },
    loadComponent: () =>
      import('./feature/cargar-producto/cargar-producto').then((m) => m.CargarProducto),
  },
  {
    path: ':id',
    loadComponent: () => import('./feature/detalle/detalle').then((m) => m.Detalle),
  },
];

export default productosRoutes;

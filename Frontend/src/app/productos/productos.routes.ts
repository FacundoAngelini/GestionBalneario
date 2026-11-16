import { Routes, UrlMatchResult, UrlSegment } from '@angular/router';
import { roleGuard } from '../auth/data-access/auth.guard';
import { ROLES_PERSONAL } from '../auth/data-access/roles.constants';
import { ROUTE_SEGMENTS } from '../core/constants/route-segments.constants';

// Ruta de detalle `:id` que no captura el segmento reservado de carga
const detalleMatcher = (segments: UrlSegment[]): UrlMatchResult | null =>
  segments.length === 1 && segments[0].path !== ROUTE_SEGMENTS.cargarProducto
    ? { consumed: segments, posParams: { id: segments[0] } }
    : null;

// PÃºblicas: menÃº y detalle, sin sesiÃ³n
export const productosPublicRoutes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    loadComponent: () => import('./feature/menu/menu').then((m) => m.Menu),
  },
  {
    matcher: detalleMatcher,
    loadComponent: () => import('./feature/detalle/detalle').then((m) => m.Detalle),
  },
];

// Privadas: solo personal
export const productosPrivateRoutes: Routes = [
  {
    path: ROUTE_SEGMENTS.cargarProducto,
    canActivate: [roleGuard],
    data: { roles: ROLES_PERSONAL },
    loadComponent: () =>
      import('./feature/cargar-producto/cargar-producto').then((m) => m.CargarProducto),
  },
];

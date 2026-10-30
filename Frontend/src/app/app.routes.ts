import { Routes } from '@angular/router';
import { authGuard, guestGuard } from './auth/data-access/auth.guard';
import { ROUTE_SEGMENTS } from './core/constants/route-segments.constants';

export const routes: Routes = [
  {
    path: ROUTE_SEGMENTS.auth,
    canActivate: [guestGuard],
    loadChildren: () => import('./auth/features/auth.routes').then(m => m.authRoutes)
  },

  // Pantalla de error
  {
    path: ROUTE_SEGMENTS.unauthorized,
    loadComponent: () => import('./core/UI/unauthorized').then(m => m.Unauthorized)
  },

  // Zona pública: no pide sesión (inicio con fotos y servicios)
  {
    path: '',
    loadComponent: () => import('./shared/public-layout/public-layout').then(m => m.PublicLayout),
    children: [
      {
        path: '',
        pathMatch: 'full',
        loadComponent: () => import('./core/UI/inicio/inicio').then(m => m.Inicio)
      },
      {
        path: ROUTE_SEGMENTS.catalogo,
        loadComponent: () =>
        import('./recursos/features/catalogo/catalogo').then((m) => m.Catalogo)
      }
    ]
  },

  // Zona privada: pide sesión
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./shared/main/main').then(m => m.Main),
    children: [
      {
        path: ROUTE_SEGMENTS.panel,
        loadComponent: () => import('./core/UI/panel/panel').then(m => m.Panel)
      },
      {
        path: ROUTE_SEGMENTS.perfil,
        loadComponent: () => import('./usuarios/feature/perfil/perfil').then(m => m.Perfil)
      },
      {
        path: ROUTE_SEGMENTS.recursos,
        loadChildren: () => import('./recursos/recursos.routes').then(m => m.recursosRoutes)
      }
    ]
  },
  {
    path: '**',
    loadComponent: () => import('./core/UI/not-found/not-found').then(m => m.NotFound)
  }
];
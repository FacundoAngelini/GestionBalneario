import { Routes } from '@angular/router';
import { ROUTE_SEGMENTS } from './core/constanst/route-segments.constants';
import { authGuard, guestGuard } from './auth/data-access/auth.guard';

export const routes: Routes = [
{
    path: ROUTE_SEGMENTS.auth,
    canActivate: [guestGuard],
    loadChildren: () => import('./auth/features/auth.routes').then(m => m.authRoutes)
  },

  //pantalla de error
  {
    path: ROUTE_SEGMENTS.unauthorized,
    loadComponent: () => import('./core/UI/Unauthorized').then(m => m.unauthorized)
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./shared/main/main').then(m => m.Main),
    children: [
      {
        path: ROUTE_SEGMENTS.inicio,
        loadComponent: () => import('./core/UI/inicio/inicio').then(m => m.Inicio)
      }
    ]
  },
   //temporla
  {
    path: '',
    pathMatch: 'full',
    redirectTo: ROUTE_SEGMENTS.inicio
  },
  {
    path: '**',
    redirectTo: ROUTE_SEGMENTS.inicio
  }
];

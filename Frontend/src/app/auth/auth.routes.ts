import { Routes } from '@angular/router';
import { ROUTE_SEGMENTS } from '../core/constanst/route-segments.constants';

export const authRoutes: Routes = [
  {
    path: '',
    redirectTo: ROUTE_SEGMENTS.login,
    pathMatch: 'full',
  },
  {
    path: ROUTE_SEGMENTS.login,
    loadComponent: () => import('./data-access/auth-login').then(m => m.Login),
  },
  {
    path: ROUTE_SEGMENTS.registro,
    loadComponent: () => import('./data-access/auth-registro').then(m => m.Registro),
  }
];

export default authRoutes;
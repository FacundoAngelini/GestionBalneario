import { Routes } from '@angular/router';
import { ROUTE_SEGMENTS } from './core/constanst/route-segments.constants';
import { guestGuard } from './auth/data-access/auth.guard';

export const routes: Routes = [
{
    path: ROUTE_SEGMENTS.auth,
    canActivate: [guestGuard],
    loadChildren: () => import('./auth/auth.routes').then(m => m.authRoutes)
  },

  //pantalla de error
  {
    path: ROUTE_SEGMENTS.unauthorized,
    loadComponent: () => import('./core/UI/Unauthorized').then(m => m.Unauthorized)
  },
   //temporla
  {
    path: '',
    pathMatch: 'full',
    redirectTo: `${ROUTE_SEGMENTS.auth}/${ROUTE_SEGMENTS.login}`
  },
  {
    path: '**',
    redirectTo: `${ROUTE_SEGMENTS.auth}/${ROUTE_SEGMENTS.login}`
  }
];

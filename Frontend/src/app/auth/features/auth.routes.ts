import { Routes } from '@angular/router';
import { AuthLayout } from '../UI/auth-layout';

export const authRoutes: Routes = [
  {
    path: '',
    component: AuthLayout,
    children: [
      { path: '', redirectTo: 'login', pathMatch: 'full' },
      {
        path: 'login',
        loadComponent: () => import('../features/auth-login').then(m => m.Login)
      },
      {
        path: 'registro',
        loadComponent: () => import('../features/auth-registro').then(m => m.Registro)
      }
      
    ]
  }
];

export default authRoutes;
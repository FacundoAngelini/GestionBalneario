import { Routes } from '@angular/router';
import { ReservasLayout } from '../UI/reservas-layout/reservas-layout';

export const reservasRoutes: Routes = [
  {
    path: '',
    component: ReservasLayout,
    children: [
      {
        path: 'crearReserva',
        loadComponent: () => import('../feature/reserva-form/reserva-form').then(m => m.ReservaForm)
      },
      {
        path: 'listarReservas',
        loadComponent: () => import('../feature/reserva-list/reserva-list').then(m => m.ReservaList)
      }
    ]
  }
];

export default reservasRoutes;
import { Routes } from '@angular/router';
import { roleGuard } from '../auth/data-access/auth.guard';
import { ROLES_PERSONAL } from '../auth/data-access/roles.constants';
import { ROUTE_SEGMENTS } from '../core/constants/route-segments.constants';

const acceso = { canActivate: [roleGuard], data: { roles: ROLES_PERSONAL } };

export const clientesRoutes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    ...acceso,
    loadComponent: () => import('./feature/clientes-lista/clientes-lista').then((m) => m.ClientesLista),
  },
  {
    path: ROUTE_SEGMENTS.nuevo, // antes de ":id", si no "nuevo" se tomaría como un id
    ...acceso,
    loadComponent: () => import('./feature/cliente-form/cliente-form').then((m) => m.ClienteForm),
  },
  {
    path: ':id',
    ...acceso,
    loadComponent: () => import('./feature/cliente-ficha/cliente-ficha').then((m) => m.ClienteFicha),
  },
  {
    path: `:id/${ROUTE_SEGMENTS.editar}`,
    ...acceso,
    loadComponent: () => import('./feature/cliente-form/cliente-form').then((m) => m.ClienteForm),
  },
];
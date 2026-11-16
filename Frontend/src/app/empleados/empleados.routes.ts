import { Routes } from '@angular/router';
import { roleGuard } from '../auth/data-access/auth.guard';
import { ROLES_GESTION_EMPLEADOS } from '../auth/data-access/roles.constants';
import { ROUTE_SEGMENTS } from '../core/constants/route-segments.constants';

const acceso = { canActivate: [roleGuard], data: { roles: ROLES_GESTION_EMPLEADOS } };

export const empleadosRoutes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    ...acceso,
    loadComponent: () => import('./feature/empleados-lista/empleados-lista').then((m) => m.EmpleadosLista),
  },
  {
    path: ROUTE_SEGMENTS.nuevo, // antes de ":id", si no "nuevo" se tomaría como un id
    ...acceso,
    loadComponent: () => import('./feature/empleados-form/empleados-form').then((m) => m.EmpleadoForm),
  },
  {
    path: ':id',
    ...acceso,
    loadComponent: () => import('./feature/empleados-ficha/empleados-ficha').then((m) => m.EmpleadoFicha),
  },
  {
    path: `:id/${ROUTE_SEGMENTS.editar}`,
    ...acceso,
    loadComponent: () => import('./feature/empleados-form/empleados-form').then((m) => m.EmpleadoForm),
  },
];
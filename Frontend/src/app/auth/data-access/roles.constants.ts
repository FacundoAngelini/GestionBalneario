export const ROLES = {
  ADMIN: 'ROLE_ADMIN',
  GERENTE: 'ROLE_GERENTE',
  ADMINISTRACION: 'ROLE_ADMINISTRACION',
  CARPERO: 'ROLE_CARPERO',
  CLIENTE: 'ROLE_CLIENTE',
  RESTRINGIDO: 'ROLE_RESTRINGIDO', 
} as const;

// Personal con pantallas de gestión: ven reservas e inventario
export const ROLES_PERSONAL = [ROLES.ADMIN, ROLES.GERENTE, ROLES.ADMINISTRACION];
export const ROLES_RESUELVEN_RESERVAS = ROLES_PERSONAL;
// El carpero también ve las reservas, pero sin ninguna acción
export const ROLES_VER_RESERVAS = [...ROLES_PERSONAL, ROLES.CARPERO];
export const ROLES_GESTION_EMPLEADOS = [ROLES.ADMIN, ROLES.GERENTE];
export interface LoginRequest {
  nombreUsuario: string;
  contrasenia: string;
}

export interface RegistroClienteRequest {
  nombreUsuario: string;
  contrasenia: string;
  nombre: string;
  apellido: string;
  email: string;
  telefono: string;
  dni: number;
}

export interface AuthResponse {
  token: string;
  refeshToken: string; // nombre del campo devuelto por el backend en openapi
}

// Nombre legible de cada rol del backend (enum Roles)
export const ETIQUETA_ROL: Record<string, string> = {
  ROLE_ADMIN: 'Administrador',
  ROLE_GERENTE: 'Gerente',
  ROLE_ADMINISTRACION: 'Administración',
  ROLE_CAJERO: 'Cajero',
  ROLE_MOZO: 'Mozo',
  ROLE_REPARTIDOR: 'Repartidor',
  ROLE_EMPLEADO: 'Empleado',
  ROLE_CLIENTE: 'Cliente',
};
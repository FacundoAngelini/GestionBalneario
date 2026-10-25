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
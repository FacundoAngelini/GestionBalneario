export interface Cliente {
  publicId: string;
  nombre: string;
  apellido: string;
  dni: number;
  email: string;
  telefono: string;
  fechaAlta: string;
  estado: boolean;
}
export interface ClienteUpdateRequest {
  nombre: string;
  apellido: string;
  email: string;
  telefono: string;
  // DNI se omite a propósito: el backend no lo acepta en este endpoint
}
export interface CambioContraseniaRequest {
  contraseniaActual: string;
  nuevaContrasenia: string;
}
export interface CambioNombreUsuarioRequest {
  nuevoNombreUsuario: string;
}
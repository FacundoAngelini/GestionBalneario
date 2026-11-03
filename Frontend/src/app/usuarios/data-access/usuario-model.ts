export type TipoPerfilUsuario =
  | 'CLIENTE'
  | 'EMPLEADO'
  | 'ADMINISTRADOR'
  | 'CUENTA';

export const ETIQUETA_TIPO_PERFIL: Record<TipoPerfilUsuario, string> = {
  CLIENTE: 'Cliente',
  EMPLEADO: 'Empleado',
  ADMINISTRADOR: 'Administrador',
  CUENTA: 'Cuenta',
};


export interface PerfilCliente {
  publicId: string;
  nombre: string;
  apellido: string;
  dni: number;
  email: string;
  telefono: string;
  fechaAlta: string;
  estado: boolean;
}

export interface DireccionEmpleado {
  calle: string;
  numero: number;
  ciudad: string;
  provincia: string;
}

export interface SectorEmpleado {
  publicId: string;
  nombre: string;
}

export interface RolEmpleado {
  publicId: string;
  tipoRol: string;
}

export interface PerfilEmpleado {
  publicId: string;
  nombre: string;
  apellido: string;
  dni: number;
  email: string;
  cuit: string;
  estadoEmpleado: string;
  telefono: string;
  direccion: DireccionEmpleado | null;
  sector: SectorEmpleado | null;
  rol: RolEmpleado | null;
}

export interface PerfilUsuario {
  nombreUsuario: string;
  roles: string[];
  tipoPerfil: TipoPerfilUsuario;
  cliente: PerfilCliente | null;
  empleado: PerfilEmpleado | null;
}


export interface ActualizarPerfilRequest {
  nombre: string;
  apellido: string;
  email: string;
  telefono: string;
}

export interface CambioContraseniaRequest {
  contraseniaActual: string;
  nuevaContrasenia: string;
}

export interface CambioNombreUsuarioRequest {
  nuevoNombreUsuario: string;
}
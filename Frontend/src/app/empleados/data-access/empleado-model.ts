import { DireccionEmpleado, RolEmpleado, SectorEmpleado } from '../../usuarios/data-access/usuario-model';

export type EstadoEmpleado = 'ACTIVO' | 'INACTIVO' | 'SUSPENDIDO' | 'VACACIONES' | 'LICENCIA';

export const ESTADOS_EMPLEADO: EstadoEmpleado[] = ['ACTIVO', 'VACACIONES', 'LICENCIA', 'SUSPENDIDO', 'INACTIVO'];

export const ETIQUETA_ESTADO_EMPLEADO: Record<EstadoEmpleado, string> = {
  ACTIVO: 'Activo',
  VACACIONES: 'De vacaciones',
  LICENCIA: 'De licencia',
  SUSPENDIDO: 'Suspendido',
  INACTIVO: 'Dado de baja',
  
};

// Clases de color definidas en styles.css
export const CLASE_ESTADO_EMPLEADO: Record<EstadoEmpleado, string> = {
  ACTIVO: 'estado--confirmada',
  VACACIONES: 'estado--finalizada',
  LICENCIA: 'estado--finalizada',
  SUSPENDIDO: 'estado--pendiente',
  INACTIVO: 'estado--cerrada',
};

/** Roles que se pueden elegir al dar de alta. Los administradores y los gerentes solo los crea un administrador. */
export interface RolOpcion {
  valor: string;
  etiqueta: string;
  soloAdmin: boolean;
}

export const ROLES_ALTA: RolOpcion[] = [
  { valor: 'ROLE_ADMIN', etiqueta: 'Administrador', soloAdmin: true },
  { valor: 'ROLE_GERENTE', etiqueta: 'Gerente', soloAdmin: true },
  { valor: 'ROLE_ADMINISTRACION', etiqueta: 'Administración', soloAdmin: false },
  { valor: 'ROLE_CARPERO', etiqueta: 'Carpero', soloAdmin: false },
  { valor: 'ROLE_CAJERO', etiqueta: 'Cajero', soloAdmin: false },
  { valor: 'ROLE_MOZO', etiqueta: 'Mozo', soloAdmin: false },
  { valor: 'ROLE_REPARTIDOR', etiqueta: 'Repartidor', soloAdmin: false },
];

/** Suspender, dar licencia, dar de baja y reactivar piden un motivo (5 a 300 caracteres). */
export const pideMotivo = (actual: EstadoEmpleado, nuevo: EstadoEmpleado): boolean =>
  nuevo === 'SUSPENDIDO' || nuevo === 'LICENCIA' || nuevo === 'INACTIVO' || actual === 'INACTIVO';

/** A qué estados se puede pasar desde el actual (una baja solo se reactiva). */
export const estadosDestino = (actual: EstadoEmpleado): EstadoEmpleado[] =>
  actual === 'INACTIVO' ? ['ACTIVO'] : ESTADOS_EMPLEADO.filter((e) => e !== actual);

export const MIN_MOTIVO = 5;
export const MAX_MOTIVO = 300;

/** Ficha de un empleado, tal como la devuelve el backend (lista y detalle). */
export interface Empleado {
  publicId: string;
  nombre: string;
  apellido: string;
  dni: number;
  email: string;
  sueldo: number | null; // null cuando quien consulta es el propio empleado
  cuit: string;
  estadoEmpleado: EstadoEmpleado;
  telefono: string;
  direccion: DireccionEmpleado | null;
  sector: SectorEmpleado | null;
  rol: RolEmpleado | null;
  usuarioPublicId: string | null;
  nombreUsuario: string | null;
  rolSeguridad: string | null;
  editable: boolean; // lo decide el backend: ¿quien consulta puede modificar a este empleado?
}

export interface EmpleadoUpdateRequest {
  nombre: string;
  apellido: string;
  dni: number;
  email: string;
  sueldo: number;
  cuit: string;
  telefono: string;
  direccion: { calle: string; numero: number; ciudad: string; provincia: string };
  sectorPublicId: string;
}

export interface EmpleadoAltaRequest extends EmpleadoUpdateRequest {
  rolSolicitado: string;
  credencial: { nombreUsuario: string; contrasenia: string };
}

export interface CambioEstadoRequest {
  estado: EstadoEmpleado;
  motivo: string;
}

export type AccionEmpleado = 'ALTA' | 'DATOS_MODIFICADOS' | 'ESTADO_CAMBIADO' | 'BAJA' | 'REACTIVADO';

export const ETIQUETA_ACCION_EMPLEADO: Record<AccionEmpleado, string> = {
  ALTA: 'Alta',
  DATOS_MODIFICADOS: 'Datos modificados',
  ESTADO_CAMBIADO: 'Cambio de estado',
  BAJA: 'Baja',
  REACTIVADO: 'Reactivación',
};

export interface EventoEmpleado {
  publicId: string;
  accion: AccionEmpleado;
  usuario: string;
  empleado: string | null;
  fecha: string;
  motivo: string | null;
  detalle: string | null;
}
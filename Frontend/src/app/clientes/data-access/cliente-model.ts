import { Modalidad } from '../../recursos/data-access/recurso-model';
import { ReservaEstado } from '../../reservas/data-access/reserva-model';

export interface Cliente {
  publicId: string;
  nombre: string;
  apellido: string;
  dni: number;
  email: string;
  telefono: string;
  fechaAlta: string;
  estado: boolean; // true = activo, false = dado de baja
}

export interface ReservaResumen {
  publicId: string;
  fechaInicio: string;
  fechaFin: string;
  modalidad: Modalidad;
  estadoReserva: ReservaEstado;
  montoTotal: number;
  recursos: string[];
}

export interface ClienteFicha extends Cliente {
  nombreUsuario: string | null;
  reservasActivas: ReservaResumen[];
  reservasAnteriores: number;
  ultimasAnteriores: ReservaResumen[];
}

export interface ClienteDatosRequest {
  nombre: string;
  apellido: string;
  dni: number;
  email: string;
  telefono: string;
}

export interface ClienteAltaRequest extends ClienteDatosRequest {
  credencial: { nombreUsuario: string; contrasenia: string };
}

export interface CambioEstadoClienteRequest {
  activo: boolean;
  motivo: string;
}

export type AccionCliente = 'ALTA' | 'DATOS_MODIFICADOS' | 'BAJA' | 'REACTIVADO';

export const ETIQUETA_ACCION_CLIENTE: Record<AccionCliente, string> = {
  ALTA: 'Alta',
  DATOS_MODIFICADOS: 'Datos modificados',
  BAJA: 'Baja',
  REACTIVADO: 'Reactivación',
};

export interface EventoCliente {
  publicId: string;
  accion: AccionCliente;
  usuario: string;
  empleado: string | null;
  fecha: string;
  motivo: string | null;
  detalle: string | null;
}

/** El listado devuelve a lo sumo esta cantidad de resultados (el servidor aplica el mismo tope). */
export const MAX_RESULTADOS_CLIENTES = 200;
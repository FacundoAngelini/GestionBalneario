import { required, schema, validate } from '@angular/forms/signals';
import { Modalidad, TipoRecurso } from '../../recursos/data-access/recurso-model';

//* Estados de reserva

export type ReservaEstado =
  | 'PENDIENTE'
  | 'CONFIRMADA'
  | 'CANCELADA'
  | 'FINALIZADA'
  | 'RECHAZADA'
  | 'VENCIDA';

export const ESTADOS_ACTIVOS: ReservaEstado[] = ['PENDIENTE', 'CONFIRMADA'];

export const ETIQUETA_ESTADO_RESERVA: Record<ReservaEstado, string> = {
  PENDIENTE: 'Pendiente de confirmación',
  CONFIRMADA: 'Confirmada',
  CANCELADA: 'Cancelada',
  FINALIZADA: 'Finalizada',
  RECHAZADA: 'Rechazada',
  VENCIDA: 'Vencida',
};

//* Clase de color de cada estado (definidas en styles.css)
export const CLASE_ESTADO: Record<ReservaEstado, string> = {
  PENDIENTE: 'estado--pendiente',
  CONFIRMADA: 'estado--confirmada',
  FINALIZADA: 'estado--finalizada',
  CANCELADA: 'estado--cerrada',
  RECHAZADA: 'estado--cerrada',
  VENCIDA: 'estado--cerrada',
};

//* Plazos del negocio (el backend aplica los mismos)
export const HORAS_PARA_CONFIRMAR = 48;
export const HORAS_PARA_CANCELAR = 48;

//* Textos

export const TEXTO_PAGO =
  'El cobro se realiza de manera presencial en administración. Podés abonar hasta el mismo día de inicio de la reserva.';

//* Métodos de pago

export type MetodoPago = 'EFECTIVO' | 'TARJETA' | 'TRANSFERENCIA' | 'MERCADO_PAGO';

export const ETIQUETA_METODO_PAGO: Record<MetodoPago, string> = {
  EFECTIVO: 'Efectivo',
  TARJETA: 'Tarjeta',
  TRANSFERENCIA: 'Transferencia',
  MERCADO_PAGO: 'Mercado Pago',
};

//* Acciones de reserva

export type AccionReserva =
  | 'CREADA'
  | 'CONFIRMADA'
  | 'RECHAZADA'
  | 'CANCELADA'
  | 'VENCIDA'
  | 'COBRO_REGISTRADO'
  | 'COBRO_ANULADO';

export const ETIQUETA_ACCION: Record<AccionReserva, string> = {
  CREADA: 'Solicitud creada',
  CONFIRMADA: 'Confirmada',
  RECHAZADA: 'Rechazada',
  CANCELADA: 'Cancelada',
  VENCIDA: 'Venció sin confirmarse',
  COBRO_REGISTRADO: 'Cobro registrado',
  COBRO_ANULADO: 'Cobro anulado',
};

//* Interfaces de solicitudes (Request)

export interface ReservaRequest {
  clientePublicId: string;
  modalidad: Modalidad;
  fechaInicio: string; //* 'YYYY-MM-DD'*
  fechaFin: string;
  recursosPublicIds: string[];
}

export interface CancelarReservaRequest {
  publicId: string;
  clientePublicId: string;
  fechaInicio: string;
  fechaFin: string;
  cancelarCocherasAsociadas: boolean;
}

//* Mirrors CancelarReservaDTO.java
export interface CancelarReserva {
  publicId?: string;
  clientePublicId: string;
  fechaInicio: string;
  fechaFin: string;
  cancelarCocherasAsociadas?: boolean;
}

export interface ReservaPresencialRequest extends ReservaRequest {
  cobrarAhora: boolean;
}

//* Query params of GET /reservas (all optional)

export interface ReservaFiltros {
  estado?: ReservaEstado;
  fechaDesde?: string;
  fechaHasta?: string;
  clientePublicId?: string;
}

//* Interfaces de respuestas (Response / DTO)

export interface Reserva {
  publicId: string;
  clientePublicId: string;
  modalidad: Modalidad;
  montoTotal: number | null;
  fechaInicio: string;
  fechaFin: string;
  recursosPublicIds: string[];
  estadoReserva: ReservaEstado;
  recursosDetalle: RecursoResumen[];
  clienteNombre: string;
  clienteDni: number;
  fechaCreacion?: string | null;
  cobro?: CobroResumen | null;
}

//* Lo que devuelve el backend (ReservaDTO.java). Todo viene siempre completo.

export interface RecursoResumen {
  publicId: string;
  nombre: string;
  tipo: TipoRecurso | null;
}

//* Mirrors CheckoutResponseDTO.java
//* para mp

export interface CheckoutResponse {
  reservaPublicId: string;
  pagoPublicId: string;
  urlMercadoPago: string;
  montoTotal: number;
  mensaje: string;
}

export interface ReservaPresencialResponse {
  reserva: Reserva;
  comprobante: Comprobante | null;
}

//* Interfaces de cobros y comprobantes

export interface CobroResumen {
  monto: number;
  descuento: number;
  metodoPago: MetodoPago;
  fecha: string | null;
  cobradoPor: string | null;
}

export interface Comprobante {
  ticketPublicId: string;
  reservaPublicId: string;
  fecha: string;
  clienteNombre: string;
  clienteDni: number;
  recursos: RecursoResumen[];
  fechaInicio: string;
  fechaFin: string;
  modalidad: Modalidad;
  subtotal: number;
  descuentoPorcentaje: number;
  descuentoMonto: number;
  total: number;
  metodoPago: MetodoPago;
  cobradoPor: string | null;
  empleado: string | null;
}

//* Interfaces de historial

export interface EventoReserva {
  publicId: string;
  accion: AccionReserva;
  usuario: string;
  empleado: string | null;
  fecha: string;
  motivo: string | null;
  detalle: string | null;
}

//* Cliente encontrado por DNI en el mostrador

export interface ClienteBusqueda {
  publicId: string;
  nombre: string;
  apellido: string;
  dni: number;
  activo: boolean;
}

//* Signal form model: plain data, dates stay as 'yyyy-MM-dd' strings
//* so they bind directly to <input type="date"> and match the backend LocalDate.

export interface ReservaFormModel {
  clientePublicId: string;
  modalidad: Modalidad;
  fechaInicio: string;
  fechaFin: string;
  recursosPublicIds: string[];
}

export const reservaFormInicial = (): ReservaFormModel => ({
  clientePublicId: '',
  modalidad: 'DIA',
  fechaInicio: '',
  fechaFin: '',
  recursosPublicIds: [],
});

export const reservaSchema = schema<ReservaFormModel>((path) => {
  required(path.clientePublicId, { message: 'El cliente es obligatorio' });
  required(path.fechaInicio, { message: 'La fecha de inicio es obligatoria' });
  required(path.fechaFin, { message: 'La fecha de fin es obligatoria' });

  validate(path.recursosPublicIds, ({ value }) =>
    value().length === 0
      ? { kind: 'required', message: 'Seleccioná al menos un recurso' }
      : undefined,
  );

  //* ISO yyyy-MM-dd strings compare correctly as text.
  validate(path.fechaFin, ({ value, valueOf }) => {
    const fin = value();
    const inicio = valueOf(path.fechaInicio);

    return fin && inicio && fin < inicio
      ? { kind: 'rangoFechas', message: 'La fecha de fin no puede ser anterior al inicio' }
      : undefined;
  });
});

export function formToReserva(value: ReservaFormModel): ReservaRequest {
  return { ...value };
}
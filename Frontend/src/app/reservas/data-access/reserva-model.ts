
import { required, schema, validate } from '@angular/forms/signals';
import { Modalidad } from '../../recursos/data-access/recurso-model';

export type ReservaEstado =
  | 'PENDIENTE'
  | 'CONFIRMADA'
  | 'CANCELADA'
  | 'FINALIZADA'
  | 'RECHAZADA'
  | 'VENCIDA';

export const ETIQUETA_ESTADO_RESERVA: Record<ReservaEstado, string> = {
  PENDIENTE: 'Pendiente de confirmación',
  CONFIRMADA: 'Confirmada',
  CANCELADA: 'Cancelada',
  FINALIZADA: 'Finalizada',
  RECHAZADA: 'Rechazada',
  VENCIDA: 'Vencida',
};

// Lo que se envía a POST /reservas/sin-pago y /reservas/checkout-online
export interface ReservaRequest {
  clientePublicId: string;
  modalidad: Modalidad;
  fechaInicio: string; // 'YYYY-MM-DD'
  fechaFin: string;
  recursosPublicIds: string[];
}

// Lo que devuelve el backend (ReservaDTO.java). Todo viene siempre completo.
export interface Reserva {
  publicId: string;
  clientePublicId: string;
  modalidad: Modalidad;
  montoTotal: number | null;
  fechaInicio: string;
  fechaFin: string;
  recursosPublicIds: string[];
  estadoReserva: ReservaEstado;
}

// Plazos del negocio (el backend aplica los mismos)
export const HORAS_PARA_CONFIRMAR = 48;
export const HORAS_PARA_CANCELAR = 48;

// Mirrors CheckoutResponseDTO.java
//para mp
export interface CheckoutResponse {
  reservaPublicId: string;
  pagoPublicId: string;
  urlMercadoPago: string;
  montoTotal: number;
  mensaje: string;
}

// Mirrors CancelarReservaDTO.java
export interface CancelarReserva {
  publicId?: string;
  clientePublicId: string;
  fechaInicio: string;
  fechaFin: string;
  cancelarCocherasAsociadas?: boolean;
}

// Query params of GET /reservas (all optional)

export interface ReservaFiltros {
  estado?: ReservaEstado;
  fechaDesde?: string;
  fechaHasta?: string;
  clientePublicId?: string;
}

// Signal form model: plain data, dates stay as 'yyyy-MM-dd' strings
// so they bind directly to <input type="date"> and match the backend LocalDate.
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
    value().length === 0 ? { kind: 'required', message: 'Seleccioná al menos un recurso' } : undefined,
  );
  // ISO yyyy-MM-dd strings compare correctly as text.
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
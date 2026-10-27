
import { required, schema, validate } from '@angular/forms/signals';

export type ReservaEstado = 'PENDIENTE' | 'CONFIRMADA' | 'CANCELADA' | 'FINALIZADA' | 'RECHAZADA';

// Mirrors ReservaDTO.java. UUID -> string, LocalDate -> 'yyyy-MM-dd' string.

export interface Reserva {
  publicId?: string;
  clientePublicId: string;
  fechaInicio: string;
  fechaFin: string;
  recursosPublicIds: string[];
  estadoReserva?: ReservaEstado;
}

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
  fechaInicio: string;
  fechaFin: string;
  recursosPublicIds: string[];
}

export const reservaFormInicial = (): ReservaFormModel => ({
  clientePublicId: '',
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

export function formToReserva(value: ReservaFormModel): Reserva {
  return { ...value };
}


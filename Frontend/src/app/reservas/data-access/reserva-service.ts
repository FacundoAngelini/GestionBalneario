import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { environment } from '../../core/environments/environment';
import {
  CancelarReservaRequest,
  ClienteBusqueda,
  Comprobante,
  EventoReserva,
  Reserva,
  ReservaPresencialRequest,
  ReservaPresencialResponse,
  ReservaRequest,
} from './reserva-model';

@Service()
export class ReservaService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/reservas`;

  // ---- Cliente ----
  reservarSinPago(pedido: ReservaRequest) {
    return this.http.post<Reserva>(`${this.apiUrl}/sin-pago`, pedido);
  }

  misReservas(clientePublicId: string) {
    const params = new HttpParams().set('clientePublicId', clientePublicId);
    return this.http.get<Reserva[]>(this.apiUrl, { params });
  }

  cancelar(pedido: CancelarReservaRequest) {
    return this.http.put<void>(`${this.apiUrl}/cancelar`, pedido);
  }

  // ---- Cliente y personal ----
  /** Cocheras que quedarían sin carpa ni sombrilla si se cancela esta reserva. */
  cocherasDependientes(reservaPublicId: string) {
    return this.http.get<Reserva[]>(`${this.apiUrl}/${reservaPublicId}/cocheras-dependientes`);
  }

  comprobante(publicId: string) {
    return this.http.get<Comprobante>(`${this.apiUrl}/${publicId}/cobro`);
  }

  // ---- Personal ----
  todas() {
    return this.http.get<Reserva[]>(this.apiUrl);
  }

  pendientes() {
    const params = new HttpParams().set('estado', 'PENDIENTE');
    return this.http.get<Reserva[]>(this.apiUrl, { params });
  }

  confirmar(publicId: string) {
    return this.http.put<Reserva>(`${this.apiUrl}/${publicId}/confirmar`, {});
  }

  rechazar(publicId: string) {
    return this.http.put<Reserva>(`${this.apiUrl}/${publicId}/rechazar`, {});
  }

  cancelarAdministrativa(publicId: string, cancelarCocheras: boolean) {
    const params = new HttpParams().set('cancelarCocherasAsociadas', cancelarCocheras);
    return this.http.put<void>(`${this.apiUrl}/${publicId}/cancelar-administrativo`, {}, { params });
  }

  registrarCobro(publicId: string) {
    return this.http.post<Comprobante>(`${this.apiUrl}/${publicId}/cobro`, { metodoPago: 'EFECTIVO' });
  }

  anularCobro(publicId: string, motivo: string) {
    return this.http.put<Reserva>(`${this.apiUrl}/${publicId}/cobro/anular`, { motivo });
  }

  historial(publicId: string) {
    return this.http.get<EventoReserva[]>(`${this.apiUrl}/${publicId}/historial`);
  }

  // ---- Mostrador ----
  crearPresencial(pedido: ReservaPresencialRequest) {
    return this.http.post<ReservaPresencialResponse>(`${this.apiUrl}/presencial`, pedido);
  }

  buscarClientePorDni(dni: number) {
    const params = new HttpParams().set('dni', dni);
    return this.http.get<ClienteBusqueda>(`${environment.apiUrl}/clientes/buscar`, { params });
  }

  configuracionPago() {
    return this.http.get<{ descuentoEfectivo: number }>(`${environment.apiUrl}/configuracion/pagos`);
  }
}
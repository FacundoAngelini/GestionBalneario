import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { environment } from '../../core/environments/environment';
import { CancelarReservaRequest, Reserva, ReservaRequest } from './reserva-model';

@Service()
export class ReservaService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/reservas`;

  reservarSinPago(pedido: ReservaRequest) {
    return this.http.post<Reserva>(`${this.apiUrl}/sin-pago`, pedido);
  }

  misReservas(clientePublicId: string) {
    const params = new HttpParams().set('clientePublicId', clientePublicId);
    return this.http.get<Reserva[]>(this.apiUrl, { params });
  }

  cocherasDependientes(reservaPublicId: string) {
    return this.http.get<Reserva[]>(`${this.apiUrl}/${reservaPublicId}/cocheras-dependientes`);
  }

  cancelar(pedido: CancelarReservaRequest) {
    return this.http.put<void>(`${this.apiUrl}/cancelar`, pedido);
  }
}
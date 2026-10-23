import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../core/environments/environment';
import {
  CancelarReserva,
  CheckoutResponse,
  Reserva,
  ReservaFiltros,
} from './reserva-model';

@Service()
export class ReservaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/reservas`;

  checkoutOnline(reserva: Reserva): Observable<CheckoutResponse> {
    return this.http.post<CheckoutResponse>(`${this.apiUrl}/checkout-online`, reserva);
  }

  obtenerPorId(id: string): Observable<Reserva> {
    return this.http.get<Reserva>(`${this.apiUrl}/${id}`);
  }

  listar(filtros: ReservaFiltros = {}): Observable<Reserva[]> {
    let params = new HttpParams();
    for (const [key, value] of Object.entries(filtros)) {
      if (value) params = params.set(key, value);
    }
    return this.http.get<Reserva[]>(this.apiUrl, { params });
  }

  cancelar(dto: CancelarReserva): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/cancelar`, dto);
  }

  cancelarAdministrativo(id: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/cancelar-administrativo`, {});
  }
}

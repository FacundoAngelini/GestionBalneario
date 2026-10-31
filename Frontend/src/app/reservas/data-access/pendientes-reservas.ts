import { effect, inject, Service, signal } from '@angular/core';
import { catchError, EMPTY, map, switchMap, timer } from 'rxjs';
import { AuthClient } from '../../auth/data-access/auth-client';
import { ReservaService } from './reserva-service';

const CADA_MS = 60_000;

/** Cantidad de solicitudes esperando confirmación. Solo se consulta si hay personal logueado. */
@Service()
export class PendientesReservas {
  private readonly auth = inject(AuthClient);
  private readonly reservaService = inject(ReservaService);

  readonly cantidad = signal(0);

  constructor() {
    effect((alLimpiar) => {
      if (!this.auth.esPersonal()) {
        this.cantidad.set(0);
        return;
      }
      // Consulta ahora y después cada minuto, mientras haya personal logueado
      const suscripcion = timer(0, CADA_MS)
        .pipe(
          switchMap(() =>
            this.reservaService.pendientes().pipe(
              map((lista) => lista.length),
              catchError(() => EMPTY),
            ),
          ),
        )
        .subscribe((n) => this.cantidad.set(n));
      alLimpiar(() => suscripcion.unsubscribe());
    });
  }

  /** Para cuando una pantalla ya sabe el número real (evita esperar al próximo minuto). */
  fijar(n: number): void {
    this.cantidad.set(n);
  }
}
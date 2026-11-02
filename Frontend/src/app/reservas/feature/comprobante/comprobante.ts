import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { formatoFechaHora, formatoRango } from '../../../core/utils/fechas';
import { formatoPrecio } from '../../../core/utils/moneda';
import { ETIQUETA_MODALIDAD, ETIQUETA_TIPO_RECURSO } from '../../../recursos/data-access/recurso-model';
import { Comprobante, ETIQUETA_METODO_PAGO } from '../../data-access/reserva-model';
import { ReservaService } from '../../data-access/reserva-service';

@Component({
  selector: 'app-comprobante-reserva',
  imports: [RouterLink],
  templateUrl: './comprobante.html',
  styleUrl: './comprobante.css',
})
export class ComprobanteReserva {
  private readonly reservaService = inject(ReservaService);
  private readonly auth = inject(AuthClient);
  private readonly reservaId = inject(ActivatedRoute).snapshot.paramMap.get('id') ?? '';

  protected readonly etiquetaModalidad = ETIQUETA_MODALIDAD;
  protected readonly etiquetaTipo = ETIQUETA_TIPO_RECURSO;
  protected readonly etiquetaMetodo = ETIQUETA_METODO_PAGO;
  protected readonly formatoPrecio = formatoPrecio;
  protected readonly formatoRango = formatoRango;
  protected readonly formatoFechaHora = formatoFechaHora;

  protected readonly comprobante = signal<Comprobante | null>(null);
  protected readonly cargando = signal(true);
  protected readonly error = signal('');

  protected readonly volverA = computed(() =>
    this.auth.esPersonal() ? '/reservas/gestion' : '/reservas/mis-reservas',
  );
  protected readonly numero = computed(
    () => this.comprobante()?.ticketPublicId.slice(0, 8).toUpperCase() ?? '',
  );

  constructor() {
    this.reservaService.comprobante(this.reservaId).subscribe({
      next: (c) => {
        this.comprobante.set(c);
        this.cargando.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.cargando.set(false);
        this.error.set(
          e.status === 404
            ? 'Esta reserva no tiene un cobro vigente.'
            : e.status === 403
              ? 'No tenés permiso para ver este comprobante.'
              : (e.error?.message ?? 'No se pudo cargar el comprobante.'),
        );
      },
    });
  }

  protected imprimir(): void {
    window.print();
  }
}
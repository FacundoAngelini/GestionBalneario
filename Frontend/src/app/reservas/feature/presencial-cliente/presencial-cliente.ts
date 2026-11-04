import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { form, FormField } from '@angular/forms/signals';
import { Router, RouterLink } from '@angular/router';
import { ROUTE_SEGMENTS } from '../../../core/constants/route-segments.constants';
import { ClienteBusqueda } from '../../data-access/reserva-model';
import { ReservaService } from '../../data-access/reserva-service';
import { SeleccionReserva } from '../../data-access/seleccion-reserva';

const FORMATO_DNI = /^\d{7,8}$/;

@Component({
  selector: 'app-presencial-cliente',
  imports: [FormField, RouterLink],
  templateUrl: './presencial-cliente.html',
  styleUrl: './presencial-cliente.css',
})
export class PresencialCliente {
  private readonly reservaService = inject(ReservaService);
  private readonly router = inject(Router);
  private readonly seleccion = inject(SeleccionReserva);

  protected readonly modelo = signal({ dni: '' });
  protected readonly f = form(this.modelo);

  protected readonly noEncontrado = signal(false);

  protected readonly buscando = signal(false);
  protected readonly encontrado = signal<ClienteBusqueda | null>(this.seleccion.clienteMostrador());
  protected readonly mensaje = signal('');

  protected readonly errorDni = computed(() => {
    const dni = this.modelo().dni.trim();
    return dni && !FORMATO_DNI.test(dni) ? 'El DNI tiene 7 u 8 números, sin puntos' : null;
  });
  protected readonly puedeBuscar = computed(
    () => FORMATO_DNI.test(this.modelo().dni.trim()) && !this.buscando(),
  );

  protected buscar(evento: Event): void {
    evento.preventDefault();
    if (!this.puedeBuscar()) return;

    this.buscando.set(true);
    this.mensaje.set('');
    this.encontrado.set(null);
    this.noEncontrado.set(false);

    this.reservaService.buscarClientePorDni(Number(this.modelo().dni.trim())).subscribe({
      next: (cliente) => {
        this.buscando.set(false);
        this.encontrado.set(cliente);
      },
        error: (e: HttpErrorResponse) => {
        this.buscando.set(false);
        this.noEncontrado.set(e.status === 404);
        this.mensaje.set(
          e.status === 404
            ? 'No hay un cliente registrado con ese DNI. Podés crearlo ahora, o pedirle que se registre desde su celular y volver a buscarlo.'
            : e.status === 0
              ? 'No se pudo conectar con el servidor'
              : (e.error?.message ?? 'No se pudo buscar al cliente. Intentá de nuevo.'),
        );
      },
    });
  }

  protected elegirRecursos(cliente: ClienteBusqueda): void {
    // Si cambia de cliente, lo que se había elegido para el anterior no se arrastra
    if (this.seleccion.clienteMostrador()?.publicId !== cliente.publicId) {
      this.seleccion.limpiar();
    }
    this.seleccion.clienteMostrador.set(cliente);
    void this.router.navigateByUrl(`/${ROUTE_SEGMENTS.catalogo}`);
  }
}

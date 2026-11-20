
import { NgOptimizedImage } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthClient } from '../../../auth/data-access/auth-client';

@Component({
  selector: 'app-inicio',
  imports: [NgOptimizedImage, RouterLink],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css',
})
export class Inicio {
  private readonly auth = inject(AuthClient);

  protected readonly mostrarReserva = computed(() => {
    // Los visitantes siguen viendo el acceso a reservar.
    if (!this.auth.usuarioLogueado()) return true;

    // Los clientes y el personal autorizado pueden reservar.
    return this.auth.esCliente() || this.auth.esPersonal();
  });

  protected readonly rutaReserva = computed(() => {
    return this.auth.esPersonal()
      ? '/reservas/presencial'
      : '/reservas/mis-reservas';
  });

  protected readonly accesos = [
    {
      titulo: 'Reservá tu lugar',
      texto: 'Carpas, sombrillas y mesas. Elegí fecha y listo.',
      imagen: '/images/carpas.webp',
      alt: 'Fila de carpas frente al mar',
      ruta: '/reservas/mis-reservas',
      cta: 'Reservar',
    },
    {
      titulo: 'Menú del restaurante',
      texto: 'Mirá la carta antes de llegar.',
      imagen: '/images/restaurant.webp',
      alt: 'Mesa servida frente a la playa',
      ruta: '/productos',
      cta: 'Ver menú',
    },
  ];
}

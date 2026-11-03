import { NgOptimizedImage } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-inicio',
  imports: [NgOptimizedImage, RouterLink],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css',
})
export class Inicio {
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
      ruta: '/menu',
      cta: 'Ver menú',
    },
  ];
}
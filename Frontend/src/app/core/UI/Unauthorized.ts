import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div style="text-align: center; padding: 50px; font-family: sans-serif;">
      <h1 style="color: #d32f2f;">403 - Acceso Denegado</h1>
      <p>No tienes los permisos (roles) necesarios para ver esta página.</p>
      <a routerLink="/" style="color: #1E3A8A; text-decoration: none; font-weight: bold;">Volver al inicio</a>
    </div>
  `
})
export class Unauthorized {}
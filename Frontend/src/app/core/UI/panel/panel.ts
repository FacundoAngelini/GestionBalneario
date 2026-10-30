import { Component, computed, inject } from '@angular/core';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { RouterLink } from '@angular/router';
import { PendientesReservas } from '../../../reservas/data-access/pendientes-reservas';

@Component({
  imports: [RouterLink],
  selector: 'app-panel',
  styleUrl: './panel.css',
  templateUrl: './panel.html',
})
export class Panel {
  private readonly auth = inject(AuthClient);

  protected readonly nombre = this.auth.nombreUsuario;
  protected readonly rol = this.auth.etiquetaRol;
  protected readonly esCliente = this.auth.esCliente;

  protected readonly esPersonal = this.auth.esPersonal;
  protected readonly pendientes = inject(PendientesReservas).cantidad;

  // Cajero, mozo, repartidor...: por ahora solo pueden iniciar sesión
  protected readonly sinFunciones = computed(
    () => !this.auth.esCliente() && !this.auth.esPersonal(),
  );
}

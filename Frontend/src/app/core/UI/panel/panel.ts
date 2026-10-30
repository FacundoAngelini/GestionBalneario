import { Component, computed, inject } from '@angular/core';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { RouterLink, RouterLinkActive } from '@angular/router';

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

  // Cajero, mozo, repartidor...: por ahora solo pueden iniciar sesión
  protected readonly sinFunciones = computed(
    () => !this.auth.esCliente() && !this.auth.esPersonal(),
  );
}

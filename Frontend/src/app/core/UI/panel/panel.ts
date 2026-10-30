import { Component, computed, inject } from '@angular/core';
import { AuthClient } from '../../../auth/data-access/auth-client';

@Component({
  imports: [],
  selector: 'app-panel',
  styleUrl: './panel.css',
  templateUrl: './panel.html',
})
export class Panel {
  private readonly auth = inject(AuthClient);

  protected readonly nombre = this.auth.nombreUsuario;
  protected readonly rol = this.auth.etiquetaRol;

  // Cajero, mozo, repartidor...: por ahora solo pueden iniciar sesión
  protected readonly sinFunciones = computed(
    () => !this.auth.esCliente() && !this.auth.esPersonal(),
  );
}

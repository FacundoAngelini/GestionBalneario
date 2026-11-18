import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { EstadoEmpleado, ETIQUETA_ESTADO_EMPLEADO } from '../../../empleados/data-access/empleado-model';
import { PendientesReservas } from '../../../reservas/data-access/pendientes-reservas';
import { rxResource } from '@angular/core/rxjs-interop';
import { UsuarioClient } from '../../../usuarios/data-access/usuarios.client';

@Component({
  imports: [RouterLink],
  selector: 'app-panel',
  styleUrl: './panel.css',
  templateUrl: './panel.html',
})
export class Panel {
  private readonly auth = inject(AuthClient);

  protected readonly nombre = computed(() => {
  const perfil = this.perfil.value();
  const ficha = perfil?.cliente ?? perfil?.empleado;

  const nombre = ficha?.nombre.trim();
  const apellido = ficha?.apellido.trim();

  if (nombre && apellido) {
    return `${nombre} ${apellido}`;
  }

  return nombre || perfil?.nombreUsuario || this.auth.nombreUsuario();
});

  protected readonly rol = this.auth.etiquetaRol;
  protected readonly esCliente = this.auth.esCliente;
  protected readonly esPersonal = this.auth.esPersonal;
  protected readonly esCarpero = this.auth.esCarpero;
  protected readonly restringido = this.auth.restringido;
  protected readonly gestionaEmpleados = this.auth.puedeGestionarEmpleados;
  protected readonly pendientes = inject(PendientesReservas).cantidad;

private readonly usuarioClient = inject(UsuarioClient);

protected readonly perfil = rxResource({
  stream: () => this.usuarioClient.obtenerMiPerfil(),
});

  protected readonly estadoTexto = computed(() => {
    const estado = this.auth.estadoEmpleado() as EstadoEmpleado | null;
    return estado ? ETIQUETA_ESTADO_EMPLEADO[estado] : '';
  });

  protected readonly sinFunciones = computed(
    () => !this.esCliente() && !this.esPersonal() && !this.esCarpero() && !this.restringido(),
  );
}
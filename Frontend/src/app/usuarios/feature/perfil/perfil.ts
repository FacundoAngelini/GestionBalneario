import { DatePipe, Location } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { ETIQUETA_ROL } from '../../../auth/data-access/auth-model';
import { ROUTE_SEGMENTS } from '../../../core/constants/route-segments.constants';
import { UsuarioClient } from '../../data-access/usuarios.client';
import { ETIQUETA_TIPO_PERFIL, PerfilCliente } from '../../data-access/usuario-model';
import { CambiarContrasenia } from './cambiar-contraseña';
import { CambiarUsuario } from './cambiar-usuario';
import { PerfilEditar } from './perfil-editar';

type ModoPerfil = 'ver' | 'editar' | 'contrasenia' | 'usuario';

@Component({
  selector: 'app-perfil',
  imports: [DatePipe, MatButtonModule, MatIconModule, PerfilEditar, CambiarContrasenia, CambiarUsuario],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil {
  private readonly usuarioClient = inject(UsuarioClient);
  private readonly router = inject(Router);
  private readonly ubicacion = inject(Location);

  protected readonly perfil = rxResource({
    stream: () => this.usuarioClient.obtenerMiPerfil(),
  });

  protected readonly modo = signal<ModoPerfil>('ver');

  protected readonly cliente = computed(() => this.perfil.value()?.cliente ?? null);
  protected readonly empleado = computed(() => this.perfil.value()?.empleado ?? null);
  protected readonly ficha = computed(() => this.cliente() ?? this.empleado());

  protected readonly nombreCompleto = computed(() => {
    const ficha = this.ficha();
    return ficha ? `${ficha.nombre} ${ficha.apellido}`.trim() : (this.perfil.value()?.nombreUsuario ?? '');
  });

  protected readonly inicial = computed(() =>
    (this.ficha()?.nombre || this.perfil.value()?.nombreUsuario || 'U').charAt(0).toUpperCase(),
  );

  protected readonly etiquetaTipo = computed(() => {
    const tipo = this.perfil.value()?.tipoPerfil;
    return tipo ? ETIQUETA_TIPO_PERFIL[tipo] : '';
  });

  protected readonly etiquetaRoles = computed(() =>
    (this.perfil.value()?.roles ?? []).map((rol) => ETIQUETA_ROL[rol] ?? rol.replace('ROLE_', '')).join(' · '),
  );

  protected readonly mensajeSinFicha = computed(() =>
    this.perfil.value()?.tipoPerfil === 'ADMINISTRADOR'
      ? 'Esta es una cuenta administrativa y no tiene datos personales asociados.'
      : 'Tu cuenta todavía no tiene una ficha de cliente o empleado asociada.',
  );

  /** Vuelve a la pantalla anterior; si entró directo por URL, va al panel. */
  protected volver(): void {
    if ((history.state?.navigationId ?? 0) > 1) {
      this.ubicacion.back();
    } else {
      void this.router.navigateByUrl(`/${ROUTE_SEGMENTS.panel}`);
    }
  }

  protected alGuardar(clienteActualizado: PerfilCliente): void {
    this.perfil.value.update((actual) => (actual ? { ...actual, cliente: clienteActualizado } : actual));
    this.modo.set('ver');
  }
}
import { DatePipe } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';

import { ETIQUETA_ROL } from '../../../auth/data-access/auth-model';
import { UsuarioClient } from '../../data-access/usuarios.client';
import { ETIQUETA_TIPO_PERFIL } from '../../data-access/usuario-model';

const ETIQUETAS_ROL: Record<string, string> = ETIQUETA_ROL;

@Component({
  selector: 'app-perfil',
  imports: [DatePipe, MatButtonModule],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil {
  private readonly usuarioClient = inject(UsuarioClient);

  protected readonly perfil = rxResource({
    stream: () => this.usuarioClient.obtenerMiPerfil(),
  });

  protected readonly cliente = computed(
    () => this.perfil.value()?.cliente ?? null,
  );
  protected readonly empleado = computed(
    () => this.perfil.value()?.empleado ?? null,
  );
  protected readonly ficha = computed(() => this.cliente() ?? this.empleado());

  protected readonly nombreCompleto = computed(() => {
    const ficha = this.ficha();
    return ficha
      ? `${ficha.nombre} ${ficha.apellido}`.trim()
      : (this.perfil.value()?.nombreUsuario ?? '');
  });

  protected readonly inicial = computed(() =>
    (this.ficha()?.nombre || this.perfil.value()?.nombreUsuario || 'U')
      .charAt(0)
      .toUpperCase(),
  );

  protected readonly etiquetaTipo = computed(() => {
    const tipo = this.perfil.value()?.tipoPerfil;
    return tipo ? ETIQUETA_TIPO_PERFIL[tipo] : '';
  });

  protected readonly etiquetaRoles = computed(() =>
    (this.perfil.value()?.roles ?? [])
      .map((rol) => ETIQUETAS_ROL[rol] ?? rol.replace('ROLE_', ''))
      .join(' · '),
  );

  protected readonly mensajeSinFicha = computed(() =>
  this.perfil.value()?.tipoPerfil === 'ADMINISTRADOR'
    ? 'Esta es una cuenta administrativa y no tiene datos personales asociados.'
    : 'Tu cuenta todavía no tiene una ficha de cliente o empleado asociada.',
);
}
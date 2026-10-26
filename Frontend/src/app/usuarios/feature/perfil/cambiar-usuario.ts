import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, input, OnInit, output, signal } from '@angular/core';
import {
  disabled,
  FieldTree,
  form,
  FormField,
  FormRoot,
  TreeValidationResult,
  validate,
} from '@angular/forms/signals';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { ROUTE_SEGMENTS } from '../../../core/constants/route-segments.constants';
import { noBlanco } from '../../../core/validators/validadores';
import { Dialogos } from '../../../shared/mensaje-dialog/dialogos';
import { UsuarioClient } from '../../data-access/usuarios.client';

interface CambiarUsuarioForm {
  nuevoNombreUsuario: string;
}

@Component({
  selector: 'app-cambiar-usuario',
  imports: [FormRoot, FormField, MatInputModule, MatButtonModule, MatProgressSpinnerModule],
  templateUrl: './cambiar-usuario.html',
  styleUrl: './perfil-form.css',
})
export class CambiarUsuario implements OnInit {
  private readonly usuarioClient = inject(UsuarioClient);
  private readonly auth = inject(AuthClient);
  private readonly router = inject(Router);
  private readonly dialogos = inject(Dialogos);

  readonly clientePublicId = input.required<string>();
  readonly cancelado = output<void>();

  protected readonly usuarioActual = this.auth.nombreUsuario;

  private readonly modelo = signal<CambiarUsuarioForm>({ nuevoNombreUsuario: '' });

  protected readonly formulario = form(
    this.modelo,
    (campos) => {
      noBlanco(campos.nuevoNombreUsuario, 'El nombre de usuario es obligatorio');
      validate(campos.nuevoNombreUsuario, ({ value }) =>
        value().trim() !== '' && value().trim() === this.usuarioActual()
          ? { kind: 'igual', message: 'Ese ya es tu nombre de usuario actual' }
          : undefined,
      );

      disabled(campos, { when: ({ state }) => state.submitting() });
    },
    {
      submission: {
        action: async (campos) => {
          const confirmado = await this.dialogos.confirmar(
            'Cambiar nombre de usuario',
            'Tu nuevo nombre de usuario será el que uses para iniciar sesión. Al confirmar, vamos a cerrar tu sesión y vas a tener que ingresar de nuevo.',
            'Cambiar usuario',
          );
          if (!confirmado) return undefined;

          try {
            await firstValueFrom(
              this.usuarioClient.cambiarNombreUsuario(this.clientePublicId(), {
                nuevoNombreUsuario: campos().value().nuevoNombreUsuario.trim(),
              }),
            );
          } catch (error) {
            return await this.manejarError(error, campos);
          }

          await this.dialogos.avisar(
            'exito',
            'Nombre de usuario actualizado',
            'El cambio se realizó correctamente. Por seguridad cerramos tu sesión: ingresá de nuevo con tu nuevo nombre de usuario.',
          );
          // el token guarda el usuario anterior, así que ya no sirve: se cierra la sesión
          this.auth.logout();
          await this.router.navigateByUrl(`/${ROUTE_SEGMENTS.auth}/${ROUTE_SEGMENTS.login}`);
          return undefined;
        },
      },
    },
  );

  ngOnInit(): void {
    this.modelo.set({ nuevoNombreUsuario: this.usuarioActual() });
  }

  private async manejarError(error: unknown, campos: FieldTree<CambiarUsuarioForm>): Promise<TreeValidationResult> {
    const mensaje = error instanceof HttpErrorResponse ? String(error.error?.message ?? '') : '';
    const estado = error instanceof HttpErrorResponse ? error.status : -1;

    if ((estado === 403 || estado === 409) && mensaje.includes('nombre de usuario')) {
      return [{ fieldTree: campos.nuevoNombreUsuario, kind: 'servidor', message: 'Ese nombre de usuario ya está en uso' }];
    }

    await this.dialogos.avisar(
      'error',
      'No se pudo cambiar el nombre de usuario',
      estado === 0
        ? 'No se pudo conectar con el servidor. Revisá tu conexión e intentá de nuevo.'
        : 'Ocurrió un error inesperado. Intentá de nuevo en unos minutos.',
    );
    return undefined;
  }
}
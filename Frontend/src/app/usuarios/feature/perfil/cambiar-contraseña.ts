import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, input, output, signal } from '@angular/core';
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
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { firstValueFrom } from 'rxjs';
import { contraseniaSegura, noBlanco } from '../../../core/validators/validadores';
import { Dialogos } from '../../../shared/mensaje-dialog/dialogos';
import { UsuarioClient } from '../../data-access/usuarios.client';

interface CambiarContraseniaForm {
  contraseniaActual: string;
  nuevaContrasenia: string;
  confirmacion: string;
}

@Component({
  selector: 'app-cambiar-contrasenia',
  imports: [FormRoot, FormField, MatInputModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule],
  templateUrl: './cambiar-contrasenia.html',
  styleUrl: './perfil-form.css',
})
export class CambiarContrasenia {
  private readonly usuarioClient = inject(UsuarioClient);
  private readonly dialogos = inject(Dialogos);

  readonly clientePublicId = input.required<string>();
  readonly cambiada = output<void>();
  readonly cancelada = output<void>();

  protected readonly verContrasenias = signal(false);

  private readonly modelo = signal<CambiarContraseniaForm>({
    contraseniaActual: '',
    nuevaContrasenia: '',
    confirmacion: '',
  });

  protected readonly formulario = form(
    this.modelo,
    (campos) => {
      noBlanco(campos.contraseniaActual, 'Ingresá tu contraseña actual');

      contraseniaSegura(campos.nuevaContrasenia);
      validate(campos.nuevaContrasenia, ({ value, valueOf }) =>
        value() !== '' && value() === valueOf(campos.contraseniaActual)
          ? { kind: 'igual', message: 'La nueva contraseña debe ser distinta de la actual' }
          : undefined,
      );

      noBlanco(campos.confirmacion, 'Repetí la nueva contraseña');
      validate(campos.confirmacion, ({ value, valueOf }) =>
        value() !== '' && value() !== valueOf(campos.nuevaContrasenia)
          ? { kind: 'noCoincide', message: 'Las contraseñas no coinciden' }
          : undefined,
      );

      disabled(campos, { when: ({ state }) => state.submitting() });
    },
    {
      submission: {
        action: async (campos) => {
          const confirmado = await this.dialogos.confirmar(
            'Cambiar contraseña',
            '¿Querés cambiar tu contraseña? Vamos a verificar tu contraseña actual antes de guardar la nueva.',
            'Cambiar contraseña',
          );
          if (!confirmado) return undefined;

          try {
            const { contraseniaActual, nuevaContrasenia } = campos().value();
            await firstValueFrom(
              this.usuarioClient.cambiarContrasenia(this.clientePublicId(), { contraseniaActual, nuevaContrasenia }),
            );
            await this.dialogos.avisar('exito', 'Contraseña actualizada', 'Tu contraseña se cambió correctamente.');
            this.cambiada.emit();
            return undefined;
          } catch (error) {
            return await this.manejarError(error, campos);
          }
        },
      },
    },
  );

  private async manejarError(error: unknown, campos: FieldTree<CambiarContraseniaForm>): Promise<TreeValidationResult> {
    const mensaje = error instanceof HttpErrorResponse ? String(error.error?.message ?? '') : '';
    const estado = error instanceof HttpErrorResponse ? error.status : -1;

    if (estado === 400 && mensaje.includes('es incorrecta')) {
      return [{ fieldTree: campos.contraseniaActual, kind: 'servidor', message: 'La contraseña actual es incorrecta' }];
    }

    await this.dialogos.avisar(
      'error',
      'No se pudo cambiar la contraseña',
      estado === 0
        ? 'No se pudo conectar con el servidor. Revisá tu conexión e intentá de nuevo.'
        : estado === 400
          ? 'Revisá los datos ingresados e intentá de nuevo.'
          : estado === 403
            ? 'No tenés permiso para cambiar esta contraseña.'
            : 'Ocurrió un error inesperado. Intentá de nuevo en unos minutos.',
    );
    return undefined;
  }
}
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, output, signal } from '@angular/core';
import { disabled, form, FormField, FormRoot, maxLength, minLength } from '@angular/forms/signals';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { mensajeAmigable } from '../../../core/utils/errores';
import { noBlanco } from '../../../core/validators/validadores';
import { Dialogos } from '../../../shared/mensaje-dialog/dialogos';
import { UsuarioClient } from '../../data-access/usuarios.client';

@Component({
  selector: 'app-solicitar-baja',
  imports: [FormRoot, FormField, MatInputModule, MatButtonModule, MatProgressSpinnerModule],
  templateUrl: './solicitar-baja.html',
  styleUrl: './perfil-form.css',
})
export class SolicitarBaja {
  private readonly usuarioClient = inject(UsuarioClient);
  private readonly auth = inject(AuthClient);
  private readonly router = inject(Router);
  private readonly dialogos = inject(Dialogos);

  readonly cancelado = output<void>();

  private readonly modelo = signal({ motivo: '' });

  protected readonly formulario = form(
    this.modelo,
    (campos) => {
      noBlanco(campos.motivo, 'Contanos el motivo');
      minLength(campos.motivo, 5, { message: 'Escribí al menos 5 caracteres' });
      maxLength(campos.motivo, 300, { message: 'Máximo 300 caracteres' });
      disabled(campos, { when: ({ state }) => state.submitting() });
    },
    {
      submission: {
        action: async (campos) => {
          const confirmado = await this.dialogos.confirmar(
            'Dar de baja mi cuenta',
            'Tu cuenta se va a desactivar y no vas a poder iniciar sesión. Para volver a usarla, Administración tiene que reactivarla. ¿Querés continuar?',
            'Dar de baja mi cuenta',
          );
          if (!confirmado) return undefined;

          try {
            await firstValueFrom(this.usuarioClient.darmeDeBaja({ motivo: campos().value().motivo.trim() }));
          } catch (error) {
            if (error instanceof HttpErrorResponse && error.status === 422) {
              const eleccion = await this.dialogos.elegir(
                'advertencia',
                'No podés darte de baja todavía',
                'Tenés reservas pendientes o confirmadas.\n\nCancelalas desde "Mis reservas" y después volvé a pedir la baja.',
                'Ir a Mis reservas',
                'Cerrar',
              );
              if (eleccion === 'principal') await this.router.navigateByUrl('/reservas/mis-reservas');
              return undefined;
            }
            await this.dialogos.avisar(
              'error',
              'No se pudo dar de baja tu cuenta',
              error instanceof HttpErrorResponse
                ? mensajeAmigable(error, 'Ocurrió un error inesperado. Intentá de nuevo en unos minutos.')
                : 'Ocurrió un error inesperado. Intentá de nuevo en unos minutos.',
            );
            return undefined;
          }

          await this.dialogos.avisar('exito', 'Cuenta dada de baja', 'Tu cuenta se desactivó. Cerramos tu sesión.');
          this.auth.logout();
          await this.router.navigateByUrl('/');
          return undefined;
        },
      },
    },
  );
}
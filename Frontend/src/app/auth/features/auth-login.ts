import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthClient } from '../data-access/auth-client';
import { form, FormField, FormRoot, disabled } from '@angular/forms/signals';
import { LoginRequest } from '../data-access/auth-model';
import { noBlanco } from '../../core/validators/validadores';
import { firstValueFrom } from 'rxjs';
import { ROUTE_SEGMENTS } from '../../core/constants/route-segments.constants';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-login',
  imports: [FormRoot,
    FormField,
    RouterLink,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,],
  templateUrl: './auth-login.html',
  styleUrl: './auth-form.css',
})
export class Login {
  private readonly auth = inject(AuthClient);
  private readonly router = inject(Router);

  protected readonly errorMsg = signal('');
  protected readonly verContrasenia = signal(false);

   private readonly loginModel = signal<LoginRequest>({ nombreUsuario: '', contrasenia: '' });

  protected readonly loginForm = form(
    this.loginModel,
    (campos) => {
      noBlanco(campos.nombreUsuario, 'El usuario es obligatorio');
      noBlanco(campos.contrasenia, 'La contraseña es obligatoria');
      disabled(campos, { when: ({ state }) => state.submitting() });
    },
    {
      submission: {
        action: async (campo) => {
          this.errorMsg.set('');
          try {
            await firstValueFrom(this.auth.login(campo().value()));
            await this.router.navigateByUrl(`/${ROUTE_SEGMENTS.panel}`);
          } catch (error) {
            this.errorMsg.set(this.mensajeDeError(error));
          }
        },
      },
    },
  );

  private mensajeDeError(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      // 401: el backend manda un mensaje claro ("Usuario o contraseña incorrecta" / cuenta deshabilitada).
      if (error.status === 401) return error.error?.message ?? 'Usuario o contraseña incorrecta';
      if (error.status === 0) return 'No se pudo conectar con el servidor';
    }
    return 'Ocurrió un error inesperado. Intentá de nuevo';
  }
}
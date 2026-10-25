import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import {
  disabled,
  FieldTree,
  form,
  FormField,
  FormRoot,
  max,
  maxLength,
  min,
  minLength,
  pattern,
  required,
  TreeValidationResult,
  validate,
} from '@angular/forms/signals';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ROUTE_SEGMENTS } from '../../core/constants/route-segments.constants';
import { noBlanco } from '../../core/validators/validadores';
import { AuthClient } from '../data-access/auth-client';
import { MatSelectModule } from '@angular/material/select';
import { buscarPais, PAISES } from '../../core/constants/paises.constants';

// El DNI empieza en null (campo vacío). En RegistroClienteRequest es number.
interface RegistroForm {
  nombre: string;
  apellido: string;
  dni: number | null;
  email: string;
  pais: string;
  telefono: string;
  nombreUsuario: string;
  contrasenia: string;
}

@Component({
  selector: 'app-registro',
  imports: [
    FormRoot,
    FormField,
    RouterLink,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSelectModule,
  ],
  templateUrl: './auth-registro.html',
  styleUrl: './auth-form.css',
})
export class Registro {
  private readonly auth = inject(AuthClient);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly errorMsg = signal('');
  protected readonly verContrasenia = signal(false);
  protected readonly paises = PAISES;

  private readonly registroModel = signal<RegistroForm>({
    nombre: '',
    apellido: '',
    dni: null,
    email: '',
    pais: '54',
    telefono: '',
    nombreUsuario: '',
    contrasenia: '',
  });

  protected readonly paisElegido = computed(() => buscarPais(this.registroModel().pais) ?? PAISES[0]);

  protected readonly registroForm = form(
    this.registroModel,
    (campos) => {
      // las reglas 
      noBlanco(campos.nombre, 'El nombre es obligatorio');
      noBlanco(campos.apellido, 'El apellido es obligatorio');

      required(campos.dni, { message: 'El DNI es obligatorio' });
      min(campos.dni, 1000000, { message: 'El DNI debe ser un número valido' });
      max(campos.dni, 99999999, { message: 'El DNI es demasiado grande' });

      noBlanco(campos.email, 'El email es obligatorio');
      pattern(campos.email, /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$/, {
        message: 'Formato inválido: en minúsculas y con dominio de 2 o 3 letras (ej: nombre@correo.com)',
      });

            noBlanco(campos.telefono, 'El teléfono es obligatorio');
      pattern(campos.telefono, /^[0-9]*$/, { message: 'Ingresá solo números, sin espacios, guiones ni el signo +' });
      validate(campos.telefono, ({ value, valueOf }) => {
        const numero = value();
        const pais = PAISES.find((p) => p.prefijo === valueOf(campos.pais));
        if (!pais || numero === '' || !/^[0-9]+$/.test(numero)) return undefined; // otras reglas ya lo cubren
        if (numero.length >= pais.min && numero.length <= pais.max) return undefined;
        const digitos = pais.min === pais.max ? `${pais.min} dígitos` : `entre ${pais.min} y ${pais.max} dígitos`;
        return { kind: 'largo', message: `En ${pais.nombre} el teléfono tiene ${digitos} (${pais.ayuda}). Ej: ${pais.ejemplo}` };
      });

      noBlanco(campos.nombreUsuario, 'El nombre de usuario es obligatorio');

      noBlanco(campos.contrasenia, 'La contraseña es obligatoria');
      minLength(campos.contrasenia, 8, { message: 'Debe tener al menos 8 caracteres' });
      maxLength(campos.contrasenia, 20, { message: 'Debe tener como máximo 20 caracteres' });
      pattern(campos.contrasenia, /[A-Z]/, { message: 'Debe incluir al menos una mayúscula' });
      pattern(campos.contrasenia, /[a-z]/, { message: 'Debe incluir al menos una minúscula' });
      pattern(campos.contrasenia, /\d/, { message: 'Debe incluir al menos un número' });
      pattern(campos.contrasenia, /[@$!%*?&]/, { message: 'Debe incluir un símbolo: @ $ ! % * ? &' });
      pattern(campos.contrasenia, /^[A-Za-z\d@$!%*?&]*$/, {
        message: 'Solo se permiten letras, números y los símbolos @ $ ! % * ? &',
      });

      // Mientras se envía, se bloquean todos los campos
      disabled(campos, { when: ({ state }) => state.submitting() });
    },
    {
      submission: {
        action: async (campos) => {
          this.errorMsg.set('');
          try {
            const { pais, telefono, ...resto } = campos().value();
            await firstValueFrom(
              this.auth.registro({ ...resto, telefono: pais + telefono, dni: Number(resto.dni) }),);
            this.snackBar.open('Cuenta creada. Ya podés iniciar sesión.', 'Cerrar', { duration: 6000 });
            await this.router.navigateByUrl(`/${ROUTE_SEGMENTS.auth}/${ROUTE_SEGMENTS.login}`);
            return undefined;
          } catch (error) {
            return this.manejarError(error, campos);
          }
        },
      },
    },
  );

  // traduce las respuestas de error del backend a mensajes en el campo que corresponde
  private manejarError(error: unknown, campos: FieldTree<RegistroForm>): TreeValidationResult {
    if (!(error instanceof HttpErrorResponse)) {
      this.errorMsg.set('Ocurrió un error inesperado. Intentá de nuevo');
      return undefined;
    }

    const mensaje: string = error.error?.message ?? '';

    if (error.status === 409 && mensaje.includes('DNI')) {
      return [{ fieldTree: campos.dni, kind: 'servidor', message: 'Ya existe un cliente con ese DNI' }];
    }
    if (error.status === 409 && mensaje.toLowerCase().includes('email')) {
      return [{ fieldTree: campos.email, kind: 'servidor', message: 'Ya existe un cliente con ese email' }];
    }
    if (error.status === 403 && mensaje.includes('nombre de usuario')) {
      return [{ fieldTree: campos.nombreUsuario, kind: 'servidor', message: 'Ese nombre de usuario ya está en uso' }];
    }
    if (error.status === 409 && /tel[eé]fono/i.test(mensaje)) {
      return [{ fieldTree: campos.telefono, kind: 'servidor', message: 'Ya existe un cliente con ese teléfono' }];
    }
    if (error.status === 403 && mensaje.includes('Acceso Restringido')) {
      return [{ fieldTree: campos.telefono, kind: 'servidor', message: 'No se pudo registrar: revisá que el teléfono no esté ya registrado' }];
    }

    this.errorMsg.set(
      error.status === 0
        ? 'No se pudo conectar con el servidor'
        : error.status === 400
          ? 'Revisá los datos ingresados'
          : 'Ocurrió un error inesperado. Intentá de nuevo',
    );
    return undefined;
  }
}
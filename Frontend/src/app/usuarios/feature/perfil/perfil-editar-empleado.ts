import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, input, OnInit, output, signal } from '@angular/core';
import {
  disabled,
  FieldTree,
  form,
  FormField,
  FormRoot,
  maxLength,
  pattern,
  TreeValidationResult,
} from '@angular/forms/signals';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { firstValueFrom } from 'rxjs';
import { buscarPais, PAISES, separarTelefono } from '../../../core/constants/paises.constants';
import { noBlanco, telefonoSegunPais, tope } from '../../../core/validators/validadores';
import { Dialogos } from '../../../shared/mensaje-dialog/dialogos';
import { UsuarioClient } from '../../data-access/usuarios.client';
import { PerfilEmpleado } from '../../data-access/usuario-model';
import { LIMITES } from '../../../core/constants/limites.constants';

interface EditarEmpleadoForm {
  email: string;
  pais: string;
  telefono: string;
}

/** Un empleado modifica solo su email y su teléfono: el resto de su ficha lo gestiona el administrador. */
@Component({
  selector: 'app-perfil-editar-empleado',
  imports: [FormRoot, FormField, MatInputModule, MatButtonModule, MatProgressSpinnerModule, MatSelectModule],
  templateUrl: './perfil-editar-empleado.html',
  styleUrl: './perfil-form.css',
})
export class PerfilEditarEmpleado implements OnInit {
  private readonly usuarioClient = inject(UsuarioClient);
  private readonly dialogos = inject(Dialogos);

  readonly empleado = input.required<PerfilEmpleado>();
  readonly guardado = output<PerfilEmpleado>();
  readonly cancelado = output<void>();

  protected readonly paises = PAISES;

  protected readonly limites = LIMITES;

  private readonly modelo = signal<EditarEmpleadoForm>({ email: '', pais: PAISES[0].prefijo, telefono: '' });

  protected readonly paisElegido = computed(() => buscarPais(this.modelo().pais) ?? PAISES[0]);

  protected readonly hayCambios = computed(() => {
    const actual = this.modelo();
    const original = this.empleado();
    return actual.email.trim() !== original.email || actual.pais + actual.telefono.trim() !== original.telefono;
  });

  protected readonly formulario = form(
    this.modelo,
    (campos) => {
      noBlanco(campos.email, 'El email es obligatorio');
      tope(campos.email, LIMITES.email);
      pattern(campos.email, /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$/, {
        message: 'Formato inválido: en minúsculas y con dominio de 2 o 3 letras (ej: nombre@correo.com)',
      });
      telefonoSegunPais(campos.telefono, campos.pais);
      disabled(campos, { when: ({ state }) => state.submitting() });
    },
    {
      submission: {
        action: async (campos) => {
          if (!this.hayCambios()) {
            await this.dialogos.avisar('info', 'Sin cambios', 'No modificaste ningún dato.');
            return undefined;
          }
          const confirmado = await this.dialogos.confirmar(
            'Guardar cambios',
            '¿Querés guardar los cambios en tus datos?',
            'Guardar cambios',
          );
          if (!confirmado) return undefined;

          try {
            const { email, pais, telefono } = campos().value();
            const actualizado = await firstValueFrom(
              this.usuarioClient.actualizarMiFichaEmpleado({ email: email.trim(), telefono: pais + telefono.trim() }),
            );
            await this.dialogos.avisar('exito', 'Datos actualizados', 'Tus datos se guardaron correctamente.');
            this.guardado.emit(actualizado);
            return undefined;
          } catch (error) {
            return await this.manejarError(error, campos);
          }
        },
      },
    },
  );

  ngOnInit(): void {
    const { email, telefono } = this.empleado();
    const { pais, numero } = separarTelefono(telefono);
    this.modelo.set({ email, pais, telefono: numero });
  }

  private async manejarError(error: unknown, campos: FieldTree<EditarEmpleadoForm>): Promise<TreeValidationResult> {
    const mensaje = error instanceof HttpErrorResponse ? String(error.error?.message ?? '') : '';
    const estado = error instanceof HttpErrorResponse ? error.status : -1;

    if (estado === 409 && mensaje.toLowerCase().includes('email')) {
      return [{ fieldTree: campos.email, kind: 'servidor', message: 'Ya existe un empleado con ese email' }];
    }
    await this.dialogos.avisar(
      'error',
      'No se pudieron guardar los cambios',
      estado === 0
        ? 'No se pudo conectar con el servidor. Revisá tu conexión e intentá de nuevo.'
        : estado === 403
          ? 'Tu cuenta no puede modificar sus datos en este momento.'
          : 'Ocurrió un error inesperado. Intentá de nuevo en unos minutos.',
    );
    return undefined;
  }
}
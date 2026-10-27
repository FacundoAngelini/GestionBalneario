import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, input, OnInit, output, signal } from '@angular/core';
import {
  disabled,
  FieldTree,
  form,
  FormField,
  FormRoot,
  pattern,
  TreeValidationResult,
} from '@angular/forms/signals';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { firstValueFrom } from 'rxjs';
import { buscarPais, PAISES, separarTelefono } from '../../../core/constants/paises.constants';
import { noBlanco, telefonoSegunPais } from '../../../core/validators/validadores';
import { Dialogos } from '../../../shared/mensaje-dialog/dialogos';
import { UsuarioClient } from '../../data-access/usuarios.client';
import { PerfilCliente } from '../../data-access/usuario-model';

interface EditarPerfilForm {
  nombre: string;
  apellido: string;
  email: string;
  pais: string;
  telefono: string;
}

@Component({
  selector: 'app-perfil-editar',
  imports: [
    FormRoot,
    FormField,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSelectModule,
  ],
  templateUrl: './perfil-editar.html',
  styleUrl: './perfil-form.css',
})
export class PerfilEditar implements OnInit {
  private readonly usuarioClient = inject(UsuarioClient);
  private readonly dialogos = inject(Dialogos);

  readonly cliente = input.required<PerfilCliente>();
  readonly guardado = output<PerfilCliente>();
  readonly cancelado = output<void>();

  protected readonly paises = PAISES;

  private readonly modelo = signal<EditarPerfilForm>({
    nombre: '',
    apellido: '',
    email: '',
    pais: PAISES[0].prefijo,
    telefono: '',
  });

  protected readonly paisElegido = computed(() => buscarPais(this.modelo().pais) ?? PAISES[0]);

  protected readonly hayCambios = computed(() => {
    const actual = this.modelo();
    const original = this.cliente();
    return (
      actual.nombre.trim() !== original.nombre ||
      actual.apellido.trim() !== original.apellido ||
      actual.email.trim() !== original.email ||
      actual.pais + actual.telefono.trim() !== original.telefono
    );
  });

  protected readonly formulario = form(
    this.modelo,
    (campos) => {
      noBlanco(campos.nombre, 'El nombre es obligatorio');
      noBlanco(campos.apellido, 'El apellido es obligatorio');

      noBlanco(campos.email, 'El email es obligatorio');
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
            '¿Querés guardar los cambios en tus datos personales?',
            'Guardar cambios',
          );
          if (!confirmado) return undefined;

          try {
            const { pais, telefono, nombre, apellido, email } = campos().value();
            const actualizado = await firstValueFrom(
              this.usuarioClient.actualizarMiPerfil({
                nombre: nombre.trim(),
                apellido: apellido.trim(),
                email: email.trim(),
                telefono: pais + telefono.trim(),
              }),
            );
            await this.dialogos.avisar('exito', 'Datos actualizados', 'Tus datos personales se guardaron correctamente.');
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
    const { nombre, apellido, email, telefono } = this.cliente();
    const { pais, numero } = separarTelefono(telefono);
    this.modelo.set({ nombre, apellido, email, pais, telefono: numero });
  }

  protected avisarDni(): void {
    void this.dialogos.avisar(
      'advertencia',
      'No podés modificar tu DNI',
      'Tu DNI no se puede cambiar desde esta pantalla. Si necesitás modificarlo, comunicate con Administración.',
    );
  }

  // los errores que corresponden a un campo se muestran en el campo; el resto, en un modal
  private async manejarError(error: unknown, campos: FieldTree<EditarPerfilForm>): Promise<TreeValidationResult> {
    const mensaje = error instanceof HttpErrorResponse ? String(error.error?.message ?? '') : '';
    const estado = error instanceof HttpErrorResponse ? error.status : -1;

    if (estado === 409 && mensaje.toLowerCase().includes('email')) {
      return [{ fieldTree: campos.email, kind: 'servidor', message: 'Ya existe un cliente con ese email' }];
    }
    if (estado === 409 && /tel[eé]fono/i.test(mensaje)) {
      return [{ fieldTree: campos.telefono, kind: 'servidor', message: 'Ya existe un cliente con ese teléfono' }];
    }

    await this.dialogos.avisar(
      'error',
      'No se pudieron guardar los cambios',
      estado === 0
        ? 'No se pudo conectar con el servidor. Revisá tu conexión e intentá de nuevo.'
        : estado === 400
          ? 'Revisá los datos ingresados e intentá de nuevo.'
          : 'Ocurrió un error inesperado. Intentá de nuevo en unos minutos.',
    );
    return undefined;
  }
}
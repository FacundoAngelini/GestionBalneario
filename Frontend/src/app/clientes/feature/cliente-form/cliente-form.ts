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
  required,
  TreeValidationResult,
  validate,
} from '@angular/forms/signals';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { buscarPais, PAISES, separarTelefono } from '../../../core/constants/paises.constants';
import { mensajeAmigable } from '../../../core/utils/errores';
import { errorContrasenia, errorEmail } from '../../../core/utils/validaciones';
import { noBlanco, regla, telefonoSegunPais } from '../../../core/validators/validadores';
import { SeleccionReserva } from '../../../reservas/data-access/seleccion-reserva';
import { OjoContrasenia } from '../../../shared/ojo-contrasenia/ojo-contrasenia';
import { ClienteDatosRequest } from '../../data-access/cliente-model';
import { ClienteService } from '../../data-access/cliente-service';

interface ClienteFormModelo {
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
  selector: 'app-cliente-form',
  imports: [
    FormRoot,
    FormField,
    RouterLink,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    OjoContrasenia,
  ],
  templateUrl: './cliente-form.html',
  styleUrl: './cliente-form.css',
})
export class ClienteForm {
  private readonly clienteService = inject(ClienteService);
  private readonly seleccion = inject(SeleccionReserva);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);
  private readonly ruta = inject(ActivatedRoute).snapshot;

  // Sin id en la ruta es un alta; con id, una edición
  protected readonly id = this.ruta.paramMap.get('id');
  protected readonly esAlta = this.id === null;
  // Si viene de la reserva presencial, al crear al cliente se sigue con su reserva
  private readonly desdeReserva = this.esAlta && this.ruta.queryParamMap.get('reserva') === '1';
  protected readonly paises = PAISES;

  protected readonly cargando = signal(!this.esAlta);
  protected readonly errorCarga = signal('');
  protected readonly errorEnvio = signal('');

  private readonly modelo = signal<ClienteFormModelo>({
    nombre: '',
    apellido: '',
    dni: this.dniInicial(),
    email: '',
    pais: '54',
    telefono: '',
    nombreUsuario: '',
    contrasenia: '',
  });

  protected readonly paisElegido = computed(() => buscarPais(this.modelo().pais) ?? PAISES[0]);

  protected readonly formulario = form(
    this.modelo,
    (campos) => {
      noBlanco(campos.nombre, 'El nombre es obligatorio');
      maxLength(campos.nombre, 50, { message: 'Máximo 50 caracteres' });
      noBlanco(campos.apellido, 'El apellido es obligatorio');
      maxLength(campos.apellido, 50, { message: 'Máximo 50 caracteres' });

      required(campos.dni, { message: 'El DNI es obligatorio' });
      min(campos.dni, 1_000_000, { message: 'El DNI debe ser un número válido' });
      max(campos.dni, 99_999_999, { message: 'El DNI es demasiado grande' });

      regla(campos.email, errorEmail);
      telefonoSegunPais(campos.telefono, campos.pais);

      // Solo en el alta: usuario y contraseña provisoria
      validate(campos.nombreUsuario, ({ value }) =>
        this.esAlta && value().trim() === ''
          ? { kind: 'requerido', message: 'El nombre de usuario es obligatorio' }
          : undefined,
      );
      validate(campos.contrasenia, ({ value }) => {
        const mensaje = this.esAlta ? errorContrasenia(value()) : null;
        return mensaje ? { kind: 'clave', message: mensaje } : undefined;
      });

      // Mientras se envía, se bloquean todos los campos
      disabled(campos, { when: ({ state }) => state.submitting() });
    },
    {
      submission: {
        action: async (campos) => {
          this.errorEnvio.set('');
          const m = campos().value();
          const datos: ClienteDatosRequest = {
            nombre: m.nombre.trim(),
            apellido: m.apellido.trim(),
            dni: Number(m.dni),
            email: m.email.trim(),
            telefono: m.pais + m.telefono.trim(),
          };

          try {
            const cliente = await firstValueFrom(
              this.id
                ? this.clienteService.actualizar(this.id, datos)
                : this.clienteService.crear({
                    ...datos,
                    credencial: { nombreUsuario: m.nombreUsuario.trim(), contrasenia: m.contrasenia },
                  }),
            );

            if (this.desdeReserva) {
              this.seleccion.limpiar();
              this.seleccion.clienteMostrador.set({
                publicId: cliente.publicId,
                nombre: cliente.nombre,
                apellido: cliente.apellido,
                dni: cliente.dni,
                activo: true,
              });
              this.snackBar.open('Cliente creado. Entregale su contraseña y elegí los recursos.', 'Cerrar', { duration: 10000 });
              await this.router.navigateByUrl('/catalogo');
            } else {
              this.snackBar.open(
                this.esAlta
                  ? 'Cliente creado. Entregale su contraseña y recomendale cambiarla en "Mi perfil".'
                  : 'Cambios guardados',
                'Cerrar',
                { duration: this.esAlta ? 10000 : 5000 },
              );
              await this.router.navigate(['/clientes', cliente.publicId]);
            }
            return undefined;
          } catch (error) {
            return this.manejarError(error, campos);
          }
        },
      },
    },
  );

  constructor() {
    if (this.id) this.cargarFicha(this.id);
  }

  // DNI que llega desde la búsqueda de la reserva presencial
  private dniInicial(): number | null {
    const dni = Number(this.ruta.queryParamMap.get('dni'));
    return this.esAlta && Number.isInteger(dni) && dni > 0 ? dni : null;
  }

  private cargarFicha(id: string): void {
    this.clienteService.ficha(id).subscribe({
      next: (c) => {
        const { pais, numero } = separarTelefono(c.telefono);
        this.modelo.update((m) => ({
          ...m,
          nombre: c.nombre,
          apellido: c.apellido,
          dni: c.dni,
          email: c.email,
          pais,
          telefono: numero,
        }));
        this.cargando.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.cargando.set(false);
        this.errorCarga.set(
          e.status === 404 ? 'No se encontró al cliente.' : mensajeAmigable(e, 'No se pudo cargar la ficha.'),
        );
      },
    });
  }

  private manejarError(error: unknown, campos: FieldTree<ClienteFormModelo>): TreeValidationResult {
    if (!(error instanceof HttpErrorResponse)) {
      this.errorEnvio.set('Ocurrió un error inesperado. Intentá de nuevo');
      return undefined;
    }
    const mensaje = String(error.error?.message ?? '');

    if (error.status === 409) {
      if (/DNI/i.test(mensaje)) {
        return [{ fieldTree: campos.dni, kind: 'servidor', message: 'Ya existe un cliente con ese DNI' }];
      }
      if (/email/i.test(mensaje)) {
        return [{ fieldTree: campos.email, kind: 'servidor', message: 'Ya existe un cliente con ese email' }];
      }
      if (/tel[eé]fono/i.test(mensaje)) {
        return [{ fieldTree: campos.telefono, kind: 'servidor', message: 'Ya existe un cliente con ese teléfono' }];
      }
      if (/nombre de usuario/i.test(mensaje)) {
        return [{ fieldTree: campos.nombreUsuario, kind: 'servidor', message: 'Ese nombre de usuario ya está en uso' }];
      }
    }
    this.errorEnvio.set(mensajeAmigable(error, 'No se pudieron guardar los datos. Intentá de nuevo.'));
    return undefined;
  }
}

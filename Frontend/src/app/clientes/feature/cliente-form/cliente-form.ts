import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import {
  disabled,
  FieldTree,
  form,
  FormField,
  FormRoot,
  max,
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
import { LIMITES } from '../../../core/constants/limites.constants';
import { buscarPais, PAISES, separarTelefono } from '../../../core/constants/paises.constants';
import { mensajeAmigable } from '../../../core/utils/errores';
import { errorContrasenia, errorEmail } from '../../../core/utils/validaciones';
import { noBlanco, regla, telefonoSegunPais, tope } from '../../../core/validators/validadores';
import { SeleccionReserva } from '../../../reservas/data-access/seleccion-reserva';
import { Dialogos } from '../../../shared/mensaje-dialog/dialogos';
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

const MODELO_INICIAL: ClienteFormModelo = {
  nombre: '',
  apellido: '',
  dni: null,
  email: '',
  pais: '54',
  telefono: '',
  nombreUsuario: '',
  contrasenia: '',
};

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
  private readonly dialogos = inject(Dialogos);
  private readonly ruta = inject(ActivatedRoute).snapshot;

  // Sin id en la ruta es un alta; con id, una edición
  protected readonly id = this.ruta.paramMap.get('id');
  protected readonly esAlta = this.id === null;
  // Si viene de la reserva presencial, al crear al cliente se sigue con su reserva
  private readonly desdeReserva = this.esAlta && this.ruta.queryParamMap.get('reserva') === '1';
  protected readonly paises = PAISES;
  protected readonly limites = LIMITES;

  protected readonly cargando = signal(!this.esAlta);
  protected readonly errorCarga = signal('');
  protected readonly errorEnvio = signal('');

  private readonly modelo = signal<ClienteFormModelo>({ ...MODELO_INICIAL, dni: this.dniInicial() });

  protected readonly paisElegido = computed(() => buscarPais(this.modelo().pais) ?? PAISES[0]);

  protected readonly formulario = form(
    this.modelo,
    (campos) => {
      noBlanco(campos.nombre, 'El nombre es obligatorio');
      tope(campos.nombre, LIMITES.nombre);
      noBlanco(campos.apellido, 'El apellido es obligatorio');
      tope(campos.apellido, LIMITES.apellido);

      required(campos.dni, { message: 'El DNI es obligatorio' });
      min(campos.dni, 1_000_000, { message: 'El DNI debe ser un número válido' });
      max(campos.dni, 99_999_999, { message: 'El DNI es demasiado grande' });

      regla(campos.email, errorEmail);
      tope(campos.email, LIMITES.email);
      telefonoSegunPais(campos.telefono, campos.pais);

      // Solo en el alta: usuario y contraseña provisoria
      validate(campos.nombreUsuario, ({ value }) =>
        this.esAlta && value().trim() === ''
          ? { kind: 'requerido', message: 'El nombre de usuario es obligatorio' }
          : undefined,
      );
      tope(campos.nombreUsuario, LIMITES.nombreUsuario);
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

            if (!this.esAlta) {
              this.snackBar.open('Cambios guardados', 'Cerrar', { duration: 5000 });
              await this.router.navigate(['/clientes', cliente.publicId]);
              return undefined;
            }

            const aviso =
              `${cliente.nombre} ${cliente.apellido} ya puede iniciar sesión con el usuario "${m.nombreUsuario.trim()}".\n\n` +
              'Entregale la contraseña que definiste y recomendale cambiarla cuanto antes en "Mi perfil". No se vuelve a mostrar.';

            if (this.desdeReserva) {
              // Se sigue con la reserva a nombre del cliente recién creado
              this.seleccion.limpiar();
              this.seleccion.clienteMostrador.set({
                publicId: cliente.publicId,
                nombre: cliente.nombre,
                apellido: cliente.apellido,
                dni: cliente.dni,
                activo: true,
              });
              await this.dialogos.avisar('exito', 'Cliente creado', `${aviso}\n\nAhora elegí los recursos de su reserva.`);
              await this.router.navigateByUrl('/catalogo');
              return undefined;
            }

            const eleccion = await this.dialogos.elegir('exito', 'Cliente creado', aviso, 'Ver ficha', 'Crear otro cliente');
            if (eleccion === 'secundaria') {
              this.modelo.set({ ...MODELO_INICIAL });
              this.formulario().reset(); // limpia también lo "tocado", para que no aparezcan errores en un formulario nuevo
              return undefined;
            }
            await this.router.navigate(['/clientes', cliente.publicId]);
            return undefined;
          } catch (error) {
            return this.manejarError(error, campos);
          }
        },
      },
    },
  );

  // Qué campos impiden guardar, también los que vienen cargados y no se tocaron
  protected readonly problemas = computed(() => {
    const f = this.formulario;
    const campos: { etiqueta: string; errores: () => readonly { message?: string }[] }[] = [
      { etiqueta: 'Nombre', errores: () => f.nombre().errors() },
      { etiqueta: 'Apellido', errores: () => f.apellido().errors() },
      { etiqueta: 'DNI', errores: () => f.dni().errors() },
      { etiqueta: 'Email', errores: () => f.email().errors() },
      { etiqueta: 'Teléfono', errores: () => f.telefono().errors() },
      { etiqueta: 'Nombre de usuario', errores: () => f.nombreUsuario().errors() },
      { etiqueta: 'Contraseña', errores: () => f.contrasenia().errors() },
    ];
    return campos
      .map((c) => ({ etiqueta: c.etiqueta, mensaje: c.errores().at(0)?.message ?? '' }))
      .filter((p) => p.mensaje !== '');
  });

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

  // Traduce los errores del backend a mensajes en el campo que corresponde
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
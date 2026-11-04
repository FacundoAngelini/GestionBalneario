import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
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
import { catchError, firstValueFrom, of } from 'rxjs';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { buscarPais, PAISES, separarTelefono } from '../../../core/constants/paises.constants';
import { mensajeAmigable } from '../../../core/utils/errores';
import { errorContrasenia, errorCuit, errorEmail } from '../../../core/utils/validaciones';
import { noBlanco, regla, telefonoSegunPais, tope } from '../../../core/validators/validadores';
import { Sector } from '../../../recursos/data-access/recurso-model';
import { RecursoService } from '../../../recursos/data-access/recurso-service';
import { OjoContrasenia } from '../../../shared/ojo-contrasenia/ojo-contrasenia';
import { EmpleadoUpdateRequest, ROLES_ALTA } from '../../data-access/empleado-model';
import { EmpleadoService } from '../../data-access/empleado-service';
import { LIMITES } from '../../../core/constants/limites.constants';
import { Dialogos } from '../../../shared/mensaje-dialog/dialogos';

interface EmpleadoFormModelo {
  nombre: string;
  apellido: string;
  dni: number | null;
  cuit: string;
  email: string;
  pais: string;
  telefono: string;
  sueldo: number | null;
  calle: string;
  numero: number | null;
  ciudad: string;
  provincia: string;
  sectorPublicId: string;
  rolSolicitado: string;
  nombreUsuario: string;
  contrasenia: string;
}

const MODELO_INICIAL: EmpleadoFormModelo = {
  nombre: '',
  apellido: '',
  dni: null,
  cuit: '',
  email: '',
  pais: '54',
  telefono: '',
  sueldo: null,
  calle: '',
  numero: null,
  ciudad: '',
  provincia: '',
  sectorPublicId: '',
  rolSolicitado: '',
  nombreUsuario: '',
  contrasenia: '',
};

@Component({
  selector: 'app-empleado-form',
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
  templateUrl: './empleados-form.html',
  styleUrl: './empleados-form.css',
})
export class EmpleadoForm {
  private readonly empleadoService = inject(EmpleadoService);
  private readonly recursoService = inject(RecursoService);
  private readonly auth = inject(AuthClient);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);
  protected readonly limites = LIMITES;
  private readonly dialogos = inject(Dialogos);

  // Sin id en la ruta es un alta; con id, una edición
  protected readonly id = inject(ActivatedRoute).snapshot.paramMap.get('id');
  protected readonly esAlta = this.id === null;
  protected readonly paises = PAISES;

  protected readonly sectores = toSignal(
    this.recursoService.listarSectores().pipe(catchError(() => of([] as Sector[]))),
    { initialValue: [] as Sector[] },
  );

  // El administrador crea cualquier rol; el gerente, solo los de menor rango
  protected readonly opcionesRol = computed(() =>
    ROLES_ALTA.filter((r) => !r.soloAdmin || this.auth.esAdmin()),
  );

  protected readonly cargando = signal(!this.esAlta);
  protected readonly errorCarga = signal('');
  protected readonly errorEnvio = signal('');

  private readonly modelo = signal<EmpleadoFormModelo>({ ...MODELO_INICIAL });

  protected readonly paisElegido = computed(() => buscarPais(this.modelo().pais) ?? PAISES[0]);

    // Cajero, mozo y repartidor se pueden crear, pero todavía no tienen pantallas
  protected readonly rolSinPantallas = computed(() =>
    ['ROLE_CAJERO', 'ROLE_MOZO', 'ROLE_REPARTIDOR'].includes(this.modelo().rolSolicitado),
  );

    // Qué campos impiden guardar, también los que vienen cargados y no se tocaron
  protected readonly problemas = computed(() => {
    const f = this.formulario;
    const campos: { etiqueta: string; errores: () => readonly { message?: string }[] }[] = [
      { etiqueta: 'Nombre', errores: () => f.nombre().errors() },
      { etiqueta: 'Apellido', errores: () => f.apellido().errors() },
      { etiqueta: 'DNI', errores: () => f.dni().errors() },
      { etiqueta: 'CUIT', errores: () => f.cuit().errors() },
      { etiqueta: 'Email', errores: () => f.email().errors() },
      { etiqueta: 'Teléfono', errores: () => f.telefono().errors() },
      { etiqueta: 'Calle', errores: () => f.calle().errors() },
      { etiqueta: 'Número de la calle', errores: () => f.numero().errors() },
      { etiqueta: 'Ciudad', errores: () => f.ciudad().errors() },
      { etiqueta: 'Provincia', errores: () => f.provincia().errors() },
      { etiqueta: 'Sector', errores: () => f.sectorPublicId().errors() },
      { etiqueta: 'Sueldo', errores: () => f.sueldo().errors() },
      { etiqueta: 'Rol', errores: () => f.rolSolicitado().errors() },
      { etiqueta: 'Nombre de usuario', errores: () => f.nombreUsuario().errors() },
      { etiqueta: 'Contraseña', errores: () => f.contrasenia().errors() },
    ];
    return campos
      .map((c) => ({ etiqueta: c.etiqueta, mensaje: c.errores().at(0)?.message ?? '' }))
      .filter((p) => p.mensaje !== '');
  });

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

      regla(campos.cuit, errorCuit);
      regla(campos.email, errorEmail);
      tope(campos.email, LIMITES.email);
      telefonoSegunPais(campos.telefono, campos.pais);

      required(campos.sueldo, { message: 'Ingresá el sueldo' });
      min(campos.sueldo, 0.01, { message: 'El sueldo debe ser mayor a 0' });
      max(campos.sueldo, 999_999_999, { message: 'El sueldo es demasiado grande' });

      noBlanco(campos.calle, 'La calle es obligatoria');
      tope(campos.calle, LIMITES.calle);
      required(campos.numero, { message: 'Ingresá el número de la calle' });
      min(campos.numero, 0, { message: 'El número de la calle no es válido' });
      max(campos.numero, 15000, { message: 'El número de la calle es demasiado grande' });
      noBlanco(campos.ciudad, 'La ciudad es obligatoria');
      tope(campos.ciudad, LIMITES.ciudad);
      noBlanco(campos.provincia, 'La provincia es obligatoria');
      tope(campos.provincia, LIMITES.provincia);

      noBlanco(campos.sectorPublicId, 'Elegí un sector');

      // Solo en el alta: rol, usuario y contraseña inicial
      validate(campos.rolSolicitado, ({ value }) =>
        this.esAlta && !value() ? { kind: 'requerido', message: 'Elegí un rol' } : undefined,
      );
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
          const datos: EmpleadoUpdateRequest = {
            nombre: m.nombre.trim(),
            apellido: m.apellido.trim(),
            dni: Number(m.dni),
            email: m.email.trim(),
            sueldo: Number(m.sueldo),
            cuit: m.cuit.trim(),
            telefono: m.pais + m.telefono.trim(),
            direccion: {
              calle: m.calle.trim(),
              numero: Number(m.numero),
              ciudad: m.ciudad.trim(),
              provincia: m.provincia.trim(),
            },
            sectorPublicId: m.sectorPublicId,
          };

          try {
            const empleado = await firstValueFrom(
              this.id
                ? this.empleadoService.actualizar(this.id, datos)
                : this.empleadoService.crear({
                    ...datos,
                    rolSolicitado: m.rolSolicitado,
                    credencial: { nombreUsuario: m.nombreUsuario.trim(), contrasenia: m.contrasenia },
                  }),
            );
            if (this.esAlta) {
              const eleccion = await this.dialogos.elegir(
                'exito',
                'Empleado creado',
                `${empleado.nombre} ${empleado.apellido} ya puede iniciar sesión con el usuario "${m.nombreUsuario.trim()}".\n\n` +
                  'Entregale la contraseña que definiste y recomendale cambiarla cuanto antes en "Mi perfil". No se vuelve a mostrar.',
                'Ver ficha',
                'Crear otro empleado',
              );
              if (eleccion === 'secundaria') {
                this.modelo.set({ ...MODELO_INICIAL });
                this.formulario().reset(); // limpia también lo "tocado", para que no aparezcan errores en un formulario nuevo
                return undefined;
              }
            } else {
              this.snackBar.open('Cambios guardados', 'Cerrar', { duration: 5000 });
            }
            await this.router.navigate(['/empleados', empleado.publicId]);
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

  private cargarFicha(id: string): void {
    this.empleadoService.buscar(id).subscribe({
      next: (e) => {
        if (!e.editable) {
          this.cargando.set(false);
          this.errorCarga.set('No podés modificar a este empleado (es de tu mismo rango o de uno superior).');
          return;
        }
        const { pais, numero } = separarTelefono(e.telefono);
        this.modelo.update((m) => ({
          ...m,
          nombre: e.nombre,
          apellido: e.apellido,
          dni: e.dni,
          cuit: e.cuit,
          email: e.email,
          pais,
          telefono: numero,
          sueldo: e.sueldo,
          calle: e.direccion?.calle ?? '',
          numero: e.direccion?.numero ?? null,
          ciudad: e.direccion?.ciudad ?? '',
          provincia: e.direccion?.provincia ?? '',
          sectorPublicId: e.sector?.publicId ?? '',
        }));
        this.cargando.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.cargando.set(false);
        this.errorCarga.set(
          e.status === 404 ? 'No se encontró al empleado.' : mensajeAmigable(e, 'No se pudo cargar la ficha.'),
        );
      },
    });
  }

  // Traduce los errores del backend a mensajes en el campo que corresponde
  private manejarError(error: unknown, campos: FieldTree<EmpleadoFormModelo>): TreeValidationResult {
    if (!(error instanceof HttpErrorResponse)) {
      this.errorEnvio.set('Ocurrió un error inesperado. Intentá de nuevo');
      return undefined;
    }
    const mensaje = String(error.error?.message ?? '');

    if (error.status === 409) {
      if (/DNI/i.test(mensaje)) {
        return [{ fieldTree: campos.dni, kind: 'servidor', message: 'Ya existe un empleado con ese DNI' }];
      }
      if (/email/i.test(mensaje)) {
        return [{ fieldTree: campos.email, kind: 'servidor', message: 'Ya existe un empleado con ese email' }];
      }
      if (/CUIT/i.test(mensaje)) {
        return [{ fieldTree: campos.cuit, kind: 'servidor', message: 'Ya existe un empleado con ese CUIT' }];
      }
      if (/nombre de usuario/i.test(mensaje)) {
        return [{ fieldTree: campos.nombreUsuario, kind: 'servidor', message: 'Ese nombre de usuario ya está en uso' }];
      }
    }
    this.errorEnvio.set(mensajeAmigable(error, 'No se pudieron guardar los datos. Intentá de nuevo.'));
    return undefined;
  }
}
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { form, FormField } from '@angular/forms/signals';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { catchError, of } from 'rxjs';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { buscarPais, PAISES, separarTelefono } from '../../../core/constants/paises.constants';
import { mensajeAmigable } from '../../../core/utils/errores';
import { formatoNumero, interpretarPrecio } from '../../../core/utils/moneda';
import {
  errorContrasenia,
  errorCuit,
  errorEmail,
  errorEntero,
  errorTelefono,
  esNumero,
} from '../../../core/utils/validaciones';
import { Sector } from '../../../recursos/data-access/recurso-model';
import { RecursoService } from '../../../recursos/data-access/recurso-service';
import { EmpleadoUpdateRequest, ROLES_ALTA } from '../../data-access/empleado-model';
import { EmpleadoService } from '../../data-access/empleado-service';

@Component({
  selector: 'app-empleado-form',
  imports: [FormField, RouterLink],
  templateUrl: './empleados-form.html',
  styleUrl: './empleados-form.css',
})
export class EmpleadoForm {
  private readonly empleadoService = inject(EmpleadoService);
  private readonly recursoService = inject(RecursoService);
  private readonly auth = inject(AuthClient);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly id = inject(ActivatedRoute).snapshot.paramMap.get('id');
  protected readonly esAlta = this.id === null;
  protected readonly paises = PAISES;

  protected readonly sectores = toSignal(
    this.recursoService.listarSectores().pipe(catchError(() => of([] as Sector[]))),
    { initialValue: [] as Sector[] },
  );

  protected readonly opcionesRol = computed(() =>
    ROLES_ALTA.filter((r) => !r.soloAdmin || this.auth.esAdmin()),
  );

  protected readonly modelo = signal({
    nombre: '',
    apellido: '',
    dni: null as number | null,
    cuit: '',
    email: '',
    pais: '54',
    telefono: '',
    sueldo: null as number | null,
    calle: '',
    numero: null as number | null,
    ciudad: '',
    provincia: '',
    sectorPublicId: '',
    rolSolicitado: '',
    nombreUsuario: '',
    contrasenia: '',
  });
  protected readonly f = form(this.modelo);
  protected readonly textoSueldo = signal(''); 
  protected readonly verContrasenia = signal(false);

  protected readonly cargando = signal(!this.esAlta);
  protected readonly enviando = signal(false);
  protected readonly errorCarga = signal('');
  protected readonly errorEnvio = signal('');

  protected readonly paisElegido = computed(() => buscarPais(this.modelo().pais) ?? PAISES[0]);

  protected readonly errores = computed(() => {
    const m = this.modelo();
    const vacio = (valor: string, mensaje: string) => (valor.trim() ? null : mensaje);

    return {
      nombre: vacio(m.nombre, 'El nombre es obligatorio'),
      apellido: vacio(m.apellido, 'El apellido es obligatorio'),
      dni: errorEntero(m.dni, 1_000_000, 99_999_999, 'El DNI debe ser un número válido (7 u 8 dígitos)', 'El DNI es obligatorio'),
      cuit: errorCuit(m.cuit),
      email: errorEmail(m.email),
      telefono: errorTelefono(m.pais, m.telefono),
      sueldo: !esNumero(m.sueldo) ? 'Ingresá el sueldo' : m.sueldo <= 0 ? 'Tiene que ser mayor a 0' : null,
      calle: vacio(m.calle, 'La calle es obligatoria'),
      numero: errorEntero(m.numero, 0, 99_999, 'El número de la calle no es válido', 'Ingresá el número de la calle'),
      ciudad: vacio(m.ciudad, 'La ciudad es obligatoria'),
      provincia: vacio(m.provincia, 'La provincia es obligatoria'),
      sector: m.sectorPublicId ? null : 'Elegí un sector',
      // Solo en el alta
      rol: this.esAlta && !m.rolSolicitado ? 'Elegí un rol' : null,
      usuario: this.esAlta ? vacio(m.nombreUsuario, 'El nombre de usuario es obligatorio') : null,
      contrasenia: this.esAlta ? errorContrasenia(m.contrasenia) : null,
    };
  });
  protected readonly valido = computed(() => Object.values(this.errores()).every((e) => e === null));

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
        this.textoSueldo.set(e.sueldo !== null ? formatoNumero(e.sueldo) : '');
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

  protected cambiarSueldo(evento: Event): void {
    const input = evento.target as HTMLInputElement;
    const { texto, valor } = interpretarPrecio(input.value);
    input.value = texto;
    this.textoSueldo.set(texto);
    this.modelo.update((m) => ({ ...m, sueldo: valor }));
  }

  protected guardar(evento: Event): void {
    evento.preventDefault();
    if (!this.valido() || this.enviando()) return;

    const m = this.modelo();
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

    const peticion = this.id
      ? this.empleadoService.actualizar(this.id, datos)
      : this.empleadoService.crear({
          ...datos,
          rolSolicitado: m.rolSolicitado,
          credencial: { nombreUsuario: m.nombreUsuario.trim(), contrasenia: m.contrasenia },
        });

    this.enviando.set(true);
    this.errorEnvio.set('');
    peticion.subscribe({
      next: (empleado) => {
        this.enviando.set(false);
        this.snackBar.open(
          this.esAlta
            ? 'Empleado creado. Entregale su contraseña y recomendale cambiarla en "Mi perfil".'
            : 'Cambios guardados',
          'Cerrar',
          { duration: this.esAlta ? 10000 : 5000 },
        );
        void this.router.navigate(['/empleados', empleado.publicId]);
      },
      error: (e: HttpErrorResponse) => {
        this.enviando.set(false);
        this.errorEnvio.set(mensajeAmigable(e, 'No se pudieron guardar los datos. Intentá de nuevo.'));
      },
    });
  }
}

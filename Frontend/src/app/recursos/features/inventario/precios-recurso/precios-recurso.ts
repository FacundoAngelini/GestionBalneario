import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, input, output, signal } from '@angular/core';
import { form, FormField } from '@angular/forms/signals';
import { MAT_DATE_LOCALE, provideNativeDateAdapter } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSnackBar } from '@angular/material/snack-bar';
import { aIso, desdeIso, formatoRango, hoyIso } from '../../../../core/utils/fechas';
import { formatoNumero, formatoPrecio, interpretarPrecio } from '../../../../core/utils/moneda';
import { esNumero } from '../../../../core/utils/validaciones';
import {
  ETIQUETA_MODALIDAD,
  Modalidad,
  PrecioRecurso,
  PrecioRecursoRequest,
  RecursoInventario,
  Temporada,
} from '../../../data-access/recurso-model';
import { RecursoService } from '../../../data-access/recurso-service';

const NUEVO = 'nuevo'; // valor de editandoId cuando se está agregando un precio
const ORDEN_MODALIDAD: Modalidad[] = ['DIA', 'QUINCENA', 'MES', 'TEMPORADA'];

type EstadoPrecio = 'VIGENTE' | 'FUTURO' | 'VENCIDO';

const ETIQUETA_ESTADO_PRECIO: Record<EstadoPrecio, string> = {
  VIGENTE: 'Vigente',
  FUTURO: 'Futuro',
  VENCIDO: 'Vencido',
};

// Clases de color definidas en styles.css
const CLASE_ESTADO_PRECIO: Record<EstadoPrecio, string> = {
  VIGENTE: 'estado--confirmada',
  FUTURO: 'estado--pendiente',
  VENCIDO: 'estado--finalizada',
};

interface FormularioPrecio {
  modalidad: Modalidad;
  precio: number | null;
  fechaVigencia: string;
  fechaCaducada: string;
}

const FORMULARIO_VACIO: FormularioPrecio = {
  modalidad: 'DIA',
  precio: null,
  fechaVigencia: '',
  fechaCaducada: '',
};

@Component({
  selector: 'app-precios-recurso',
  imports: [FormField, MatDatepickerModule],
  providers: [provideNativeDateAdapter(), { provide: MAT_DATE_LOCALE, useValue: 'es-AR' }],
  templateUrl: './precios-recurso.html',
  styleUrl: './precios-recurso.css',
})
export class PreciosRecurso {
  private readonly recursoService = inject(RecursoService);
  private readonly snackBar = inject(MatSnackBar);

  readonly recurso = input.required<RecursoInventario>();
  readonly precios = input.required<PrecioRecurso[]>();
  readonly cargados = input(false);
  readonly esAdmin = input(false);
  readonly temporada = input<Temporada | null>(null);
  readonly guardado = output<void>();

  protected readonly etiquetaModalidad = ETIQUETA_MODALIDAD;
  protected readonly etiquetaEstado = ETIQUETA_ESTADO_PRECIO;
  protected readonly claseEstado = CLASE_ESTADO_PRECIO;
  protected readonly modalidades = ORDEN_MODALIDAD;
  protected readonly formatoPrecio = formatoPrecio;
  protected readonly formatoRango = formatoRango;

  // En celular el calendario se abre como ventana modal; en pantallas grandes como desplegable
  protected readonly esMovil =
    typeof window !== 'undefined' && window.matchMedia('(max-width: 40rem)').matches;

  // Para que los id de los campos sean únicos aunque haya varias tarjetas con el formulario abierto
  protected readonly sufijo = computed(() => this.recurso().publicId);

  // Por modalidad y dentro de cada una el período más reciente primero
  protected readonly ordenados = computed(() =>
    [...this.precios()].sort(
      (a, b) =>
        ORDEN_MODALIDAD.indexOf(a.modalidad) - ORDEN_MODALIDAD.indexOf(b.modalidad) ||
        b.fechaVigencia.localeCompare(a.fechaVigencia),
    ),
  );

  // Formulario: agregar o editar
  protected readonly editandoId = signal<string | null>(null);
  protected readonly esNuevo = computed(() => this.editandoId() === NUEVO);
  protected readonly modelo = signal<FormularioPrecio>({ ...FORMULARIO_VACIO });
  protected readonly f = form(this.modelo);
  protected readonly textoPrecio = signal(''); // lo que se ve en el campo, con puntos de mil
  protected readonly guardando = signal(false);
  protected readonly errorGuardado = signal('');

  protected readonly fechaDesde = computed(() => desdeIso(this.modelo().fechaVigencia));
  protected readonly fechaHasta = computed(() => desdeIso(this.modelo().fechaCaducada));

  protected readonly errores = computed(() => {
    const m = this.modelo();
    const editando = this.editandoId();

    let precio: string | null = null;
    if (!esNumero(m.precio)) precio = 'Ingresá el precio';
    else if (m.precio < 0) precio = 'No puede ser negativo';
    else if (m.precio === 0) precio = 'Tiene que ser mayor a 0';

    const desde = m.fechaVigencia ? null : 'Elegí la fecha de inicio';

    let hasta: string | null = null;
    if (!m.fechaCaducada) hasta = 'Elegí la fecha de fin';
    else if (m.fechaVigencia && m.fechaCaducada < m.fechaVigencia) {
      hasta = 'La fecha de fin no puede ser anterior a la de inicio';
    }

    // Dos precios de la misma modalidad no pueden regir a la vez
    let superposicion: string | null = null;
    if (m.fechaVigencia && m.fechaCaducada && !hasta) {
      const choque = this.precios().find(
        (p) =>
          p.publicId !== editando &&
          p.modalidad === m.modalidad &&
          p.fechaVigencia <= m.fechaCaducada &&
          m.fechaVigencia <= p.fechaCaducada,
      );
      if (choque) {
        superposicion = `Se superpone con el precio de ${ETIQUETA_MODALIDAD[choque.modalidad]} del ${formatoRango(choque.fechaVigencia, choque.fechaCaducada)}`;
      }
    }
    return { precio, desde, hasta, superposicion };
  });

  protected readonly valido = computed(() => Object.values(this.errores()).every((e) => e === null));

  protected estadoDe(p: PrecioRecurso): EstadoPrecio {
    const hoy = hoyIso();
    if (p.fechaCaducada < hoy) return 'VENCIDO';
    return p.fechaVigencia > hoy ? 'FUTURO' : 'VIGENTE';
  }

  protected empezarNuevo(): void {
    const t = this.temporada();
    this.errorGuardado.set('');
    this.textoPrecio.set('');
    this.modelo.set({
      ...FORMULARIO_VACIO,
      fechaVigencia: t?.inicioTemporada ?? '',
      fechaCaducada: t?.finTemporada ?? '',
    });
    this.editandoId.set(NUEVO);
  }

  protected empezarEdicion(p: PrecioRecurso): void {
    this.errorGuardado.set('');
    this.textoPrecio.set(formatoNumero(p.precio));
    this.modelo.set({
      modalidad: p.modalidad,
      precio: p.precio,
      fechaVigencia: p.fechaVigencia,
      fechaCaducada: p.fechaCaducada,
    });
    this.editandoId.set(p.publicId);
  }

  protected cancelar(): void {
    this.editandoId.set(null);
  }

  protected cambiarPrecio(evento: Event): void {
    const input = evento.target as HTMLInputElement;
    const { texto, valor } = interpretarPrecio(input.value);
    input.value = texto;
    this.textoPrecio.set(texto);
    this.modelo.update((m) => ({ ...m, precio: valor }));
  }

  protected elegirFecha(campo: 'fechaVigencia' | 'fechaCaducada', fecha: Date | null): void {
    const valor = fecha ? aIso(fecha) : '';
    this.modelo.update((m) => ({ ...m, [campo]: valor }));
  }

  protected guardar(evento: Event): void {
    evento.preventDefault();
    const id = this.editandoId();
    const m = this.modelo();
    if (!id || !this.valido() || !esNumero(m.precio) || this.guardando()) return;

    const pedido: PrecioRecursoRequest = {
      recursoPublicId: this.recurso().publicId,
      modalidad: m.modalidad,
      precio: m.precio,
      fechaVigencia: m.fechaVigencia,
      fechaCaducada: m.fechaCaducada,
    };
    const peticion =
      id === NUEVO
        ? this.recursoService.crearPrecio(pedido)
        : this.recursoService.actualizarPrecio(id, pedido);

    this.guardando.set(true);
    this.errorGuardado.set('');
    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.editandoId.set(null);
        this.snackBar.open('Precio guardado', 'Cerrar', { duration: 5000 });
        this.guardado.emit(); // el inventario vuelve a pedir los precios
      },
      error: (e: HttpErrorResponse) => {
        this.guardando.set(false);
        this.errorGuardado.set(e.error?.message ?? 'No se pudo guardar el precio. Intentá de nuevo.');
      },
    });
  }
}
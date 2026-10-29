import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { form, FormField } from '@angular/forms/signals';
import { MAT_DATE_LOCALE, provideNativeDateAdapter } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, of } from 'rxjs';
import {
  ETIQUETA_MODALIDAD,
  ETIQUETA_TIPO_RECURSO,
  Modalidad,
  PrecioModalidad,
  RecursoLoteRequest,
  Sector,
  TamanioSombrilla,
  Temporada,
  TipoRecurso,
} from '../../data-access/recurso-model';
import { RecursoService } from '../../data-access/recurso-service';

// Límites de negocio (el backend tiene que aplicar los mismos)
const MAX_POR_LOTE = 200;
const MAX_PASILLO = 20;
const MIN_CAPACIDAD = 2;
const MAX_CAPACIDAD = 8;
const MAX_NOMBRE = 25;
const MAX_NUMERO = 10_000;

// Cuánto vale cada modalidad respecto de la inmediata superior.
// MES depende de la cantidad de meses del rango de fechas (se calcula aparte).
const DIVISOR_QUINCENA = 2; // quincena = mes / 2
const DIVISOR_DIA = 15; // día = quincena / 15
const DIAS_POR_MES = 30;
const MS_POR_DIA = 86_400_000;

interface PrecioFinal {
  modalidad: Modalidad;
  precio: number;
  calculado: boolean;
}
type ClavePrecio = 'precioDia' | 'precioQuincena' | 'precioMes' | 'precioTemporada';
const esNumero = (n: unknown): n is number => typeof n === 'number' && Number.isFinite(n);
const esPrecioCargado = (n: unknown): n is number => esNumero(n) && n > 0;
const redondear = (n: number): number => Math.round(n * 100) / 100;

// Fechas: el backend usa 'YYYY-MM-DD'. Se arma con la hora local para evitar el corrimiento de zona horaria.
const aIso = (d: Date): string =>
  `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;

const desdeIso = (s: string): Date | null => {
  const [y, m, d] = s.split('-').map(Number);
  return y && m && d ? new Date(y, m - 1, d) : null;
};
// Valida un entero dentro de un rango; devuelve el mensaje de error o null si está bien
const errorEntero = (
  valor: unknown,
  min: number,
  max: number,
  mensajeFuera: string,
  mensajeVacio: string,
): string | null => {
  if (!esNumero(valor)) return mensajeVacio;
  if (!Number.isInteger(valor)) return 'Ingresá un número entero';
  return valor < min || valor > max ? mensajeFuera : null;
};

const errorPrecio = (valor: number | null): string | null =>
  esNumero(valor) && valor < 0 ? 'No puede ser negativo' : null;


@Component({
  selector: 'app-carga-masiva',
  imports: [FormField, MatDatepickerModule],
  providers: [provideNativeDateAdapter(), { provide: MAT_DATE_LOCALE, useValue: 'es-AR' }],
  templateUrl: './carga-masiva.html',
  styleUrl: './carga-masiva.css',
})
export class CargaMasiva {
  private readonly recursoService = inject(RecursoService);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly sectores = toSignal(
    this.recursoService.listarSectores().pipe(catchError(() => of([] as Sector[]))),
    { initialValue: [] as Sector[] },
  );
  private readonly temporada = toSignal(
    this.recursoService.obtenerTemporada().pipe(catchError(() => of(null as Temporada | null))),
    { initialValue: null as Temporada | null },
  );

  protected readonly tipos = Object.keys(ETIQUETA_TIPO_RECURSO) as TipoRecurso[];
  protected readonly etiquetaTipo = ETIQUETA_TIPO_RECURSO;
  protected readonly etiquetaModalidad = ETIQUETA_MODALIDAD;

  protected readonly maxPorLote = MAX_POR_LOTE;
  protected readonly maxPasillo = MAX_PASILLO;
  protected readonly minCapacidad = MIN_CAPACIDAD;
  protected readonly maxCapacidad = MAX_CAPACIDAD;
  protected readonly maxNombre = MAX_NOMBRE;

  protected readonly capacidadSombrilla = computed(() => {
  const capacidades: Record<TamanioSombrilla, string> = {
    CHICO: '2 personas',
    MEDIANO: '3–4 personas',
    GRANDE: '5–6 personas',
  };

  return capacidades[this.modelo().tamanio];
});

  // En celular el calendario se abre como ventana modal (touchUi); en pantallas grandes, como desplegable.
  protected readonly esMovil =
    typeof window !== 'undefined' && window.matchMedia('(max-width: 40rem)').matches;

  protected readonly modelo = signal({
    tipo: 'CARPA' as TipoRecurso,
    prefijoNombre: 'Carpa',
    numeroDesde: 1,
    numeroHasta: 50,
    sectorPublicId: '',
    pasillo: 1,
    capacidad: 4,
    tamanio: 'MEDIANO' as TamanioSombrilla,
    fechaVigencia: '',
    fechaCaducada: '',
    // null = vacío (se prorratea). 0 se trata igual que vacío.
    precioDia: null as number | null,
    precioQuincena: null as number | null,
    precioMes: null as number | null,
    precioTemporada: null as number | null,
  });

protected readonly f = form(this.modelo);

protected readonly camposPrecio = [
  { id: 'p-dia', modalidad: 'DIA' as Modalidad, campo: this.f.precioDia, error: 'precioDia' as const },
  { id: 'p-quincena', modalidad: 'QUINCENA' as Modalidad, campo: this.f.precioQuincena, error: 'precioQuincena' as const },
  { id: 'p-mes', modalidad: 'MES' as Modalidad, campo: this.f.precioMes, error: 'precioMes' as const },
  { id: 'p-temporada', modalidad: 'TEMPORADA' as Modalidad, campo: this.f.precioTemporada, error: 'precioTemporada' as const },
];

  protected readonly enviando = signal(false);
  // Lo que se ve escrito en cada campo de precio (con puntos de mil); el modelo guarda el número
protected readonly textosPrecio = signal<Record<ClavePrecio, string>>({
  precioDia: '',
  precioQuincena: '',
  precioMes: '',
  precioTemporada: '',
});

  // ---- Fechas (como Date, para el calendario de Material) ----
  protected readonly fechaDesde = computed(() => desdeIso(this.modelo().fechaVigencia));
  protected readonly fechaHasta = computed(() => desdeIso(this.modelo().fechaCaducada));

  // ---- Numeración ----
  protected readonly cantidad = computed(() => {
    const { numeroDesde: d, numeroHasta: h } = this.modelo();
    return esNumero(d) && esNumero(h) && h >= d ? h - d + 1 : 0;
  });

  // ---- Precios y prorrateo ----
  private readonly mesesDeTemporada = computed<number | null>(() => {
    const d = this.fechaDesde();
    const h = this.fechaHasta();
    if (!d || !h || h < d) return null;
    const dias = Math.round((h.getTime() - d.getTime()) / MS_POR_DIA) + 1;
    return Math.max(1, dias / DIAS_POR_MES);
  });

  /**
   * Precios que se van a enviar. Una modalidad vacía (o en 0) se calcula a partir de la
   * inmediata más larga que sí esté cargada:
   *   mes = temporada / meses del rango · quincena = mes / 2 · día = quincena / 15
   * Si no hay ninguna más larga cargada, esa modalidad no se ofrece.
   */
  protected readonly preciosFinales = computed<PrecioFinal[]>(() => {
    const m = this.modelo();
    const ingresado: Record<Modalidad, number | null> = {
      TEMPORADA: m.precioTemporada,
      MES: m.precioMes,
      QUINCENA: m.precioQuincena,
      DIA: m.precioDia,
    };
    const divisor: Record<Modalidad, number | null> = {
      TEMPORADA: null,
      MES: this.mesesDeTemporada(),
      QUINCENA: DIVISOR_QUINCENA,
      DIA: DIVISOR_DIA,
    };
    const orden: Modalidad[] = ['TEMPORADA', 'MES', 'QUINCENA', 'DIA'];

    const resultado: PrecioFinal[] = [];
    let anterior: number | null = null; // valor sin redondear de la modalidad superior

    for (const modalidad of orden) {
      const propio = ingresado[modalidad];
      if (esPrecioCargado(propio)) {
        resultado.push({ modalidad, precio: propio, calculado: false });
        anterior = propio;
        continue;
      }
      const d = divisor[modalidad];
      if (anterior !== null && d !== null && d > 0) {
        const calculado: number = anterior / d;
        resultado.push({ modalidad, precio: redondear(calculado), calculado: true });
        anterior = calculado;
      } else {
        anterior = null;
      }
    }
    return resultado.filter((p) => p.precio > 0);
  });

  protected readonly preciosCalculados = computed(() =>
    this.preciosFinales().filter((p) => p.calculado),
  );
  protected readonly sinPrecios = computed(() => this.preciosFinales().length === 0);
  // Hoy a las 00:00, para que el calendario no deje elegir días pasados
protected readonly hoy = (() => {
  const d = new Date();
  d.setHours(0, 0, 0, 0);
  return d;
})();

// Un solo lugar con TODAS las reglas. Cada clave es un campo: texto de error o null si está bien.
protected readonly errores = computed(() => {
  const m = this.modelo();
  const esCarpa = m.tipo === 'CARPA';
  const hoyIso = aIso(new Date());
  const textoRango = `La numeración va de 1 a ${MAX_NUMERO.toLocaleString('es-AR')}`;

  const desde = errorEntero(m.numeroDesde, 1, MAX_NUMERO, textoRango, 'Ingresá el número inicial');
  let hasta = errorEntero(m.numeroHasta, 1, MAX_NUMERO, textoRango, 'Ingresá el número final');
  if (!desde && !hasta) {
    if (m.numeroHasta < m.numeroDesde) {
      hasta = 'El número de finalización no puede ser menor que el número inicial';
    } else if (m.numeroHasta - m.numeroDesde + 1 > MAX_POR_LOTE) {
      hasta = `Máximo ${MAX_POR_LOTE} recursos por ${esCarpa ? 'pasillo' : 'sector'} en cada carga`;
    }
  }

  let nombre: string | null = null;
  if (m.prefijoNombre.trim() === '') nombre = 'El nombre es obligatorio';
  else if (m.prefijoNombre.length > MAX_NOMBRE) nombre = `Máximo ${MAX_NOMBRE} caracteres`;

  let fechaDesde: string | null = null;
  if (!m.fechaVigencia) fechaDesde = 'Elegí la fecha de inicio';
  else if (m.fechaVigencia < hoyIso) fechaDesde = 'La fecha de inicio no puede ser anterior a hoy';

  let fechaHasta: string | null = null;
  if (!m.fechaCaducada) fechaHasta = 'Elegí la fecha de fin';
  else if (m.fechaVigencia && m.fechaCaducada < m.fechaVigencia) {
    fechaHasta = 'La fecha de fin no puede ser anterior a la fecha de inicio';
  }

  return {
    sector: m.sectorPublicId ? null : 'Elegí un sector',
    nombre,
    desde,
    hasta,
    pasillo: esCarpa
      ? errorEntero(m.pasillo, 1, MAX_PASILLO, `Los pasillos van del 1 al ${MAX_PASILLO}`, 'Ingresá el pasillo')
      : null,
    capacidad: esCarpa
      ? errorEntero(m.capacidad, MIN_CAPACIDAD, MAX_CAPACIDAD, `La capacidad va de ${MIN_CAPACIDAD} a ${MAX_CAPACIDAD} personas`, 'Ingresá la capacidad')
      : null,
    fechaDesde,
    fechaHasta,
    precioDia: errorPrecio(m.precioDia),
    precioQuincena: errorPrecio(m.precioQuincena),
    precioMes: errorPrecio(m.precioMes),
    precioTemporada: errorPrecio(m.precioTemporada),
  };
});

// El botón "Crear" se habilita solo cuando no hay ningún error y hay al menos un precio
protected readonly listo = computed(
  () => Object.values(this.errores()).every((e) => e === null) && !this.sinPrecios(),
);


  private readonly tipoElegido = computed(() => this.modelo().tipo);

  constructor() {
    // Al cargar la temporada, la usamos como rango de fechas por defecto
    effect(() => {
      const t = this.temporada();
      if (t) {
        this.modelo.update((m) => ({
          ...m,
          fechaVigencia: t.inicioTemporada,
          fechaCaducada: t.finTemporada,
        }));
      }
    });
    // Al cambiar el tipo, sugerimos el prefijo del nombre ("Carpa", "Sombrilla"...)
    effect(() => {
      const tipo = this.tipoElegido();
      untracked(() =>
        this.modelo.update((m) => ({ ...m, prefijoNombre: ETIQUETA_TIPO_RECURSO[tipo] })),
      );
    });
  }

  protected formatoPrecio(n: number): string {
    return n.toLocaleString('es-AR', { style: 'currency', currency: 'ARS' });
  }

  protected cambiarPrecio(clave: ClavePrecio, evento: Event): void {
  const input = evento.target as HTMLInputElement;
  const negativo = input.value.trim().startsWith('-');

  // Nos quedamos solo con dígitos y la coma decimal
  const limpio = input.value.replace(/[^\d,]/g, '');
  const tieneComa = limpio.includes(',');
  const [enteros = '', ...resto] = limpio.split(',');
  const decimales = resto.join('').slice(0, 2);
  const sinCeros = enteros.replace(/^0+(?=\d)/, '');
  const base = sinCeros || (tieneComa ? '0' : '');

  // Texto que se ve: 1234567 -> 1.234.567
  let texto = base.replace(/\B(?=(\d{3})+(?!\d))/g, '.') + (tieneComa ? ',' + decimales : '');
  if (negativo) texto = '-' + texto;

  const hayDigitos = base !== '' || decimales !== '';
  const numero = hayDigitos ? Number(`${negativo ? '-' : ''}${base || '0'}.${decimales || '0'}`) : null;

  input.value = texto;
  this.textosPrecio.update((t) => ({ ...t, [clave]: texto }));
  this.modelo.update((m) => ({ ...m, [clave]: numero }));
}

protected elegirFecha(campo: 'fechaVigencia' | 'fechaCaducada', fecha: Date | null): void {
  const valor = fecha ? aIso(fecha) : '';
  this.modelo.update((m) =>
    campo === 'fechaVigencia' ? { ...m, fechaVigencia: valor } : { ...m, fechaCaducada: valor },
  );
  this.f[campo]().markAsTouched();
}


protected guardar(evento: Event): void {
  evento.preventDefault();
  if (!this.listo() || this.enviando()) return;

  this.enviando.set(true);
    this.recursoService.crearLote(this.armarPeticion()).subscribe({
      next: (r) => {
        this.enviando.set(false);
        this.snackBar.open(`Se crearon ${r.creados} recursos`, 'Cerrar', { duration: 5000 });
      },
      error: (e: HttpErrorResponse) => {
        this.enviando.set(false);
        this.snackBar.open(e.error?.message ?? 'No se pudo completar la carga', 'Cerrar', {
          duration: 8000,
        });
      },
    });
  }

  private armarPeticion(): RecursoLoteRequest {
    const m = this.modelo();
    return {
      tipo: m.tipo,
      prefijoNombre: m.prefijoNombre.trim(),
      numeroDesde: m.numeroDesde,
      numeroHasta: m.numeroHasta,
      sectorPublicId: m.sectorPublicId,
      ...(m.tipo === 'CARPA' && { pasillo: m.pasillo, capacidad: m.capacidad }),
      ...(m.tipo === 'SOMBRILLA' && { tamanio: m.tamanio }),
      precios: this.construirPrecios(),
    };
  }

  private construirPrecios(): PrecioModalidad[] {
    const m = this.modelo();
    return this.preciosFinales().map((p) => ({
      modalidad: p.modalidad,
      precio: p.precio,
      fechaVigencia: m.fechaVigencia,
      fechaCaducada: m.fechaCaducada,
    }));
  }
}

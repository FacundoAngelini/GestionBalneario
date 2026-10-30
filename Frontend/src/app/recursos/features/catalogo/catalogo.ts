import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MAT_DATE_LOCALE, provideNativeDateAdapter } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { catchError, of } from 'rxjs';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { ROUTE_SEGMENTS } from '../../../core/constants/route-segments.constants';
import { aIso, desdeIso, formatoFecha, hoyIso } from '../../../core/utils/fechas';
import { formatoPrecio } from '../../../core/utils/moneda';
import { SeleccionReserva } from '../../../reservas/data-access/seleccion-reserva';
import {
  ETIQUETA_MODALIDAD,
  ETIQUETA_TIPO_RECURSO,
  Modalidad,
  PrecioModalidad,
  RecursoCatalogo,
  TamanioSombrilla,
  Temporada,
  TipoRecurso,
} from '../../data-access/recurso-model';
import { RecursoService } from '../../data-access/recurso-service';

const POR_PAGINA = 24;
const ORDEN_MODALIDAD: Modalidad[] = ['DIA', 'QUINCENA', 'MES', 'TEMPORADA'];

type FiltroTipo = 'TODOS' | TipoRecurso;

const ETIQUETA_FILTRO: Record<FiltroTipo, string> = {
  TODOS: 'Todos',
  CARPA: 'Carpas',
  SOMBRILLA: 'Sombrillas',
  COCHERA: 'Cocheras',
};

const ETIQUETA_TAMANIO: Record<TamanioSombrilla, string> = {
  CHICO: 'chico',
  MEDIANO: 'mediano',
  GRANDE: 'grande',
};

interface Tarjeta {
  recurso: RecursoCatalogo;
  detalle: string;
  precios: PrecioModalidad[];
}

const detalleDe = (r: RecursoCatalogo): string => {
  switch (r.tipo) {
    case 'CARPA':
      return `Pasillo ${r.pasillo} · hasta ${r.capacidad} personas`;
    case 'SOMBRILLA':
      return r.tamanio ? `Tamaño ${ETIQUETA_TAMANIO[r.tamanio]}` : 'Sombrilla';
    case 'COCHERA':
      return 'Estacionamiento';
  }
};

const ordenarPrecios = (precios: PrecioModalidad[] | null | undefined): PrecioModalidad[] =>
  [...(precios ?? [])].sort(
    (a, b) => ORDEN_MODALIDAD.indexOf(a.modalidad) - ORDEN_MODALIDAD.indexOf(b.modalidad),
  );

@Component({
  selector: 'app-catalogo',
  imports: [MatDatepickerModule],
  providers: [provideNativeDateAdapter(), { provide: MAT_DATE_LOCALE, useValue: 'es-AR' }],
  templateUrl: './catalogo.html',
  styleUrl: './catalogo.css',
})
export class Catalogo {
  private readonly recursoService = inject(RecursoService);
  private readonly auth = inject(AuthClient);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);
  protected readonly seleccion = inject(SeleccionReserva);

  private readonly temporada = toSignal(
    this.recursoService.obtenerTemporada().pipe(catchError(() => of(null as Temporada | null))),
    { initialValue: null as Temporada | null },
  );

  protected readonly etiquetaTipo = ETIQUETA_TIPO_RECURSO;
  protected readonly etiquetaModalidad = ETIQUETA_MODALIDAD;
  protected readonly etiquetaFiltro = ETIQUETA_FILTRO;
  protected readonly filtros: FiltroTipo[] = [
    'TODOS',
    ...(Object.keys(ETIQUETA_TIPO_RECURSO) as TipoRecurso[]),
  ];
  protected readonly formatoPrecio = formatoPrecio;

  // En celular el calendario se abre como ventana modal; en pantallas grandes, como desplegable.
  protected readonly esMovil =
    typeof window !== 'undefined' && window.matchMedia('(max-width: 40rem)').matches;

  // ---- Estado de la pantalla ----
  protected readonly resultados = signal<RecursoCatalogo[] | null>(null); // null = todavía no buscó
  protected readonly cargando = signal(false);
  protected readonly errorCarga = signal('');
  protected readonly filtroTipo = signal<FiltroTipo>('TODOS');
  private readonly limite = signal(POR_PAGINA);

  // Quién mira: visitantes y clientes pueden elegir; el personal solo consulta
  protected readonly puedeElegir = computed(
    () => !this.auth.usuarioLogueado() || this.auth.esCliente(),
  );
  protected readonly rol = this.auth.etiquetaRol;

  // ---- Fechas (como Date, para el calendario de Material) ----
  private readonly hoy = (() => {
    const d = new Date();
    d.setHours(0, 0, 0, 0);
    return d;
  })();

  protected readonly fechaEntrada = computed(() => desdeIso(this.seleccion.busqueda().fechaInicio));
  protected readonly fechaSalida = computed(() => desdeIso(this.seleccion.busqueda().fechaFin));

  private readonly inicioTemporada = computed(() => {
    const t = this.temporada();
    return t ? desdeIso(t.inicioTemporada) : null;
  });
  protected readonly maxFecha = computed(() => {
    const t = this.temporada();
    return t ? desdeIso(t.finTemporada) : null;
  });
  // La entrada más temprana es hoy o, si la temporada todavía no empezó, su primer día
  protected readonly minEntrada = computed(() => {
    const inicio = this.inicioTemporada();
    return inicio && inicio > this.hoy ? inicio : this.hoy;
  });

  protected readonly temporadaTexto = computed(() => {
    const t = this.temporada();
    return t ? `Temporada: ${formatoFecha(t.inicioTemporada)} al ${formatoFecha(t.finTemporada)}` : '';
  });

  // Errores solo para fechas ya elegidas (vacías no se marcan en rojo; el botón sigue apagado)
  protected readonly errores = computed(() => {
    const { fechaInicio, fechaFin } = this.seleccion.busqueda();
    const hoy = hoyIso();
    const t = this.temporada();

    let entrada: string | null = null;
    if (fechaInicio) {
      if (fechaInicio < hoy) entrada = 'La entrada no puede ser anterior a hoy';
      else if (t && fechaInicio < t.inicioTemporada)
        entrada = `La temporada empieza el ${formatoFecha(t.inicioTemporada)}`;
      else if (t && fechaInicio > t.finTemporada)
        entrada = `La temporada termina el ${formatoFecha(t.finTemporada)}`;
    }

    let salida: string | null = null;
    if (fechaFin) {
      if (fechaInicio && fechaFin < fechaInicio)
        salida = 'La salida no puede ser anterior a la entrada';
      else if (t && fechaFin > t.finTemporada)
        salida = `La temporada termina el ${formatoFecha(t.finTemporada)}`;
    }
    return { entrada, salida };
  });

  protected readonly valido = computed(() => {
    const { fechaInicio, fechaFin } = this.seleccion.busqueda();
    return !!fechaInicio && !!fechaFin && !this.errores().entrada && !this.errores().salida;
  });

  protected readonly faltante = computed(() => {
    const { fechaInicio, fechaFin, modalidad } = this.seleccion.busqueda();
    if (!fechaInicio) return 'Elegí la fecha de entrada para ver la disponibilidad.';
    if (modalidad === 'DIA' && !fechaFin) return 'Elegí la fecha de salida para ver la disponibilidad.';
    return '';
  });

  // ---- Resultados ----
  protected readonly cantidades = computed(() => {
    const cuenta: Record<FiltroTipo, number> = { TODOS: 0, CARPA: 0, SOMBRILLA: 0, COCHERA: 0 };
    for (const r of this.resultados() ?? []) {
      cuenta.TODOS++;
      cuenta[r.tipo]++;
    }
    return cuenta;
  });

  private readonly filtrados = computed(() => {
    const lista = this.resultados() ?? [];
    const tipo = this.filtroTipo();
    return tipo === 'TODOS' ? lista : lista.filter((r) => r.tipo === tipo);
  });

  protected readonly tarjetas = computed<Tarjeta[]>(() =>
    this.filtrados()
      .slice(0, this.limite())
      .map((recurso) => ({
        recurso,
        detalle: detalleDe(recurso),
        precios: ordenarPrecios(recurso.precios),
      })),
  );
  protected readonly hayMas = computed(() => this.filtrados().length > this.limite());

  // ---- Textos de la barra de selección ----
  protected readonly periodoTexto = computed(() => {
    const p = this.seleccion.periodo();
    if (!p) return '';
    return p.fechaInicio === p.fechaFin
      ? formatoFecha(p.fechaInicio)
      : `${formatoFecha(p.fechaInicio)} al ${formatoFecha(p.fechaFin)}`;
  });

  protected readonly cantidadTexto = computed(() => {
    const n = this.seleccion.elegidos().length;
    return n === 1 ? '1 recurso elegido' : `${n} recursos elegidos`;
  });

  constructor() {
    // Si el usuario vuelve (por ejemplo, tras iniciar sesión), se repite su última búsqueda
    if (this.seleccion.periodo() && this.valido()) {
      this.buscar();
    }
  }

  protected elegirFecha(campo: 'fechaInicio' | 'fechaFin', fecha: Date | null): void {
    const valor = fecha ? aIso(fecha) : '';
    this.seleccion.busqueda.update((b) => ({ ...b, [campo]: valor }));
  }

  protected enviar(evento: Event): void {
    evento.preventDefault();
    this.buscar();
  }

  private buscar(): void {
    if (!this.valido() || this.cargando()) return;

    const periodo = this.seleccion.busqueda();
    this.cargando.set(true);
    this.errorCarga.set('');

    this.recursoService.catalogo(periodo.fechaInicio, periodo.fechaFin).subscribe({
      next: (lista) => {
        const perdidos = this.seleccion.aplicarBusqueda(periodo, lista);
        this.resultados.set(lista);
        this.filtroTipo.set('TODOS');
        this.limite.set(POR_PAGINA);
        this.cargando.set(false);
        if (perdidos > 0) {
          this.snackBar.open(
            'Algunos recursos que habías elegido ya no están disponibles para esas fechas',
            'Cerrar',
            { duration: 6000 },
          );
        }
      },
      error: (e: HttpErrorResponse) => {
        this.cargando.set(false);
        this.errorCarga.set(e.error?.message ?? 'No se pudo cargar el catálogo. Intentá de nuevo.');
      },
    });
  }

  protected elegirFiltro(filtro: FiltroTipo): void {
    this.filtroTipo.set(filtro);
    this.limite.set(POR_PAGINA);
  }

  protected verMas(): void {
    this.limite.update((l) => l + POR_PAGINA);
  }

  protected continuar(): void {
    if (!this.seleccion.puedeContinuar()) return;
    // La pantalla de confirmación pide sesión de cliente: si no hay, su guard manda al login y vuelve ahí.
    this.router.navigateByUrl(`/${ROUTE_SEGMENTS.reservas}/${ROUTE_SEGMENTS.sinPago}`);
  }
}
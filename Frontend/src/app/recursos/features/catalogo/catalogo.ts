import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { form, FormField } from '@angular/forms/signals';
import { MAT_DATE_LOCALE, provideNativeDateAdapter } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { catchError, of } from 'rxjs';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { ROUTE_SEGMENTS } from '../../../core/constants/route-segments.constants';
import { aIso, desdeIso, formatoFecha, formatoRango, hoyIso } from '../../../core/utils/fechas';
import { formatoPrecio } from '../../../core/utils/moneda';
import {
  diasEntre,
  fechaFinEfectiva,
  precioVigente,
} from '../../../reservas/data-access/periodo-reserva';
import { Periodo, SeleccionReserva } from '../../../reservas/data-access/seleccion-reserva';
import {
  ETIQUETA_MODALIDAD,
  ETIQUETA_TIPO_RECURSO,
  Modalidad,
  RecursoCatalogo,
  TamanioSombrilla,
  Temporada,
  TipoRecurso,
} from '../../data-access/recurso-model';
import { RecursoService } from '../../data-access/recurso-service';

const POR_PAGINA = 24;
const MODALIDADES: Modalidad[] = ['DIA', 'QUINCENA', 'MES', 'TEMPORADA'];

const AYUDA_MODALIDAD: Record<Modalidad, string> = {
  DIA: 'Elegís la entrada y la salida.',
  QUINCENA: 'La reserva dura 15 días desde la entrada.',
  MES: 'La reserva dura un mes calendario desde la entrada.',
  TEMPORADA: 'La reserva dura hasta el fin de la temporada.',
};

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
  total: number;
  detallePrecio: string;
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

const textoDias = (dias: number): string => `${dias} ${dias === 1 ? 'día' : 'días'}`;

@Component({
  selector: 'app-catalogo',
  imports: [FormField, MatDatepickerModule],
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
  protected readonly modalidades = MODALIDADES;
  protected readonly filtros: FiltroTipo[] = [
    'TODOS',
    ...(Object.keys(ETIQUETA_TIPO_RECURSO) as TipoRecurso[]),
  ];
  protected readonly formatoPrecio = formatoPrecio;

  // En pantallas chicas, el calendario se muestra en un modal.
  protected readonly esMovil =
    typeof window !== 'undefined' && window.matchMedia('(max-width: 40rem)').matches;

  // La búsqueda se mantiene al volver del login.
  protected readonly f = form(this.seleccion.busqueda);

  protected readonly resultados = signal<RecursoCatalogo[] | null>(null); // null mientras no se haya buscado
  protected readonly sinTarifa = signal(0);
  protected readonly cargando = signal(false);
  protected readonly errorCarga = signal('');
  protected readonly filtroTipo = signal<FiltroTipo>('TODOS');
  private readonly limite = signal(POR_PAGINA);
  private readonly busquedaPendiente = signal(this.seleccion.periodo() !== null);

  // Los visitantes y clientes pueden elegir recursos; el personal solo puede verlos.
  protected readonly puedeElegir = computed(
    () => !this.auth.usuarioLogueado() || this.auth.esCliente(),
  );
  protected readonly rol = this.auth.etiquetaRol;

  private readonly hoy = (() => {
    const d = new Date();
    d.setHours(0, 0, 0, 0);
    return d;
  })();

  protected readonly fechaEntrada = computed(() => desdeIso(this.seleccion.busqueda().fechaInicio));
  protected readonly fechaSalida = computed(() => desdeIso(this.seleccion.busqueda().fechaFin));
  protected readonly modalidadDia = computed(() => this.seleccion.busqueda().modalidad === 'DIA');
  protected readonly ayudaModalidad = computed(
    () => AYUDA_MODALIDAD[this.seleccion.busqueda().modalidad],
  );

  protected readonly maxFecha = computed(() => {
    const t = this.temporada();
    return t ? desdeIso(t.finTemporada) : null;
  });
  // La fecha de entrada no puede ser anterior al inicio de la temporada ni a hoy.
  protected readonly minEntrada = computed(() => {
    const t = this.temporada();
    const inicio = t ? desdeIso(t.inicioTemporada) : null;
    return inicio && inicio > this.hoy ? inicio : this.hoy;
  });

  protected readonly temporadaTexto = computed(() => {
    const t = this.temporada();
    return t ? `Temporada: ${formatoFecha(t.inicioTemporada)} al ${formatoFecha(t.finTemporada)}` : '';
  });

  /** Devuelve la fecha de finalización según la modalidad seleccionada. */
  private readonly finEfectiva = computed<string | null>(() => {
    const b = this.seleccion.busqueda();
    if (!b.fechaInicio) return null;
    const fin = fechaFinEfectiva(
      b.modalidad,
      b.fechaInicio,
      b.fechaFin,
      this.temporada()?.finTemporada ?? null,
    );
    return fin || null;
  });

  // Valida los campos que ya tienen una fecha.
  protected readonly errores = computed(() => {
    const { fechaInicio, fechaFin, modalidad } = this.seleccion.busqueda();
    const hoy = hoyIso();
    const t = this.temporada();
    const fin = this.finEfectiva();

    let entrada: string | null = null;
    if (fechaInicio) {
      if (fechaInicio < hoy) entrada = 'La entrada no puede ser anterior a hoy';
      else if (t && fechaInicio < t.inicioTemporada)
        entrada = `La temporada empieza el ${formatoFecha(t.inicioTemporada)}`;
      else if (t && fechaInicio > t.finTemporada)
        entrada = `La temporada termina el ${formatoFecha(t.finTemporada)}`;
      else if (modalidad === 'TEMPORADA' && !t)
        entrada = 'No se pudo cargar el fin de la temporada. Recargá la página.';
      else if (modalidad !== 'DIA' && t && fin && fin > t.finTemporada)
        entrada = `Con esta modalidad la reserva terminaría el ${formatoFecha(fin)}, después del fin de la temporada (${formatoFecha(t.finTemporada)}). Elegí una entrada más temprana u otra modalidad.`;
    }

    let salida: string | null = null;
    if (modalidad === 'DIA' && fechaFin) {
      if (fechaInicio && fechaFin < fechaInicio) salida = 'La salida no puede ser anterior a la entrada';
      else if (t && fechaFin > t.finTemporada)
        salida = `La temporada termina el ${formatoFecha(t.finTemporada)}`;
    }
    return { entrada, salida };
  });

  protected readonly valido = computed(
    () => this.finEfectiva() !== null && !this.errores().entrada && !this.errores().salida,
  );

  // Resumen del período seleccionado.
  protected readonly periodoPrevio = computed(() => {
    const fin = this.finEfectiva();
    const inicio = this.seleccion.busqueda().fechaInicio;
    if (!this.valido() || !fin) return '';
    return `${formatoRango(inicio, fin)} (${textoDias(diasEntre(inicio, fin))})`;
  });

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

  protected readonly tarjetas = computed<Tarjeta[]>(() => {
    const p = this.seleccion.periodo();
    if (!p) return [];
    const dias = this.seleccion.dias();
    return this.filtrados()
      .slice(0, this.limite())
      .map((recurso) => {
        const precio = precioVigente(recurso, p.modalidad, p.fechaInicio) ?? 0;
        const total = p.modalidad === 'DIA' ? precio * dias : precio;
        const detallePrecio =
          p.modalidad === 'DIA'
            ? `${formatoPrecio(precio)} por día × ${textoDias(dias)}`
            : ETIQUETA_MODALIDAD[p.modalidad];
        return { recurso, detalle: detalleDe(recurso), total, detallePrecio };
      });
  });
  protected readonly hayMas = computed(() => this.filtrados().length > this.limite());

  protected readonly periodoTexto = computed(() => {
    const p = this.seleccion.periodo();
    if (!p) return '';
    return `${ETIQUETA_MODALIDAD[p.modalidad]} · ${formatoRango(p.fechaInicio, p.fechaFin)} (${textoDias(this.seleccion.dias())})`;
  });

  protected readonly cantidadTexto = computed(() => {
    const n = this.seleccion.elegidos().length;
    return n === 1 ? '1 recurso elegido' : `${n} recursos elegidos`;
  });

  constructor() {
    // Repite la búsqueda si ya había un período seleccionado.

    effect(() => {
      if (this.busquedaPendiente() && this.valido()) {
        untracked(() => {
          this.busquedaPendiente.set(false);
          this.buscar();
        });
      }
    });
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
    const b = this.seleccion.busqueda();
    const fin = this.finEfectiva();
    if (!this.valido() || !fin || this.cargando()) return;

    const periodo: Periodo = { fechaInicio: b.fechaInicio, fechaFin: fin, modalidad: b.modalidad };
    this.cargando.set(true);
    this.errorCarga.set('');

    this.recursoService.catalogo(periodo.fechaInicio, periodo.fechaFin).subscribe({
      next: (lista) => {
        // Muestra únicamente los recursos con una tarifa vigente.
        const conTarifa = lista.filter(
          (r) => precioVigente(r, periodo.modalidad, periodo.fechaInicio) !== null,
        );
        const perdidos = this.seleccion.aplicarBusqueda(periodo, conTarifa);
        this.sinTarifa.set(lista.length - conTarifa.length);
        this.resultados.set(conTarifa);
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
    // Para continuar, el usuario debe iniciar sesión con una cuenta de cliente.
    this.router.navigateByUrl(`/${ROUTE_SEGMENTS.reservas}/${ROUTE_SEGMENTS.sinPago}`);
  }
}
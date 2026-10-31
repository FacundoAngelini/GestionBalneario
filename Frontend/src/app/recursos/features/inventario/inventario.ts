import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { form, FormField } from '@angular/forms/signals';
import { PreciosRecurso } from './precios-recurso/precios-recurso';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RouterLink } from '@angular/router';
import { catchError, of } from 'rxjs';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { errorEntero } from '../../../core/utils/validaciones';
import {
  MAX_CAPACIDAD,
  MAX_NOMBRE_RECURSO,
  MAX_NUMERO,
  MAX_PASILLO,
  MIN_CAPACIDAD,
} from '../../data-access/limites-recursos';
import {
  DatosRecurso,
  ETIQUETA_TAMANIO,
  ETIQUETA_TIPO_PLURAL,
  PrecioRecurso,
  RecursoInventario,
  Sector,
  Temporada,
  TipoRecurso,
} from '../../data-access/recurso-model';
import { RecursoService } from '../../data-access/recurso-service';

const POR_PAGINA = 30;
const TIPOS: TipoRecurso[] = ['CARPA', 'SOMBRILLA', 'COCHERA'];

type FiltroEstado = 'TODOS' | 'ACTIVOS' | 'DESACTIVADOS';

const EDICION_VACIA: DatosRecurso = {
  nombre: '',
  esReservable: true,
  sectorPublicId: '',
  numero: 1,
  pasillo: 1,
  capacidad: MIN_CAPACIDAD,
  tamanio: 'MEDIANO',
};

@Component({
  selector: 'app-inventario',
  imports: [FormField, RouterLink,PreciosRecurso],
  templateUrl: './inventario.html',
  styleUrl: './inventario.css',
})
export class Inventario {
  private readonly recursoService = inject(RecursoService);
  private readonly auth = inject(AuthClient);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly esAdmin = this.auth.esAdmin;
  protected readonly tipos = TIPOS;
  protected readonly etiquetaPlural = ETIQUETA_TIPO_PLURAL;
  protected readonly etiquetaTamanio = ETIQUETA_TAMANIO;
  protected readonly maxNombre = MAX_NOMBRE_RECURSO;
  protected readonly maxPasillo = MAX_PASILLO;
  protected readonly minCapacidad = MIN_CAPACIDAD;
  protected readonly maxCapacidad = MAX_CAPACIDAD;

  protected readonly sectores = toSignal(
    this.recursoService.listarSectores().pipe(catchError(() => of([] as Sector[]))),
    { initialValue: [] as Sector[] },
  );
  private readonly nombresSector = computed(
    () => new Map(this.sectores().map((s) => [s.publicId, s.nombre])),
  );

  // ---- Estado de la pantalla ----
  protected readonly tipoActivo = signal<TipoRecurso>('CARPA');
  // Cada tipo se pide la primera vez que se abre su pestaña (null = todavía no se pidió)
  private readonly datos = signal<Record<TipoRecurso, RecursoInventario[] | null>>({
    CARPA: null,
    SOMBRILLA: null,
    COCHERA: null,
  });
  protected readonly cargando = signal(false);
  protected readonly errorCarga = signal('');
  private readonly limite = signal(POR_PAGINA);

  protected readonly filtros = signal({ texto: '', sector: '', estado: 'TODOS' as FiltroEstado });
  protected readonly f = form(this.filtros);

  private readonly filtrados = computed(() => {
    const lista = this.datos()[this.tipoActivo()] ?? [];
    const { texto, sector, estado } = this.filtros();
    const buscado = texto.trim().toLowerCase();
    return lista
      .filter((r) => !sector || r.sectorPublicId === sector)
      .filter((r) => estado === 'TODOS' || (estado === 'ACTIVOS' ? r.esReservable : !r.esReservable))
      .filter((r) => !buscado || `${r.nombre} ${r.numero}`.toLowerCase().includes(buscado))
      .sort((a, b) => a.numero - b.numero);
  });
  protected readonly visibles = computed(() => this.filtrados().slice(0, this.limite()));
  protected readonly hayMas = computed(() => this.filtrados().length > this.limite());
  protected readonly cargado = computed(() => this.datos()[this.tipoActivo()] !== null);

  // ---- Edición (solo administrador) ----
  protected readonly editandoId = signal<string | null>(null);
  protected readonly edicion = signal<DatosRecurso>({ ...EDICION_VACIA });
  protected readonly fe = form(this.edicion);
  protected readonly guardando = signal(false);
  protected readonly errorEdicion = signal('');

  protected readonly errores = computed(() => {
    const d = this.edicion();
    const esCarpa = this.tipoActivo() === 'CARPA';

    let nombre: string | null = null;
    if (d.nombre.trim() === '') nombre = 'El nombre es obligatorio';
    else if (d.nombre.length > MAX_NOMBRE_RECURSO) nombre = `Máximo ${MAX_NOMBRE_RECURSO} caracteres`;

    return {
      nombre,
      sector: d.sectorPublicId ? null : 'Elegí un sector',
      numero: errorEntero(
        d.numero,
        1,
        MAX_NUMERO,
        `La numeración va de 1 a ${MAX_NUMERO.toLocaleString('es-AR')}`,
        'Ingresá el número',
      ),
      pasillo: esCarpa
        ? errorEntero(d.pasillo, 1, MAX_PASILLO, `Los pasillos van del 1 al ${MAX_PASILLO}`, 'Ingresá el pasillo')
        : null,
      capacidad: esCarpa
        ? errorEntero(
            d.capacidad,
            MIN_CAPACIDAD,
            MAX_CAPACIDAD,
            `La capacidad va de ${MIN_CAPACIDAD} a ${MAX_CAPACIDAD} personas`,
            'Ingresá la capacidad',
          )
        : null,
    };
  });
  protected readonly edicionValida = computed(() =>
    Object.values(this.errores()).every((e) => e === null),
  );

  constructor() {
    // Al cambiar de pestaña o de filtros, se vuelve a la primera página
    effect(() => {
      this.tipoActivo();
      this.filtros();
      untracked(() => this.limite.set(POR_PAGINA));
    });
    this.cargar('CARPA');
    this.cargarPrecios();
  }

  protected cantidad(tipo: TipoRecurso): number | null {
    return this.datos()[tipo]?.length ?? null;
  }

  protected nombreSector(publicId: string): string {
    return this.nombresSector().get(publicId) ?? '—';
  }

    protected preciosDe(publicId: string): PrecioRecurso[] {
    return this.preciosPorRecurso().get(publicId) ?? [];
  }

  protected cargarPrecios(): void {
    this.errorPrecios.set('');
    this.recursoService.listarPrecios().subscribe({
      next: (lista) => this.precios.set(lista),
      error: (e: HttpErrorResponse) => {
        this.errorPrecios.set(
          e.status === 403
            ? 'No tenés permiso para ver los precios.'
            : (e.error?.message ?? 'No se pudieron cargar los precios.'),
        );
      },
    });
  }

    // ---- Precios: se piden una sola vez para todos los recursos y se agrupan por recurso ----
  protected readonly temporada = toSignal(
    this.recursoService.obtenerTemporada().pipe(catchError(() => of(null as Temporada | null))),
    { initialValue: null as Temporada | null },
  );
  private readonly precios = signal<PrecioRecurso[] | null>(null);
  protected readonly preciosCargados = computed(() => this.precios() !== null);
  protected readonly errorPrecios = signal('');

  private readonly preciosPorRecurso = computed(() => {
    const mapa = new Map<string, PrecioRecurso[]>();
    for (const p of this.precios() ?? []) {
      const lista = mapa.get(p.recursoPublicId);
      if (lista) lista.push(p);
      else mapa.set(p.recursoPublicId, [p]);
    }
    return mapa;
  });

  protected elegirTipo(tipo: TipoRecurso): void {
    this.editandoId.set(null);
    this.tipoActivo.set(tipo);
    this.cargar(tipo);
  }

  private cargar(tipo: TipoRecurso, forzar = false): void {
    if (!forzar && this.datos()[tipo] !== null) return;
    this.cargando.set(true);
    this.errorCarga.set('');
    this.recursoService.inventario(tipo).subscribe({
      next: (lista) => {
        this.datos.update((d) => ({ ...d, [tipo]: lista }));
        this.cargando.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.cargando.set(false);
        this.errorCarga.set(this.mensajeDeError(e));
      },
    });
  }

  protected reintentar(): void {
    this.cargar(this.tipoActivo(), true);
  }

  protected verMas(): void {
    this.limite.update((l) => l + POR_PAGINA);
  }

  // ---- Edición ----
  protected empezarEdicion(r: RecursoInventario): void {
    this.errorEdicion.set('');
    this.edicion.set({
      nombre: r.nombre,
      esReservable: r.esReservable,
      sectorPublicId: r.sectorPublicId,
      numero: r.numero,
      pasillo: r.pasillo ?? EDICION_VACIA.pasillo,
      capacidad: r.capacidad ?? EDICION_VACIA.capacidad,
      tamanio: r.tamanio ?? EDICION_VACIA.tamanio,
    });
    this.editandoId.set(r.publicId);
  }

  protected cancelarEdicion(): void {
    this.editandoId.set(null);
  }

  protected guardar(evento: Event): void {
    evento.preventDefault();
    const id = this.editandoId();
    if (!id || !this.edicionValida() || this.guardando()) return;

    const tipo = this.tipoActivo();
    this.guardando.set(true);
    this.errorEdicion.set('');

    this.recursoService.actualizar(tipo, id, this.edicion()).subscribe({
      next: () => {
        this.guardando.set(false);
        this.editandoId.set(null);
        this.snackBar.open('Cambios guardados', 'Cerrar', { duration: 5000 });
        this.cargar(tipo, true);
      },
      error: (e: HttpErrorResponse) => {
        this.guardando.set(false);
        this.errorEdicion.set(
          e.error?.message ?? 'No se pudieron guardar los cambios. Intentá de nuevo.',
        );
      },
    });
  }

  private mensajeDeError(e: HttpErrorResponse): string {
    if (e.status === 0) return 'No se pudo conectar con el servidor';
    if (e.status === 403) return 'No tenés permiso para ver este inventario. Cerrá sesión y volvé a ingresar.';
    return e.error?.message ?? 'No se pudo cargar el inventario. Intentá de nuevo.';
  }
}
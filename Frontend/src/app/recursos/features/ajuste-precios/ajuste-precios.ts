import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { form, FormField } from '@angular/forms/signals';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RouterLink } from '@angular/router';
import { catchError, of } from 'rxjs';
import { formatoPrecio, interpretarPrecio } from '../../../core/utils/moneda';
import { esNumero } from '../../../core/utils/validaciones';
import {
  AjustePreciosRequest,
  ETIQUETA_MODALIDAD,
  ETIQUETA_TIPO_PLURAL,
  Modalidad,
  OperacionAjuste,
  ResultadoAjuste,
  Sector,
  TipoRecurso,
} from '../../data-access/recurso-model';
import { RecursoService } from '../../data-access/recurso-service';
import { MAX_PORCENTAJE, MIN_PORCENTAJE } from '../../../core/constants/route-segments.constants';

const TIPOS: TipoRecurso[] = ['CARPA', 'SOMBRILLA', 'COCHERA'];
const MODALIDADES: Modalidad[] = ['DIA', 'QUINCENA', 'MES', 'TEMPORADA'];

@Component({
  selector: 'app-ajuste-precios',
  imports: [FormField, RouterLink],
  templateUrl: './ajuste-precios.html',
  styleUrl: './ajuste-precios.css',
})
export class AjustePrecios {
  private readonly recursoService = inject(RecursoService);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly tipos = TIPOS;
  protected readonly modalidades = MODALIDADES;
  protected readonly etiquetaPlural = ETIQUETA_TIPO_PLURAL;
  protected readonly etiquetaModalidad = ETIQUETA_MODALIDAD;
  protected readonly formatoPrecio = formatoPrecio;
  protected readonly minPorcentaje = MIN_PORCENTAJE;
  protected readonly maxPorcentaje = MAX_PORCENTAJE;

  protected readonly sectores = toSignal(
    this.recursoService.listarSectores().pipe(catchError(() => of([] as Sector[]))),
    { initialValue: [] as Sector[] },
  );

  protected readonly modelo = signal({
    tipo: 'CARPA' as TipoRecurso,
    sectorPublicId: '',
    modalidad: '' as '' | Modalidad, // '' = todas
    operacion: 'PORCENTAJE' as OperacionAjuste,
    valor: null as number | null,
  });
  protected readonly f = form(this.modelo);
  protected readonly textoValor = signal(''); // lo que se ve en el campo (con puntos de mil)

  protected readonly procesando = signal(false);
  protected readonly error = signal('');
  protected readonly confirmando = signal(false);
  protected readonly aplicado = signal<ResultadoAjuste | null>(null);

  protected readonly esPorcentaje = computed(() => this.modelo().operacion === 'PORCENTAJE');

  protected readonly errores = computed(() => {
    const m = this.modelo();

    let valor: string | null = null;
    if (!esNumero(m.valor)) valor = 'Ingresá el valor';
    else if (m.operacion === 'PORCENTAJE') {
      if (m.valor === 0) valor = 'El porcentaje no puede ser 0';
      else if (m.valor < MIN_PORCENTAJE || m.valor > MAX_PORCENTAJE) {
        valor = `El porcentaje va de ${MIN_PORCENTAJE} a ${MAX_PORCENTAJE}`;
      }
    } else if (m.valor <= 0) valor = 'Tiene que ser mayor a 0';

    const modalidad =
      m.operacion === 'MONTO_FIJO' && !m.modalidad ? 'Para fijar un monto elegí una modalidad' : null;
    return { valor, modalidad };
  });
  protected readonly valido = computed(() => Object.values(this.errores()).every((e) => e === null));

  // Lo que se enviaría ahora (null si falta el valor)
  private readonly pedido = computed<AjustePreciosRequest | null>(() => {
    const m = this.modelo();
    if (!esNumero(m.valor)) return null;
    return {
      tipo: m.tipo,
      sectorPublicId: m.sectorPublicId || null,
      modalidad: m.modalidad || null,
      operacion: m.operacion,
      valor: m.valor,
      soloSimular: true,
    };
  });
  private readonly claveActual = computed(() => JSON.stringify(this.pedido()));

  // La vista previa solo vale si el formulario sigue igual que cuando se calculó
  private readonly vistaPrevia = signal<{ clave: string; resultado: ResultadoAjuste } | null>(null);
  protected readonly previaVigente = computed(() => {
    const v = this.vistaPrevia();
    return v && v.clave === this.claveActual() ? v.resultado : null;
  });

  // Al cambiar la operación el valor cambia de significado (% o $): se vacía el campo
  private readonly operacion = computed(() => this.modelo().operacion);

  constructor() {
    effect(() => {
      this.operacion();
      untracked(() => {
        this.textoValor.set('');
        this.modelo.update((m) => ({ ...m, valor: null }));
      });
    });
  }

  protected cambiarValor(evento: Event): void {
    const input = evento.target as HTMLInputElement;
    const { texto, valor } = interpretarPrecio(input.value);
    input.value = texto;
    this.textoValor.set(texto);
    this.modelo.update((m) => ({ ...m, valor }));
    this.aplicado.set(null);
  }

  protected previsualizar(evento: Event): void {
    evento.preventDefault();
    const pedido = this.pedido();
    if (!pedido || !this.valido() || this.procesando()) return;

    this.procesando.set(true);
    this.error.set('');
    this.aplicado.set(null);
    this.confirmando.set(false);

    this.recursoService.ajustarPrecios({ ...pedido, soloSimular: true }).subscribe({
      next: (resultado) => {
        this.vistaPrevia.set({ clave: JSON.stringify(pedido), resultado });
        this.procesando.set(false);
      },
      error: (e: HttpErrorResponse) => this.fallar(e),
    });
  }

  protected pedirConfirmacion(): void {
    this.confirmando.set(true);
  }

  protected descartarConfirmacion(): void {
    this.confirmando.set(false);
  }

  protected aplicar(): void {
    const pedido = this.pedido();
    if (!pedido || !this.previaVigente() || this.procesando()) return;

    this.procesando.set(true);
    this.error.set('');

    this.recursoService.ajustarPrecios({ ...pedido, soloSimular: false }).subscribe({
      next: (resultado) => {
        this.procesando.set(false);
        this.confirmando.set(false);
        this.vistaPrevia.set(null);
        this.aplicado.set(resultado);
        this.snackBar.open('Precios actualizados', 'Cerrar', { duration: 5000 });
      },
      error: (e: HttpErrorResponse) => this.fallar(e),
    });
  }

  private fallar(e: HttpErrorResponse): void {
    this.procesando.set(false);
    this.confirmando.set(false);
    if (e.status === 0) this.error.set('No se pudo conectar con el servidor');
    else if (e.status === 403) this.error.set('Solo el administrador puede actualizar precios.');
    else this.error.set(e.error?.message ?? 'No se pudo completar la operación. Intentá de nuevo.');
  }
}
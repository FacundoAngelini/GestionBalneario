import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { form, FormField } from '@angular/forms/signals';
import { RouterLink } from '@angular/router';
import { catchError, debounceTime, of, switchMap, tap } from 'rxjs';
import { mensajeAmigable } from '../../../core/utils/errores';
import { formatoFecha } from '../../../core/utils/fechas';
import { Cliente, MAX_RESULTADOS_CLIENTES } from '../../data-access/cliente-model';
import { ClienteService } from '../../data-access/cliente-service';

type FiltroEstado = 'ACTIVOS' | 'BAJA' | 'TODOS';

@Component({
  selector: 'app-clientes-lista',
  imports: [FormField, RouterLink],
  templateUrl: './cliente-lista.html',
  styleUrl: './cliente-lista.css',
})
export class ClientesLista {
  private readonly clienteService = inject(ClienteService);

  protected readonly maxResultados = MAX_RESULTADOS_CLIENTES;
  protected readonly formatoFecha = formatoFecha;

  protected readonly clientes = signal<Cliente[] | null>(null); // null = todavía no cargó
  protected readonly cargando = signal(false);
  protected readonly errorCarga = signal('');

  // Los activos son lo habitual: se ven primero
  protected readonly filtros = signal({ texto: '', estado: 'ACTIVOS' as FiltroEstado });
  protected readonly f = form(this.filtros);

  protected readonly hayMas = computed(() => (this.clientes()?.length ?? 0) >= MAX_RESULTADOS_CLIENTES);

  constructor() {
    // Cada cambio de filtro busca solo, esperando 300 ms a que termine de escribir
    toObservable(this.filtros)
      .pipe(
        debounceTime(300),
        tap(() => {
          this.cargando.set(true);
          this.errorCarga.set('');
        }),
        switchMap(({ texto, estado }) =>
          this.clienteService
            .listar(texto, estado === 'ACTIVOS' ? true : estado === 'BAJA' ? false : undefined)
            .pipe(
              catchError((e: HttpErrorResponse) => {
                this.errorCarga.set(
                  e.status === 403
                    ? 'No tenés permiso para ver los clientes.'
                    : mensajeAmigable(e, 'No se pudo cargar la lista de clientes. Intentá de nuevo.'),
                );
                return of(null);
              }),
            ),
        ),
        takeUntilDestroyed(),
      )
      .subscribe((lista) => {
        if (lista) this.clientes.set(lista);
        this.cargando.set(false);
      });
  }

  protected formatoDni(dni: number): string {
    return dni.toLocaleString('es-AR');
  }
}
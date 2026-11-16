import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { ETIQUETA_CATEGORIA, formatoPrecio } from '../../data-access/producto-etiquetas';
import { CategoriaProducto } from '../../data-access/producto-model';
import { ProductoService } from '../../data-access/producto-service';

@Component({
  imports: [RouterLink],
  selector: 'app-menu',
  styleUrl: './menu.css',
  templateUrl: './menu.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Menu {
  private readonly auth = inject(AuthClient);
  private readonly productoService = inject(ProductoService);

  protected readonly etiquetaCategoria = ETIQUETA_CATEGORIA;
  protected readonly formatoPrecio = formatoPrecio;
  protected readonly esAdmin = this.auth.esAdmin;
  protected readonly categoriaActiva = signal<CategoriaProducto | null>(null);

  protected readonly productos = rxResource({
    stream: () =>
      this.auth.esAdmin()
        ? this.productoService.listar()
        : this.productoService.disponibles(),
  });

  protected readonly categorias = computed(() => {
    const presentes = new Set((this.productos.value() ?? []).map((p) => p.categoria));
    return (Object.keys(ETIQUETA_CATEGORIA) as CategoriaProducto[]).filter((c) =>
      presentes.has(c),
    );
  });

  protected readonly visibles = computed(() => {
    const categoria = this.categoriaActiva();
    const todos = this.productos.value() ?? [];
    const disponiblesParaUsuario = this.auth.esAdmin()
      ? todos
      : todos.filter((p) => p.productoDisponible);
    return categoria
      ? disponiblesParaUsuario.filter((p) => p.categoria === categoria)
      : disponiblesParaUsuario;
  });
}

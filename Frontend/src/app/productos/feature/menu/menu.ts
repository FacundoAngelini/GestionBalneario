
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { ROLES } from '../../../auth/data-access/roles.constants';
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

  protected readonly puedeVerTodos = computed(() => {
    const roles = this.auth.roles();

    return roles.includes(ROLES.ADMIN) || roles.includes(ROLES.GERENTE);
  });

  protected readonly productos = rxResource({
    stream: () =>
      this.puedeVerTodos()
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

    const productosVisibles = this.puedeVerTodos()
      ? todos
      : todos.filter((p) => p.productoDisponible);

    return categoria
      ? productosVisibles.filter((p) => p.categoria === categoria)
      : productosVisibles;
  });
}

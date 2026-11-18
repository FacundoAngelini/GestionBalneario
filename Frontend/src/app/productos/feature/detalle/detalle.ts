
import { rxResource } from '@angular/core/rxjs-interop';
import {
  FormField,
  form,
  maxLength,
  min,
  minLength,
  required,
  submit,
  validate,
} from '@angular/forms/signals';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { firstValueFrom, map } from 'rxjs';
import { AuthClient } from '../../../auth/data-access/auth-client';
import {
  ETIQUETA_CATEGORIA,
  formatoPrecio,
} from '../../data-access/producto-etiquetas';
import {
  CategoriaProducto,
  ProductoRequest,
} from '../../data-access/producto-model';
import { ProductoService } from '../../data-access/producto-service';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { ROLES } from '../../../auth/data-access/roles.constants';

interface ProductoFormModel {
  nombre: string;
  precio: number | null;
  categoria: CategoriaProducto | '';
  productoDisponible: boolean;
}

const CATEGORIAS: { valor: CategoriaProducto; etiqueta: string }[] = [
  { valor: 'DESAYUNO', etiqueta: 'Desayuno' },
  { valor: 'ALMUERZO', etiqueta: 'Almuerzo' },
  { valor: 'MERIENDA', etiqueta: 'Merienda' },
  { valor: 'CENA', etiqueta: 'Cena' },
  { valor: 'COMIDA_CALIENTE', etiqueta: 'Comida caliente' },
  { valor: 'COMIDA_FRIA', etiqueta: 'Comida fría' },
  { valor: 'ENSALADAS', etiqueta: 'Ensaladas' },
  { valor: 'GUARNICION', etiqueta: 'Guarnición' },
  { valor: 'POSTRES', etiqueta: 'Postres' },
  { valor: 'BEBIDAS_FRIAS', etiqueta: 'Bebidas frías' },
  { valor: 'BEBIDAS_CALIENTES', etiqueta: 'Bebidas calientes' },
  { valor: 'TRAGOS', etiqueta: 'Tragos' },
];

const nuevoProducto = (): ProductoFormModel => ({
  nombre: '',
  precio: null,
  categoria: '',
  productoDisponible: true,
});

@Component({
  imports: [RouterLink, FormField],
  selector: 'app-detalle',
  styleUrl: './detalle.css',
  templateUrl: './detalle.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Detalle {
  private readonly auth = inject(AuthClient);
  private readonly productoService = inject(ProductoService);
  private readonly publicId =
    inject(ActivatedRoute).snapshot.paramMap.get('id') ?? '';

  protected readonly etiquetaCategoria = ETIQUETA_CATEGORIA;
  protected readonly formatoPrecio = formatoPrecio;
  protected readonly puedeGestionar = computed(() => {
  const roles = this.auth.roles();

  return roles.includes(ROLES.ADMIN) || roles.includes(ROLES.GERENTE);
});
  protected readonly categorias = CATEGORIAS;

  protected readonly actualizandoDisponibilidad = signal(false);
  protected readonly errorDisponibilidad = signal<string | null>(null);

  protected readonly editando = signal(false);
  protected readonly guardandoEdicion = signal(false);
  protected readonly errorEdicion = signal<string | null>(null);
  protected readonly exitoEdicion = signal<string | null>(null);

  private readonly modelo = signal<ProductoFormModel>(nuevoProducto());

  protected readonly productoForm = form(this.modelo, (p) => {
    required(p.nombre, { message: 'El nombre es obligatorio.' });
    minLength(p.nombre, 2, {
      message: 'El nombre debe tener al menos 2 caracteres.',
    });
    maxLength(p.nombre, 100, {
      message: 'El nombre no puede superar los 100 caracteres.',
    });
    validate(p.nombre, ({ value }) =>
      value().length > 0 && value().trim().length === 0
        ? { kind: 'blank', message: 'El nombre no puede estar vacío.' }
        : undefined,
    );

    required(p.precio, { message: 'El precio es obligatorio.' });
    min(p.precio, 0.01, {
      message: 'El precio debe ser mayor a 0.',
    });

    required(p.categoria, {
      message: 'Seleccioná una categoría.',
    });
  });

  protected readonly producto = rxResource({
    stream: () =>
      this.puedeGestionar()
        ? this.productoService.obtener(this.publicId)
        : this.productoService.disponibles().pipe(
            map((productos) => {
              const producto = productos.find(
                (item) => item.publicId === this.publicId,
              );
              if (!producto) {
                throw new Error('El producto no está disponible.');
              }
              return producto;
            }),
          ),
  });

  protected editar(): void {
    const producto = this.producto.value();
    if (!this.puedeGestionar() || !producto) return;

    this.modelo.set({
      nombre: producto.nombre,
      precio: producto.precio,
      categoria: producto.categoria,
      productoDisponible: producto.productoDisponible,
    });

    this.errorEdicion.set(null);
    this.exitoEdicion.set(null);
    this.editando.set(true);
  }

  protected cancelarEdicion(): void {
    this.editando.set(false);
    this.errorEdicion.set(null);
    this.modelo.set(nuevoProducto());
  }

  protected guardarEdicion(event: Event): void {
    event.preventDefault();
    this.errorEdicion.set(null);
    this.exitoEdicion.set(null);

    void submit(this.productoForm, async () => {
      if (!this.puedeGestionar()) return;

      const { nombre, precio, categoria, productoDisponible } =
        this.modelo();

      const request: ProductoRequest = {
        nombre: nombre.trim(),
        precio: precio as number,
        categoria: categoria as CategoriaProducto,
        productoDisponible,
      };

      this.guardandoEdicion.set(true);

      try {
        const actualizado = await firstValueFrom(
          this.productoService.actualizar(this.publicId, request),
        );

        this.exitoEdicion.set(
          `"${actualizado.nombre}" se actualizó correctamente.`,
        );
        this.editando.set(false);
        this.producto.reload();
      } catch {
        this.errorEdicion.set(
          'No se pudo actualizar el producto. Intentá nuevamente.',
        );
      } finally {
        this.guardandoEdicion.set(false);
      }
    });
  }

  protected async cambiarDisponibilidad(disponible: boolean): Promise<void> {
    if (!this.puedeGestionar()) return;

    this.errorDisponibilidad.set(null);
    this.actualizandoDisponibilidad.set(true);

    try {
      await firstValueFrom(
        disponible
          ? this.productoService.reactivar(this.publicId)
          : this.productoService.borrar(this.publicId),
      );
      this.producto.reload();
    } catch {
      this.errorDisponibilidad.set(
        'No se pudo cambiar la disponibilidad del producto. Intentá nuevamente.',
      );
    } finally {
      this.actualizandoDisponibilidad.set(false);
    }
  }
}

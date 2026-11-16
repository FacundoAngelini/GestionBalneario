import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
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
import { firstValueFrom } from 'rxjs';
import { CategoriaProducto, ProductoRequest } from '../../data-access/producto-model';
import { ProductoService } from '../../data-access/producto-service';

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
  imports: [FormField],
  selector: 'app-cargar-producto',
  styleUrl: './cargar-producto.css',
  templateUrl: './cargar-producto.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CargarProducto {
  private readonly productoService = inject(ProductoService);

  protected readonly categorias = CATEGORIAS;
  protected readonly guardando = signal(false);
  protected readonly exito = signal<string | null>(null);
  protected readonly errorServidor = signal<string | null>(null);

  private readonly modelo = signal<ProductoFormModel>(nuevoProducto());

  protected readonly productoForm = form(this.modelo, (p) => {
    required(p.nombre, { message: 'El nombre es obligatorio.' });
    minLength(p.nombre, 2, { message: 'El nombre debe tener al menos 2 caracteres.' });
    maxLength(p.nombre, 100, { message: 'El nombre no puede superar los 100 caracteres.' });
    validate(p.nombre, ({ value }) =>
      value().length > 0 && value().trim().length === 0
        ? { kind: 'blank', message: 'El nombre no puede estar vacío.' }
        : undefined,
    );

    required(p.precio, { message: 'El precio es obligatorio.' });
    min(p.precio, 0.01, { message: 'El precio debe ser mayor a 0.' });

    required(p.categoria, { message: 'Seleccioná una categoría.' });
  });

  protected guardar(event: Event): void {
    event.preventDefault();
    this.exito.set(null);
    this.errorServidor.set(null);

    void submit(this.productoForm, async () => {
      const { nombre, precio, categoria, productoDisponible } = this.modelo();
      const request: ProductoRequest = {
        nombre: nombre.trim(),
        precio: precio as number,
        categoria: categoria as CategoriaProducto,
        productoDisponible,
      };

      this.guardando.set(true);
      try {
        const creado = await firstValueFrom(this.productoService.crear(request));
        this.exito.set(`"${creado.nombre}" se agregó al menú.`);
        this.productoForm().reset(nuevoProducto());
      } catch {
        this.errorServidor.set('No se pudo guardar el producto. Intentá nuevamente.');
      } finally {
        this.guardando.set(false);
      }
    });
  }
}

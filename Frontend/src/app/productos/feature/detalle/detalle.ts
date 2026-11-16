import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { firstValueFrom, map } from 'rxjs';
import { AuthClient } from '../../../auth/data-access/auth-client';
import { ETIQUETA_CATEGORIA, formatoPrecio } from '../../data-access/producto-etiquetas';
import { ProductoService } from '../../data-access/producto-service';

@Component({
  imports: [RouterLink],
  selector: 'app-detalle',
  styleUrl: './detalle.css',
  templateUrl: './detalle.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Detalle {
  private readonly auth = inject(AuthClient);
  private readonly productoService = inject(ProductoService);
  private readonly publicId = inject(ActivatedRoute).snapshot.paramMap.get('id') ?? '';

  protected readonly etiquetaCategoria = ETIQUETA_CATEGORIA;
  protected readonly formatoPrecio = formatoPrecio;
  protected readonly esAdmin = this.auth.esAdmin;
  protected readonly actualizandoDisponibilidad = signal(false);
  protected readonly errorDisponibilidad = signal<string | null>(null);

  protected readonly producto = rxResource({
    stream: () =>
      this.auth.esAdmin()
        ? this.productoService.obtener(this.publicId)
        : this.productoService.disponibles().pipe(
            map((productos) => {
              const producto = productos.find((item) => item.publicId === this.publicId);
              if (!producto) throw new Error('El producto no está disponible.');
              return producto;
            }),
          ),
  });

  protected async cambiarDisponibilidad(disponible: boolean): Promise<void> {
    if (!this.auth.esAdmin()) return;

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

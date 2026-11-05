import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
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
  private readonly productoService = inject(ProductoService);
  private readonly publicId = inject(ActivatedRoute).snapshot.paramMap.get('id') ?? '';

  protected readonly etiquetaCategoria = ETIQUETA_CATEGORIA;
  protected readonly formatoPrecio = formatoPrecio;

  protected readonly producto = rxResource({
    stream: () => this.productoService.obtener(this.publicId),
  });
}

import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { environment } from '../../core/environments/environment';
import { FiltrosProducto, Producto, ProductoRequest } from './producto-model';

@Service()
export class ProductoService {
  private readonly http = inject(HttpClient);
  private readonly productosUrl = `${environment.apiUrl}/productos`;

  disponibles() {
    return this.http.get<Producto[]>(`${this.productosUrl}/disponibles`);
  }

  listar(filtros: FiltrosProducto = {}) {
    let params = new HttpParams();
    const nombre = filtros.nombre?.trim();

    if (nombre) params = params.set('nombre', nombre);
    if (filtros.categoria) params = params.set('categoria', filtros.categoria);
    if (filtros.disponible !== undefined) {
      params = params.set('disponible', filtros.disponible);
    }

    return this.http.get<Producto[]>(this.productosUrl, { params });
  }

  obtener(publicId: string) {
    return this.http.get<Producto>(`${this.productosUrl}/${publicId}`);
  }

  crear(producto: ProductoRequest) {
    return this.http.post<Producto>(this.productosUrl, producto);
  }

  actualizar(publicId: string, producto: ProductoRequest) {
    return this.http.put<Producto>(`${this.productosUrl}/${publicId}`, producto);
  }

  borrar(publicId: string) {
    return this.http.delete<void>(`${this.productosUrl}/${publicId}`);
  }

  reactivar(publicId: string) {
    return this.http.patch<void>(`${this.productosUrl}/${publicId}/reactivar`, {});
  }
}
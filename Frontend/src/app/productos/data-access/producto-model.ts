export type CategoriaProducto =
  | 'BEBIDAS_FRIAS'
  | 'BEBIDAS_CALIENTES'
  | 'COMIDA_CALIENTE'
  | 'COMIDA_FRIA'
  | 'ALMUERZO'
  | 'MERIENDA'
  | 'CENA'
  | 'DESAYUNO'
  | 'TRAGOS'
  | 'POSTRES'
  | 'ENSALADAS'
  | 'GUARNICION';

export interface Producto {
  publicId: string;
  nombre: string;
  precio: number;
  categoria: CategoriaProducto;
  productoDisponible: boolean;
}

export type ProductoRequest = Pick<
  Producto,
  'nombre' | 'precio' | 'categoria' | 'productoDisponible'
>;

export interface FiltrosProducto {
  nombre?: string;
  categoria?: CategoriaProducto;
  disponible?: boolean;
}

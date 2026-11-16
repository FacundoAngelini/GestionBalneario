import { CategoriaProducto } from './producto-model';

export const ETIQUETA_CATEGORIA: Record<CategoriaProducto, string> = {
  DESAYUNO: 'Desayuno',
  ALMUERZO: 'Almuerzo',
  MERIENDA: 'Merienda',
  CENA: 'Cena',
  COMIDA_CALIENTE: 'Comida caliente',
  COMIDA_FRIA: 'Comida fría',
  ENSALADAS: 'Ensaladas',
  GUARNICION: 'Guarnición',
  POSTRES: 'Postres',
  BEBIDAS_FRIAS: 'Bebidas frías',
  BEBIDAS_CALIENTES: 'Bebidas calientes',
  TRAGOS: 'Tragos',
};

const formatoPesos = new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS' });

export const formatoPrecio = (valor: number) => formatoPesos.format(valor);

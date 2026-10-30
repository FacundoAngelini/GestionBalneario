export const formatoPrecio = (n: number): string =>
  n.toLocaleString('es-AR', { style: 'currency', currency: 'ARS' });
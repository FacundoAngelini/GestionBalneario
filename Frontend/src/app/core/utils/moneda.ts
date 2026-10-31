export const formatoPrecio = (n: number): string =>
  n.toLocaleString('es-AR', { style: 'currency', currency: 'ARS' });

/**
 * Interpreta lo que se escribe en un campo de precio.
 * Devuelve el texto con puntos de mil ("1234567" -> "1.234.567"; la coma es el decimal)
 * y el número real (null si el campo quedó vacío).
 */
export function interpretarPrecio(entrada: string): { texto: string; valor: number | null } {
  const negativo = entrada.trim().startsWith('-');

  // Nos quedamos solo con dígitos y la coma decimal
  const limpio = entrada.replace(/[^\d,]/g, '');
  const tieneComa = limpio.includes(',');
  const [enteros = '', ...resto] = limpio.split(',');
  const decimales = resto.join('').slice(0, 2);
  const sinCeros = enteros.replace(/^0+(?=\d)/, '');
  const base = sinCeros || (tieneComa ? '0' : '');

  let texto = base.replace(/\B(?=(\d{3})+(?!\d))/g, '.') + (tieneComa ? ',' + decimales : '');
  if (negativo) texto = '-' + texto;

  const hayDigitos = base !== '' || decimales !== '';
  const valor = hayDigitos ? Number(`${negativo ? '-' : ''}${base || '0'}.${decimales || '0'}`) : null;
  return { texto, valor };
}

/** Un monto como se escribe en el campo: 18000 -> "18.000". */
export const formatoNumero = (n: number): string => interpretarPrecio(String(n).replace('.', ',')).texto;
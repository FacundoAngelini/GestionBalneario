export const esNumero = (n: unknown): n is number => typeof n === 'number' && Number.isFinite(n);

// Valida un entero dentro de un rango; devuelve el mensaje de error o null si esta bien
export const errorEntero = (
  valor: unknown,
  min: number,
  max: number,
  mensajeFuera: string,
  mensajeVacio: string,
): string | null => {
  if (!esNumero(valor)) return mensajeVacio;
  if (!Number.isInteger(valor)) return 'Ingresá un número entero';
  return valor < min || valor > max ? mensajeFuera : null;
};
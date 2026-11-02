// Límites de negocio de los recursos (el backend aplica los mismos)
export const MAX_PASILLO = 20;
export const MIN_CAPACIDAD = 2;
export const MAX_CAPACIDAD = 8;
export const MAX_NUMERO = 10_000;
export const MAX_NOMBRE = 25; 
// El nombre que arma la carga masiva: nombre base + espacio + número (hasta 5 dígitos) = 31
export const MAX_NOMBRE_RECURSO = MAX_NOMBRE + 1 + String(MAX_NUMERO).length;
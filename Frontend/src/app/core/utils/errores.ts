import { HttpErrorResponse } from '@angular/common/http';

//Muestra mensajes no tecnicos.
const TECNICO = /could not|sql|constraint|exception|hibernate|jdbc|truncated|restringido|duplicate/i;

export function mensajeAmigable(e: HttpErrorResponse, porDefecto: string): string {
  if (e.status === 0) return 'No se pudo conectar con el servidor';
  const mensaje = String(e.error?.message ?? '');
  return mensaje && !TECNICO.test(mensaje) ? mensaje : porDefecto;
}
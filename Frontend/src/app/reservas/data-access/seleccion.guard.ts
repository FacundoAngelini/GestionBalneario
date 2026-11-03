import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { ROUTE_SEGMENTS } from '../../core/constants/route-segments.constants';
import { SeleccionReserva } from './seleccion-reserva';

// La pantalla de confirmación solo tiene sentido si hay una búsqueda y algo elegido
export const seleccionGuard: CanActivateFn = () => {
  const seleccion = inject(SeleccionReserva);
  const router = inject(Router);
  return seleccion.periodo() !== null && seleccion.puedeContinuar()
    ? true
    : router.createUrlTree([`/${ROUTE_SEGMENTS.catalogo}`]);
};

// La confirmación del mostrador necesita un cliente, una búsqueda y algo elegido
export const mostradorGuard: CanActivateFn = () => {
  const seleccion = inject(SeleccionReserva);
  const router = inject(Router);
  if (seleccion.clienteMostrador() === null) {
    return router.createUrlTree([`/${ROUTE_SEGMENTS.reservas}/${ROUTE_SEGMENTS.presencial}`]);
  }
  return seleccion.periodo() !== null && seleccion.puedeContinuar()
    ? true
    : router.createUrlTree([`/${ROUTE_SEGMENTS.catalogo}`]);
};
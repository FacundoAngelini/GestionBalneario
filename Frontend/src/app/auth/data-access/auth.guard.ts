import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { AuthClient } from "./auth-client";
import { ROUTE_SEGMENTS } from "../../core/constanst/route-segments.constants";

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthClient);
  const router = inject(Router);
  return authService.usuarioLogueado() ? true : router.createUrlTree([`/${ROUTE_SEGMENTS.auth}/${ROUTE_SEGMENTS.login}`]);
};

export const guestGuard: CanActivateFn = () => {
  const authService = inject(AuthClient);
  const router = inject(Router);
  return !authService.usuarioLogueado() ? true : router.createUrlTree([`/${ROUTE_SEGMENTS.inicio}`])
};

export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthClient);
  const router = inject(Router);
  
  const rolesPermitidos = route.data?.['roles'] as Array<string>;
  const rolesUsuario = authService.roles();

  // Verifica si hay alguna coincidencia entre los roles del usuario y los permitidos
  const tienePermiso = rolesPermitidos.some(rol => rolesUsuario.includes(rol));

  if (tienePermiso) {
    return true;
  }

  return router.createUrlTree([`/${ROUTE_SEGMENTS.unauthorized}`]);
};
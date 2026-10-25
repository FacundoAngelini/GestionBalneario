import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { AuthClient } from "./auth-client";
import { ROUTE_SEGMENTS } from "../../core/constants/route-segments.constants";

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthClient);
  const router = inject(Router);
  return authService.usuarioLogueado() ? true : router.createUrlTree([`/${ROUTE_SEGMENTS.auth}/${ROUTE_SEGMENTS.login}`]);
};

export const guestGuard: CanActivateFn = () => {
  const authService = inject(AuthClient);
  const router = inject(Router);
  return !authService.usuarioLogueado() ? true : router.createUrlTree([`/${ROUTE_SEGMENTS.panel}`])
};

export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthClient);
  const router = inject(Router);
  
  const rolesPermitidos = (route.data?.['roles'] as string[] | undefined) ?? [];
  const rolesUsuario = authService.roles();

  const tienePermiso = rolesPermitidos.some(rol => rolesUsuario.includes(rol));

  if (tienePermiso) {
    return true;
  }

  return router.createUrlTree([`/${ROUTE_SEGMENTS.unauthorized}`]);
};
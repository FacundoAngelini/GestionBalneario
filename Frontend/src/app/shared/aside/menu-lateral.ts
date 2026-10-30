import { computed, inject, Service } from '@angular/core';
import { AuthClient } from '../../auth/data-access/auth-client';
import { ROLES } from '../../auth/data-access/roles.constants';
import { ROUTE_SEGMENTS } from '../../core/constants/route-segments.constants';

export interface ItemMenu {
  etiqueta: string;
  icono: string;
  ruta: string;
  roles: readonly string[];
}

// Cada fase suma su ítem cuando su ruta ya existe
const ITEMS_MENU: readonly ItemMenu[] = [
  {
    etiqueta: 'Carga masiva',
    icono: 'upload_file',
    ruta: `/${ROUTE_SEGMENTS.recursos}/${ROUTE_SEGMENTS.cargaMasiva}`,
    roles: [ROLES.ADMIN],
  },
];

@Service()
export class MenuLateral {
  private readonly auth = inject(AuthClient);

  // Solo los ítems que el usuario logueado puede ver
  readonly items = computed(() => ITEMS_MENU.filter((item) => this.auth.tieneAlgunRol(item.roles)));
  readonly hayItems = computed(() => this.items().length > 0);
}
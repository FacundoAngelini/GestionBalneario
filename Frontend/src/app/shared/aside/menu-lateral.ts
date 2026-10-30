import { computed, inject, Service, Signal } from '@angular/core';
import { AuthClient } from '../../auth/data-access/auth-client';
import { ROLES_PERSONAL } from '../../auth/data-access/roles.constants';
import { ROUTE_SEGMENTS } from '../../core/constants/route-segments.constants';
import { PendientesReservas } from '../../reservas/data-access/pendientes-reservas';

export interface ItemMenu {
  etiqueta: string;
  icono: string;
  ruta: string;
  roles: readonly string[];
  insignia?: Signal<number>; // número que se muestra al lado (por ejemplo, pendientes)
}

@Service()
export class MenuLateral {
  private readonly auth = inject(AuthClient);
  private readonly pendientes = inject(PendientesReservas);

  private readonly todos: readonly ItemMenu[] = [
    {
      etiqueta: 'Gestión de reservas',
      icono: 'event_note',
      ruta: `/${ROUTE_SEGMENTS.reservas}/${ROUTE_SEGMENTS.gestionReservas}`,
      roles: ROLES_PERSONAL,
      insignia: this.pendientes.cantidad,
    },
    {
      etiqueta: 'Inventario',
      icono: 'inventory_2',
      ruta: `/${ROUTE_SEGMENTS.recursos}`,
      roles: ROLES_PERSONAL,
    },
  ];

  // Solo los ítems que el usuario logueado puede ver
  readonly items = computed(() => this.todos.filter((item) => this.auth.tieneAlgunRol(item.roles)));
  readonly hayItems = computed(() => this.items().length > 0);
}
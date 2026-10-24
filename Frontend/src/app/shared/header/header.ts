import { Component, computed, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { AuthClient } from '../../auth/data-access/auth-client';
import { ETIQUETA_ROL } from '../../auth/data-access/auth-model';
import { ROUTE_SEGMENTS } from '../../core/constanst/route-segments.constants';

@Component({
  imports: [MatButtonModule, MatIconModule],
  selector: 'app-header',
  styleUrl: './header.css',
  templateUrl: './header.html',
})
export class Header {
  private readonly auth = inject(AuthClient);
  private readonly router = inject(Router);

  protected readonly nombreUsuario = this.auth.nombreUsuario;
  protected readonly rol = computed(() => ETIQUETA_ROL[this.auth.roles()[0]] ?? '');

  protected cerrarSesion() {
    this.auth.logout();
    this.router.navigateByUrl(`/${ROUTE_SEGMENTS.auth}/${ROUTE_SEGMENTS.login}`);
  }
}

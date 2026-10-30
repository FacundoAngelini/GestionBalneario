import { CdkTrapFocus } from '@angular/cdk/a11y';
import {
  afterRenderEffect,
  Component,
  computed,
  effect,
  ElementRef,
  inject,
  signal,
  viewChild,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterLink } from '@angular/router';
import { AuthClient } from '../../auth/data-access/auth-client';
import { ETIQUETA_ROL } from '../../auth/data-access/auth-model';
import { PendientesReservas } from '../../reservas/data-access/pendientes-reservas';

@Component({
  imports: [CdkTrapFocus, MatButtonModule, MatIconModule, RouterLink],
  selector: 'app-header',
  styleUrl: './header.css',
  templateUrl: './header.html',
  host: {
    '(document:keydown.escape)': 'cerrarMenuPerfil()',
  },
})
export class Header {
  private readonly auth = inject(AuthClient);
  private readonly router = inject(Router);

  protected readonly logueado = this.auth.usuarioLogueado;
  protected readonly nombreUsuario = this.auth.nombreUsuario;
  protected readonly rol = this.auth.etiquetaRol;
  protected readonly esCliente = this.auth.esCliente;
  protected readonly esPersonal = this.auth.esPersonal;
  protected readonly pendientes = inject(PendientesReservas).cantidad;
    protected readonly etiquetaCatalogo = computed(() =>
    this.auth.esCliente() ? 'Reservar' : 'Ver recursos',
  );

  protected readonly menuPerfilAbierto = signal(false);

  private readonly botonPerfil = viewChild('botonPerfil', { read: ElementRef });
  private readonly botonCerrarPanel = viewChild('botonCerrarPanel', {
    read: ElementRef,
  });

  constructor() {
    // Al abrir el panel, el foco pasa al botón de cerrar.
    afterRenderEffect(() => {
      if (this.menuPerfilAbierto()) {
        this.botonCerrarPanel()?.nativeElement.focus();
      }
    });

    // Si la sesión termina con el panel abierto (ej. token vencido), se cierra.
    effect(() => {
      if (!this.logueado()) {
        this.menuPerfilAbierto.set(false);
      }
    });
  }

  protected alternarMenuPerfil(): void {
    this.menuPerfilAbierto.update((abierto) => !abierto);
  }

  protected cerrarMenuPerfil(devolverFoco = true): void {
    if (!this.menuPerfilAbierto()) return;

    this.menuPerfilAbierto.set(false);

    if (devolverFoco) {
      this.botonPerfil()?.nativeElement.focus();
    }
  }

  protected cerrarSesion(): void {
    this.auth.logout();
    this.cerrarMenuPerfil(false);
    this.router.navigateByUrl('/');
  }
}
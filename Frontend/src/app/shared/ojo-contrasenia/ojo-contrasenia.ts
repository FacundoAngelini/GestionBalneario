import { Component, input, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-ojo-contrasenia',
  imports: [MatButtonModule, MatIconModule],
  template: `
    <button
      matIconButton
      type="button"
      [disabled]="deshabilitado()"
      [attr.aria-label]="'Mostrar ' + etiqueta()"
      [attr.aria-pressed]="visible()"
      (click)="visible.set(!visible())"
    >
      <mat-icon>{{ visible() ? 'visibility_off' : 'visibility' }}</mat-icon>
    </button>
  `,
})
export class OjoContrasenia {
  readonly etiqueta = input('contraseña');
  readonly deshabilitado = input(false);
  readonly visible = signal(false);
}
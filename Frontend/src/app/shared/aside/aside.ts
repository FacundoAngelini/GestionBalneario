import { Component, inject, output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MenuLateral } from './menu-lateral';

@Component({
  imports: [MatIconModule, RouterLink, RouterLinkActive],
  selector: 'app-aside',
  styleUrl: './aside.css',
  templateUrl: './aside.html',
})
export class Aside {
  protected readonly items = inject(MenuLateral).items;

  // Avisa al layout que se navego, para cerrar el menu en el celular
  readonly navegar = output<void>();
}
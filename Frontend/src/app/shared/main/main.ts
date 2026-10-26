import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from '../header/header';
import { Aside } from '../aside/aside';
import { Footer } from '../footer/footer';

@Component({
  selector: 'app-main-layout',
  imports: [RouterOutlet, Header, Aside, Footer],
  templateUrl: './main.html',
  styleUrl: './main.css'
})
export class Main {
  //controla para el celular
  menuAbierto = signal(false);

  toggleMenu() {
    this.menuAbierto.update(estado => !estado);
  }
}

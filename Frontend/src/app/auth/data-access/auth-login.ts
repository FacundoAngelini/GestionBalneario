import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthClient } from './auth-client';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="auth-container" style="max-width: 400px; margin: 50px auto; text-align: center;">
      <h2>Ingresar a ArenaDesk</h2>
      @if (errorMsg()) { <p style="color: red;">{{ errorMsg() }}</p> }
      
      <form (ngSubmit)="login()" style="display: flex; flex-direction: column; gap: 15px;">
        <input type="text" [(ngModel)]="username" name="username" placeholder="Usuario" required />
        <input type="password" [(ngModel)]="password" name="password" placeholder="Contraseña" required />
        <button type="submit" [disabled]="cargando()">Entrar</button>
      </form>
      <p>¿No tienes cuenta? <a routerLink="/auth/registro">Regístrate</a></p>
    </div>
  `
})
export class Login {
  private authService = inject(AuthClient);
  private router = inject(Router);

  username = '';
  password = '';
  errorMsg = signal<string>('');
  cargando = signal<boolean>(false);

  login() {
    this.cargando.set(true);
    this.authService.login({ nombreUsuario: this.username, contrasenia: this.password }).subscribe({
      next: () => {
        // Redirigir al dashboard/home una vez logueado. Como no existe, mandamos a '/'
        this.router.navigate(['/']); 
      },
      error: () => {
        this.errorMsg.set('Credenciales incorrectas');
        this.cargando.set(false);
      }
    });
  }
}
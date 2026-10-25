
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Component, inject, Inject, signal } from '@angular/core';
import { AuthClient } from '../data-access/auth-client';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="auth-container" style="max-width: 400px; margin: 50px auto; text-align: center;">
      <h2>Crear Cuenta Cliente</h2>
      @if (errorMsg()) { <p style="color: red;">{{ errorMsg() }}</p> }
      
      <form (ngSubmit)="registrar()" style="display: flex; flex-direction: column; gap: 10px;">
        <input type="text" [(ngModel)]="datos.nombre" name="nombre" placeholder="Nombre" required />
        <input type="text" [(ngModel)]="datos.apellido" name="apellido" placeholder="Apellido" required />
        <input type="number" [(ngModel)]="datos.dni" name="dni" placeholder="DNI" required />
        <input type="email" [(ngModel)]="datos.email" name="email" placeholder="Email" required />
        <input type="text" [(ngModel)]="datos.telefono" name="telefono" placeholder="Teléfono" required />
        <hr>
        <input type="text" [(ngModel)]="datos.nombreUsuario" name="usuario" placeholder="Nombre de Usuario" required />
        <input type="password" [(ngModel)]="datos.contrasenia" name="pass" placeholder="Contraseña (ej. Clave123!)" required />
        
        <button type="submit" [disabled]="cargando()">Registrarme</button>
      </form>
      <p>¿Ya tienes cuenta? <a routerLink="/auth/login">Ingresa aquí</a></p>
    </div>
  `
})
export class Registro {
  private authService = inject(AuthClient);
  private router = inject(Router);
  datos = {
    nombreUsuario: '', contrasenia: '', nombre: '', apellido: '', email: '', telefono: '', dni: 0
  };
  
  errorMsg = signal<string>('');
  cargando = signal<boolean>(false);

  registrar() {
    this.cargando.set(true);
    this.authService.registro(this.datos).subscribe({
      next: () => {
        alert('Cuenta creada con éxito. Ahora puedes iniciar sesión.');
        this.router.navigate(['/auth/login']);
      },
      error: (err) => {
        this.errorMsg.set('Error al registrar. Verifica los datos.');
        this.cargando.set(false);
      }
    });
  }
}
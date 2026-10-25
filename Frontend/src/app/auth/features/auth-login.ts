import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthClient } from '../data-access/auth-client';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './auth-login.html',
  styleUrls: ['./auth-login.css']
})
export class Login {
  private authService = inject(AuthClient);
  private router = inject(Router);

  credenciales = { nombreUsuario: '', contrasenia: '' };
  errorMsg = signal<string>('');
  cargando = signal<boolean>(false);
  ingresar() {
  this.cargando.set(true);
  this.authService.login(this.credenciales).subscribe({
    next: () => {
      const roles = this.authService.obtenerRolesUsuario();
      
    
      if (roles.includes('ROLE_CLIENTE')) {
        this.router.navigate(['/reservas']); 
      } else if (roles.includes('ROLE_MOZO') || roles.includes('ROLE_COCINERO')) {
        this.router.navigate(['/pedidos']);
      } else if (roles.includes('ROLE_ADMIN') || roles.includes('ROLE_GERENTE')) {
        this.router.navigate(['/empleados']);
      } else {
        this.router.navigate(['/']); 
      }
    },
    error: () => {
      this.errorMsg.set('Credenciales incorrectas');
      this.cargando.set(false);
    }
  });
}
}
import { Component } from "@angular/core";
import { RouterOutlet } from "@angular/router";

@Component({
  selector: 'app-auth-layout',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="min-h-screen bg-gray-100 flex items-center justify-center">
      <div class="w-full max-w-md bg-white rounded-lg shadow-md p-8">
        <h1 class="text-2xl font-bold text-center text-blue-900 mb-6">ArenaDesk</h1>
        <router-outlet></router-outlet>
      </div>
    </div>
  `
})
export class AuthLayout {}
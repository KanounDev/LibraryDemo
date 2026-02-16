// src/app/app.ts  (or app.component.ts if you kept the name)
import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterOutlet],
  template: `
    <div class="container">
      <h1>Library Management System</h1>
      <p>Welcome to the demo application!</p>

      <div class="actions">
        <button routerLink="/login">Login</button>
        <button routerLink="/register">Register</button>
      </div>

      <!-- This is where routed components will appear -->
      <router-outlet></router-outlet>
    </div>
  `,
  styles: []
})
export class App {
  // You can remove the signal if you don't need it
}
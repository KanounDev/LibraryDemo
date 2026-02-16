// src/app/home/home.ts  (or home.component.ts)
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div style="text-align: center; padding: 60px;">
      <h1>Welcome to the Library Dashboard</h1>
      <p>You are now logged in!</p>

      <p>Role: {{ role }}</p>
      <p *ngIf="branch">Branch: {{ branch }}</p>

      <button (click)="logout()">Logout</button>
    </div>
  `,
  styles: [`
    h1 { color: #3f51b5; }
    button {
      margin-top: 30px;
      padding: 12px 28px;
      background: #f44336;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }
  `]
})
export class Home {
  role: string | null = null;
  branch: string | null = null;

  constructor(private authService: AuthService, private router: Router) {
    this.role = this.authService.getRole();
    this.branch = this.authService.getBranch();  // if you have getBranch() method
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

// This component handles the login form.
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  template: `
    <!-- Simple form HTML. -->
    <form (ngSubmit)="login()">
  <input [(ngModel)]="user.username" name="username" placeholder="Username" required>  <!-- Add name="username" -->
  <input [(ngModel)]="user.password" name="password" type="password" placeholder="Password" required>  <!-- Add name="password" -->
  <select [(ngModel)]="user.role" name="role" (change)="updateForm()">  <!-- Add name="role" -->
    <option value="MEMBER">Member</option>
    <option value="LIBRARIAN">Librarian</option>
    <option value="ADMIN">Admin</option>
  </select>
  <input *ngIf="showBranch" [(ngModel)]="user.branch" name="branch" placeholder="Branch" required>  <!-- Add name="branch" -->
  <button type="submit">Login</button>
</form>
  `,
})
export class LoginComponent {
  // User object for form.
  user = {
    username: '',
    password: '',
    branch: '',          // Branch field.
    role: 'MEMBER'       // Default role.
  };

  showBranch = false;    // Hide branch field initially.

  constructor(private authService: AuthService, private router: Router) { }

  // Updates form: Show branch if not MEMBER.
  updateForm() {
    this.showBranch = this.user.role !== 'MEMBER';
  }

  // Login function: Called on submit.
  login() {
    const payload: any = {
      username: this.user.username,
      password: this.user.password
    };
    if (this.showBranch) {
      payload.branch = this.user.branch;
    }

    this.authService.login(payload).subscribe({
      next: (response: any) => {
        this.authService.saveToken(response.token);
        this.router.navigate(['/home']);          // ← changed here
      },
      error: (err) => {
        console.error('Login failed', err);
        alert(err.error?.error || 'Invalid credentials or branch');
      }
    });
  }
}
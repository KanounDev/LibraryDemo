import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

// This component handles the registration form.
@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule],
  template: `
    <!-- Simple form HTML (add your own CSS). -->
    <form (ngSubmit)="register()">
  <input [(ngModel)]="user.username" name="username" placeholder="Username" required>  <!-- Add name="username" -->
  <input [(ngModel)]="user.password" name="password" type="password" placeholder="Password" required>  <!-- Add name="password" -->
  <select [(ngModel)]="user.role" name="role">  <!-- Add name="role" -->
    <option value="MEMBER">Member</option>
    <option value="LIBRARIAN">Librarian</option>
    <option value="ADMIN">Admin</option>
  </select>
  <input [(ngModel)]="user.branch" name="branch" placeholder="Branch (if Librarian/Admin)">  <!-- Add name="branch" -->
  <button type="submit">Register</button>
</form>
  `,
})
export class RegisterComponent {
  // User object bound to form.
  user = { username: '', password: '', role: 'MEMBER', branch: '' };  // Default to MEMBER.

  constructor(private authService: AuthService, private router: Router) {}

  // Register function: Called on form submit.
  register() {
    // Call service to register.
    this.authService.register(this.user).subscribe({
      next: (response: any) => {
        // Save token and redirect to home.
        this.authService.saveToken(response.token);
        this.router.navigate(['/']);
      },
      error: (err) => {
        // Log error.
        console.error('Registration failed', err);
        let errorMsg = 'Registration failed. Please try again.';
        
        // Handle specific errors (e.g., from backend).
        if (err.status === 400 && err.error?.error) {
          errorMsg = err.error.error; // e.g., "Branch is required..."
        }
        
        alert(errorMsg); // Show alert (or use a toast).
      }
    });
  }
}
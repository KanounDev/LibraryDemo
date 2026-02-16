import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// This service handles auth API calls and token storage.
@Injectable({ providedIn: 'root' })
export class AuthService {
  // Base API URL (matches backend).
  private apiUrl = 'http://localhost:8081/api/auth';

  constructor(private http: HttpClient) { }

  // Registers a user: Sends User object, returns token.
  register(user: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/register`, user);
  }

  // Logs in: Sends credentials, returns token.
  login(user: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, user);
  }

  // Saves token to localStorage.
  saveToken(token: string) {
    localStorage.setItem('token', token);
  }

  // Gets token from storage.
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // Checks if logged in.
  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  // Cache for role and branch.
  private _role: string | null = null;
  private _branch: string | null = null;  // NEW: Similar to municipality.

  // Gets role from JWT payload (caches for performance).
  getRole(): string | null {
    if (this._role !== null) return this._role;  // Cache hit.

    const token = this.getToken();
    if (!token) return null;

    try {
      // Decode JWT payload (base64).
      const payload = JSON.parse(atob(token.split('.')[1]));
      console.log('JWT payload (first read):', payload);
      this._role = payload.role || null; // Extract role.
      this._branch = payload.branch || null; // Extract branch.
      return this._role;
    } catch (e) {
      console.error('JWT decode error:', e);
      return null;
    }
  }

  // Gets branch from JWT (uses getRole to trigger extraction if needed).
  getBranch(): string | null {
    if (this._branch !== null) return this._branch;
    this.getRole();  // Triggers cache if not set.
    return this._branch;
  }

  // Gets full current user from JWT.
  getCurrentUser(): any {
    const token = this.getToken();
    if (!token) return null;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload;
    } catch (e) {
      console.error('JWT decode error:', e);
      return null;
    }
  }

  // Gets user ID from JWT.
  getCurrentUserId(): string | null {
    const user = this.getCurrentUser();
    return user?.id || null;
  }

  // Logs out: Clears token and cache.
  logout() {
    localStorage.removeItem('token');
    this._role = null;  // Reset role cache.
    this._branch = null;  // Reset branch cache.
  }
}
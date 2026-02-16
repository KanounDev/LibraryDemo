import { Routes } from '@angular/router';
import { LoginComponent } from './login/login';
import { RegisterComponent } from './register/register';
import { Home } from './home/home';  // ← new import
import { authGuard } from './auth-guard';
export const routes: Routes = [
    { path: '', redirectTo: '/login', pathMatch: 'full' },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'home', component: Home ,canActivate: [authGuard]},              // ← new route
    // Optional: catch-all
    { path: '**', redirectTo: '/login' }
];
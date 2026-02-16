// src/main.ts
import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

import { App } from './app/app';
import { routes } from './app/app.routes';   // ← import here

bootstrapApplication(App, {
  providers: [
    provideRouter(routes),          // ← this activates routing
    provideHttpClient(),            // needed for HttpClient (AuthService)
    // add more providers later if needed (e.g. interceptors)
  ]
})
.catch(err => console.error(err));
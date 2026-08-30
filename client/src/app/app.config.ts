import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { includeBearerTokenInterceptor } from 'keycloak-angular';
import { provideStore } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { provideStoreDevtools } from '@ngrx/store-devtools';

import { routes } from './app.routes';
import { provideAppKeycloak } from './core/config/keycloak.config';
import { errorInterceptor } from './core/interceptors/error.interceptor';
import { environment } from '../environments/environment';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withComponentInputBinding()),
    provideAppKeycloak(),
    // Order matters: includeBearerTokenInterceptor attaches the token first,
    // errorInterceptor unwraps failures on the way back out.
    provideHttpClient(withInterceptors([includeBearerTokenInterceptor, errorInterceptor])),
    // Root store is intentionally empty at scaffold time — the student and
    // admin shells register their own feature slices via provideState()/
    // provideEffects() in their own route-level providers.
    provideStore(),
    provideEffects(),
    provideStoreDevtools({ maxAge: 25, logOnly: environment.production }),
  ],
};

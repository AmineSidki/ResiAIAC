import {
  provideKeycloak,
  withAutoRefreshToken,
  INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
  AutoRefreshTokenService,
  UserActivityService,
} from 'keycloak-angular';
import { EnvironmentProviders } from '@angular/core';
import { environment } from '../../../environments/environment';

/**
 * Keycloak setup for ResiAIAC.
 *
 * - Redirect flow only (SPA calls keycloak.login(), which sends the browser
 *   to Keycloak's hosted login page) — NOT direct grant/ROPC. `onLoad:
 *   'check-sso'` means the app boots without forcing a login redirect for
 *   anonymous visits (public routes still render); protected routes force
 *   the redirect themselves via the guards in core/auth.
 * - withAutoRefreshToken: tracks user activity and calls
 *   Keycloak.updateToken on the adapter's TokenExpired event — this is our
 *   "silent refresh" (no manual iframe wiring needed with keycloak-angular
 *   v19's functional API).
 * - INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG scopes the Authorization header
 *   to calls against our own API only, so the token is never attached to
 *   third-party requests.
 */
export function provideAppKeycloak(): EnvironmentProviders {
  return provideKeycloak({
    config: {
      url: environment.keycloak.url,
      realm: environment.keycloak.realm,
      clientId: environment.keycloak.clientId,
    },
    initOptions: {
      onLoad: 'check-sso',
      silentCheckSsoRedirectUri: `${window.location.origin}/silent-check-sso.html`,
      pkceMethod: 'S256',
      // Disables keycloak-js's background 3rd-party-cookie iframe check.
      // That check hangs/times out across our proxy chain (and increasingly
      // in browsers that block 3rd-party cookies/frames by default) —
      // it only gates periodic session-status polling, not login itself,
      // and withAutoRefreshToken already covers token refresh on its own.
      checkLoginIframe: false,
    },
    features: [withAutoRefreshToken({ onInactivityTimeout: 'logout', sessionTimeout: 300000 })],
    providers: [
      // withAutoRefreshToken's configure() step injects these directly; they
      // aren't providedIn: 'root' in keycloak-angular, so they must be listed
      // here explicitly or bootstrap throws NullInjectorError.
      AutoRefreshTokenService,
      UserActivityService,
      {
        provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
        useValue: [
          {
            urlPattern: new RegExp(`^${escapeRegExp(environment.apiBaseUrl)}(/.*)?$`),
          },
        ],
      },
    ],
  });
}

function escapeRegExp(value: string): string {
  return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

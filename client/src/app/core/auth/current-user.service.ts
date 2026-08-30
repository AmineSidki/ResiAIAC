import { computed, inject, Injectable, Signal } from '@angular/core';
import Keycloak from 'keycloak-js';
import { KEYCLOAK_EVENT_SIGNAL, KeycloakEventType, typeEventArgs, ReadyArgs } from 'keycloak-angular';
import { AppRole, ROLE_HIERARCHY } from '../models/enums';

/**
 * Thin wrapper over the Keycloak instance + event signal for components that
 * need to react to auth state or display "your role" in the UI — e.g. the
 * deliverable requirement to reflect the logged-in user's role in a guarded
 * view. Guards themselves use core/auth/role.guard.ts directly; this is for
 * display/UI logic, not access control.
 */
@Injectable({ providedIn: 'root' })
export class CurrentUserService {
  private readonly keycloak = inject(Keycloak);
  private readonly eventSignal = inject(KEYCLOAK_EVENT_SIGNAL);

  readonly authenticated: Signal<boolean> = computed(() => {
    const event = this.eventSignal();
    if (event.type === KeycloakEventType.Ready) {
      return typeEventArgs<ReadyArgs>(event.args);
    }
    return !!this.keycloak.authenticated;
  });

  readonly realmRoles: Signal<string[]> = computed(() => {
    this.eventSignal(); // re-evaluate on every keycloak event
    return this.keycloak.tokenParsed?.realm_access?.roles ?? [];
  });

  /** Most senior role the user holds, per the ADMINISTRATEUR > RESPONSABLE > MANAGER > ETUDIANT hierarchy. */
  readonly highestRole: Signal<AppRole | null> = computed(() => {
    const roles = this.realmRoles();
    return ROLE_HIERARCHY.find((role) => roles.includes(role)) ?? null;
  });

  readonly fullName: Signal<string | null> = computed(() => {
    this.eventSignal();
    const parsed = this.keycloak.tokenParsed as { given_name?: string; family_name?: string } | undefined;
    if (!parsed?.given_name && !parsed?.family_name) return null;
    return [parsed.given_name, parsed.family_name].filter(Boolean).join(' ');
  });

  login(): void {
    this.keycloak.login();
  }

  logout(): void {
    this.keycloak.logout({ redirectUri: window.location.origin });
  }
}

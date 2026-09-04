import { createAuthGuard, AuthGuardData } from 'keycloak-angular';
import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AppRole, ROLE_HIERARCHY } from '../models/enums';

/**
 * Roles form a strict hierarchy: ADMINISTRATEUR > RESPONSABLE > MANAGER > ETUDIANT.
 * A guard gated on MANAGER must also admit RESPONSABLE and ADMINISTRATEUR —
 * this checks "does the user hold a role at least as senior as `minimum`",
 * never an exact match against a single role name.
 */
/**
 * Exported for UI-level gating (nav items, action buttons) that needs the
 * same "holds a role at least this senior" check the route guards use, but
 * isn't itself a route guard — e.g. hiding "Utilisateurs" from a pure
 * MANAGER in the admin sidebar. Guards below remain the actual access
 * control; this is for display/UX only.
 */
export function hasRoleAtLeast(realmRoles: string[], minimum: AppRole): boolean {
  const minimumRank = ROLE_HIERARCHY.indexOf(minimum);
  return realmRoles.some((role) => {
    const rank = ROLE_HIERARCHY.indexOf(role as AppRole);
    return rank !== -1 && rank <= minimumRank; // lower index = more senior
  });
}

/**
 * Anonymous visitors get sent to /unauthorized with the URL they were
 * actually headed for tucked into `?redirect=`. /unauthorized (and
 * CurrentUserService.login) thread that value through to keycloak.login's
 * redirectUri, so a successful login lands the user on the page they
 * originally wanted instead of stranding them back on /unauthorized — this
 * is the fix for the "hit 401 before logging in, stuck there forever after
 * logging in" loop.
 */
function buildRoleGuard(minimum: AppRole): CanActivateFn {
  return createAuthGuard<CanActivateFn>(async (route, state, authData: AuthGuardData) => {
    const router = inject(Router);
    const { authenticated, grantedRoles } = authData;

    if (!authenticated) {
      return router.createUrlTree(['/unauthorized'], { queryParams: { redirect: state.url } });
    }

    if (hasRoleAtLeast(grantedRoles.realmRoles, minimum)) {
      return true;
    }

    return router.parseUrl('/forbidden');
  });
}

/** Route requires ETUDIANT or above — i.e. any authenticated user. */
export const requireEtudiant: CanActivateFn = buildRoleGuard('ETUDIANT');

/** Route requires MANAGER or above. */
export const requireManager: CanActivateFn = buildRoleGuard('MANAGER');

/** Route requires RESPONSABLE or above. */
export const requireResponsable: CanActivateFn = buildRoleGuard('RESPONSABLE');

/** Route requires ADMINISTRATEUR exactly (top of the hierarchy). */
export const requireAdministrateur: CanActivateFn = buildRoleGuard('ADMINISTRATEUR');

/**
 * The student self-service shell is for ETUDIANT-and-up in the raw role
 * hierarchy sense (requireEtudiant admits everyone), but ADMINISTRATEUR
 * specifically shouldn't land there — they have their own dashboard.
 * Composed alongside requireEtudiant on the /student route rather than
 * replacing it, so the ETUDIANT floor + unauthenticated handling stay
 * exactly as before for every other role.
 */
export const blockAdministrateur: CanActivateFn = createAuthGuard<CanActivateFn>(
  async (route, state, authData: AuthGuardData) => {
    const router = inject(Router);
    const { authenticated, grantedRoles } = authData;

    if (!authenticated) return true; // let requireEtudiant handle the unauthenticated case
    if (grantedRoles.realmRoles.includes('ADMINISTRATEUR')) {
      return router.parseUrl('/admin');
    }
    return true;
  },
);

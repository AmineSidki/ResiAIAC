import { Component, computed, input } from '@angular/core';
import { AppRole } from '../../../core/models/enums';

interface RoleCopy {
  label: string;
  classes: string;
}

const ROLE_COPY: Record<AppRole, RoleCopy> = {
  ADMINISTRATEUR: { label: 'Administrateur', classes: 'bg-primary-700 text-white' },
  RESPONSABLE: { label: 'Responsable', classes: 'bg-primary-600 text-white' },
  MANAGER: { label: 'Manager', classes: 'bg-primary-100 text-primary-700 dark:bg-primary-900/40 dark:text-primary-300' },
  ETUDIANT: { label: 'Étudiant', classes: 'bg-neutral-100 text-neutral-700 dark:bg-white/10 dark:text-neutral-300' },
};

/**
 * Small pill used anywhere the app needs to surface "your role" — replaces
 * the unstyled "ROLE : <User_role>" text that used to sit in the middle of
 * the screen after login. Purely presentational; access control still lives
 * in core/auth/role.guard.ts.
 */
@Component({
  selector: 'app-role-badge',
  standalone: true,
  template: `
    @if (copy(); as c) {
      <span class="inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium" [class]="c.classes">
        {{ c.label }}
      </span>
    }
  `,
})
export class RoleBadgeComponent {
  readonly role = input<AppRole | null>(null);

  protected readonly copy = computed<RoleCopy | null>(() => {
    const role = this.role();
    return role ? ROLE_COPY[role] : null;
  });
}

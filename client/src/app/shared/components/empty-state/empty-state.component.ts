import { Component, input } from '@angular/core';

/**
 * Generic empty-state block — "nothing here yet" vs "filtered to zero" is
 * distinguished by the message the caller passes in, not by this component.
 * Added alongside SkeletonComponent as a small, flagged Phase 0 extension
 * (see skeleton.component.ts) rather than a student-shell-only fork.
 */
@Component({
  selector: 'app-empty-state',
  standalone: true,
  template: `
    <div class="flex flex-col items-center gap-2 rounded-lg border border-dashed border-neutral-300 px-6 py-10 text-center">
      <p class="text-sm font-medium text-neutral-700">{{ title() }}</p>
      @if (description()) {
        <p class="text-sm text-neutral-500">{{ description() }}</p>
      }
      <ng-content></ng-content>
    </div>
  `,
})
export class EmptyStateComponent {
  readonly title = input.required<string>();
  readonly description = input<string | null>(null);
}

import { Component, input } from '@angular/core';

export type EmptyStateReason = 'no-data' | 'filtered';

const COPY: Record<EmptyStateReason, { title: string; body: string }> = {
  'no-data': {
    title: 'Nothing here yet',
    body: "There's no data in this table yet — records will appear here once they're created.",
  },
  filtered: {
    title: 'No matches',
    body: 'No records match the current filter. Try a different filter or clear it.',
  },
};

/**
 * Deliberately separate from SkeletonRowsComponent (loading) and from a
 * plain "No results." table-cell fallback: this distinguishes "we've never
 * had data" from "your filter excluded everything that does exist".
 */
@Component({
  selector: 'app-empty-state',
  standalone: true,
  template: `
    <div class="flex flex-col items-center justify-center gap-1.5 rounded-lg border border-dashed border-neutral-300 px-6 py-12 text-center">
      <p class="text-sm font-medium text-neutral-700">{{ copy().title }}</p>
      <p class="max-w-sm text-sm text-neutral-500">{{ copy().body }}</p>
      <ng-content></ng-content>
    </div>
  `,
})
export class EmptyStateComponent {
  readonly reason = input<EmptyStateReason>('no-data');

  protected copy(): { title: string; body: string } {
    return COPY[this.reason()];
  }
}

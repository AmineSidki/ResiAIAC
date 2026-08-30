import { Component, input } from '@angular/core';

export type SkeletonVariant = 'text' | 'block' | 'circle';

/**
 * Generic pulsing placeholder. Not part of the original Phase 0 component
 * set (no skeleton/empty-state primitives existed under shared/components)
 * — added here as a small, flagged extension for Task A6 rather than
 * forking a one-off version inside the student shell. Track B can reuse it
 * as-is for its own polish task.
 */
@Component({
  selector: 'app-skeleton',
  standalone: true,
  template: `
    <div
      class="animate-pulse bg-neutral-100"
      [class.rounded-md]="variant() === 'block'"
      [class.rounded]="variant() === 'text'"
      [class.rounded-full]="variant() === 'circle'"
      [style.width]="width()"
      [style.height]="height()"
    ></div>
  `,
})
export class SkeletonComponent {
  readonly variant = input<SkeletonVariant>('text');
  readonly width = input('100%');
  readonly height = input('1rem');
}
